# Assignment 8 — Concurrency Diagnosis & Fixes

## Summary

This document analyzes the intentionally buggy `Original/BankSimulation.java`. The program simulates multiple threads withdrawing from a shared `BankAccount` and demonstrates a classic concurrency bug: a race condition caused by unsynchronized read-modify-write access to shared state. The following sections identify the root cause, explain when and why the bug occurs, show annotated code excerpts, and propose two concrete code-level fixes with trade-offs.

## Findings (diagnostic)

- Specific issue: Race condition on a shared mutable field (`balance`) leading to inconsistent final balances and sometimes negative balances.
- Location: `BankAccount.withdraw(int amount, String who)` (class `BankAccount` in `Original/BankSimulation.java`).
- Why it occurs: Multiple threads call `withdraw(...)` concurrently. The method checks `if (balance >= amount)`, then later reads `balance` into `oldBalance`, does some fake work (which yields a timing window), computes `newBalance = oldBalance - amount`, and writes `balance = newBalance`. Because these steps are not atomic and there is no synchronization or lock, two threads can both pass the `if (balance >= amount)` test and both subtract the same money, allowing the account to be overdrawn or causing the final balance to be lower than expected.

### Annotated problematic snippet

```java
// In BankAccount (intentionally UNSAFE)
public void withdraw(int amount, String who) {
  // Simulate "processing time" with a dumb busy loop (CPU burn, not sleep)
  fakeWork();

  // Check balance first
  if (balance >= amount) {
    // Another thread might change balance RIGHT HERE before we subtract.
    int oldBalance = balance;

    // More fake "processing time"
    fakeWork();

    int newBalance = oldBalance - amount;
    balance = newBalance; // <-- race: write without synchronization

    System.out.println(who + " withdrew $" + amount +
      " | old balance = " + oldBalance +
      " -> new balance = " + newBalance);
  } else {
    System.out.println(who + " tried to withdraw $" + amount +
      " but INSUFFICIENT FUNDS. (current balance = " + balance + ")");
  }
}
```

Key observation: the check-and-update sequence is non-atomic. The `fakeWork()` calls intentionally enlarge the window for interleaving, increasing the likelihood of the race showing up in tests.

### When/How it manifests

- Run the program multiple times: the final balance often differs between runs.
- Under interleavings where two threads both read the same `balance` before either writes the updated value, both succeed and write independently, effectively applying the subtraction twice from the same starting balance.
- The `fakeWork()` busy loops worsen the timing and can make the race happen more often. The small `Thread.sleep(1)` in the `WithdrawTask` only reshuffles timing; it does not provide mutual exclusion.

### Evidence / Repro steps (what I did)

1. Inspect `Original/BankSimulation.java` (see annotated snippet above).
2. Reason through interleavings: imagine balance is 100, two threads check `balance >= amount` (true), both compute and write -> end result subtracts twice.
3. Optionally: add simple timestamps or run many iterations — the program prints each withdraw event, which shows overlapping old/new balances when the bug occurs.

## Contract (for a correct solution)

- Inputs: concurrent calls to withdraw(amount) from multiple threads.
- Expected output: final `balance` equals startingBalance minus the sum of all *serializable* successful withdrawals; never negative if withdrawals are prevented when insufficient funds.
- Error modes: race condition leads to double-withdraw, negative balances, inconsistent final state.

## Two concrete fixes (code-level) with examples and trade-offs

Below are two practical fixes. Both are implementable with minimal project changes. I also mention an improved thread-management pattern (ExecutorService) and removing the busy CPU burn.

### Fix A — Simple synchronization (synchronized method / block)

What: Make the `withdraw(...)` operation atomic by synchronizing access to the critical section that checks and updates the balance.

Code example (minimal change):

```java
public synchronized void withdraw(int amount, String who) {
  // fakeWork() can remain or be removed; if you keep it, it runs under lock
  fakeWork();

  if (balance >= amount) {
    int oldBalance = balance;
    fakeWork();
    int newBalance = oldBalance - amount;
    balance = newBalance;
    System.out.println(who + " withdrew $" + amount + " | old=" + oldBalance + " new=" + newBalance);
  } else {
    System.out.println(who + " insufficient: " + balance);
  }
}
```

Pros:
- Easy to implement.
- Correctly serializes withdraw operations and eliminates the race.

Cons:
- If `fakeWork()` remains inside the synchronized method, it holds the lock while consuming CPU and reduces concurrency (bad performance). Put another way: don't do expensive work while holding the lock.
- Coarse-grained locks may limit throughput for highly concurrent systems.

Recommendation: Move expensive computation outside the synchronized section or remove `fakeWork()` entirely. For example:

```java
// Prepare/validate outside
fakeWork();
synchronized(this) {
  if (balance >= amount) {
    balance -= amount;
    // print
  }
}
```

### Fix B — Use AtomicInteger and optimistic CAS or use a Lock

What: Replace the `int balance` with an `AtomicInteger` and perform an atomic compare-and-set loop to ensure check-and-update happens atomically without explicit locking. Alternatively, use `ReentrantLock` with tryLock/timeouts if you need more control.

Code example (AtomicInteger):

```java
import java.util.concurrent.atomic.AtomicInteger;

static class BankAccountAtomic {
  private final AtomicInteger balance;

  public BankAccountAtomic(int starting) { balance = new AtomicInteger(starting); }

  public boolean withdraw(int amount, String who) {
    while (true) {
      int current = balance.get();
      if (current < amount) {
        System.out.println(who + " insufficient: " + current);
        return false;
      }
      int next = current - amount;
      // Attempt to atomically set new value only if current hasn't changed
      if (balance.compareAndSet(current, next)) {
        System.out.println(who + " withdrew $" + amount + " | old=" + current + " new=" + next);
        return true;
      }
      // else retry: another thread changed balance; loop again
    }
  }
}
```

Pros:
- Non-blocking algorithm may scale better in high-concurrency situations.
- Avoids holding a mutex; good throughput when contention is moderate and operations are quick.

Cons:
- More complex reasoning, but still straightforward here.
- With heavy contention the retry loop may spin and waste CPU; consider backoff.

Notes: If you need to prevent negative balances with absolutely minimal latency, the `compareAndSet` approach does the check-and-update atomically.

### Additional improvements (thread management & CPU usage)

- Use an `ExecutorService` (fixed thread pool) instead of manually `new Thread(...)` for better lifecycle control:

```java
ExecutorService exec = Executors.newFixedThreadPool(5);
exec.submit(new WithdrawTask(shared, ...));
// after submitting all tasks
exec.shutdown();
exec.awaitTermination(10, TimeUnit.SECONDS);
```

- Remove `fakeWork()` busy loops; if you must simulate delay, use `Thread.sleep(...)` to avoid burning CPU.
- Do not swallow InterruptedException silently — restore thread's interrupted status with `Thread.currentThread().interrupt();` or handle properly.

## Edge cases and testing checklist

- Edge cases to test:
  - Many concurrent small withdrawals (high contention).
  - Withdrawal equal to remaining balance at the same time from multiple threads.
  - Tasks interrupted during sleep.
  - Large numbers of tasks (performance/memory).

- Test plan:
  1. Run baseline (original program) many times to record variance and reproduce negative balances.
  2. Apply Fix A (synchronized) and verify deterministic correct final balance across many runs.
  3. Apply Fix B (AtomicInteger) and compare throughput and CPU usage under load.

## Quick developer notes / recommended patch

- Minimal, safe change: make `withdraw(...)` a synchronized method and remove heavy work from inside the lock.
- More scalable: replace `int balance` with `AtomicInteger` and use CAS loop as shown.
- Use `ExecutorService` to manage threads and avoid manual `Thread` objects.

## AI reflection (meta)

I examined the code manually and used reasoning about possible interleavings and the explicit busy-waits to identify the race condition. The nature of the bug and proposed fixes are standard: make the critical check-and-update atomic (either with synchronization or atomic primitives) and reduce lock hold time (remove busy-wait inside critical sections).

AI Reflection:
  I used AI to make sure I spotted all the problems, and then I use it to touch up my grammer because I kept reading it out and I didn't like how it was sounding even after changing it.