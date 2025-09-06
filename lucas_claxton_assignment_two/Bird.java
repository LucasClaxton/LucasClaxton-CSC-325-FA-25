// Subclass 1: Bird
public class Bird extends Animal implements Flyable, Swimmable {
    public Bird(String name) {
        super(name);
    }

    @Override
    public String makeSound() {
        return " makes a honking sound.";
    }

    @Override
    public String move() {
        return " can waddel, swim, and fly.";
    }

    @Override
    public String fly() {
        return " flies in the sky.";
    }

    @Override
    public String swim() {
        return " swims on water.";
    }
}