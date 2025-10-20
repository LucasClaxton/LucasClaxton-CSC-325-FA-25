Assignment 8:
  Levels completed:
    Level 1: Yes.

Refactor analysis (150-250 words):

This refactor replaces imperative loops and manual mutations with Java 8 Stream
pipelines and lambda expressions to improve readability and reduce boilerplate.
Each utility function was rewritten to use filter/map/sorted/collect or
mapToDouble/average where appropriate. The advantages include clearer intent
(what is being filtered, mapped or sorted), fewer chances for accidental
mutation, and shorter, more declarative methods. Sorting logic now leverages
Comparator composition which is easier to follow than anonymous inner classes.

Performance-wise the new implementations preserve the same big-O behavior; in
some cases streams may introduce small overhead for very small lists but they
scale well and facilitate parallelization if needed. Tradeoffs included the
loss of explicit intermediate mutation, which can be unfamiliar to students
used to imperative code, and the requirement for Java 8+ runtime. One challenge
was preserving original null-safety and return-value behavior; the refactor
keeps defensive null checks and returns empty lists or 0.0 where the legacy
code did. Overall the refactor improves clarity and maintainability while
keeping behavior equivalent to the original.
  
AI Reflection: The thing I used AI for was the comparison comments because I forgot to keep track of that and also the analysis.