import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(new Bird("Duck"));
        animals.add(new Fish("Goldfish"));
        animals.add(new Mammal("Tiger"));
        
        for (Animal animal : animals) {
            System.out.println(animal.getName() + animal.makeSound());
            System.out.println(animal.getName() + animal.move());
            if (animal instanceof Flyable flyable) {
                System.out.println(animal.getName() + flyable.fly());
            }
            if (animal instanceof Swimmable swimmable) {
                System.out.println(animal.getName() + swimmable.swim());
            }
            System.out.println();
            System.out.println();
        }
    }
}