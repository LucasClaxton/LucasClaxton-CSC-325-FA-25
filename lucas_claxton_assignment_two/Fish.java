// Subclass 2: Fish
public class Fish extends Animal implements Swimmable {
    public Fish(String name) {
        super(name);
    }

    @Override
    public String makeSound() {
        return "makes a Blub blub sound.";
    }

    @Override
    public String move() {
        return " can swim.";
    }

    @Override
    public String swim() {
        return " swims swiftly.";
    }
}