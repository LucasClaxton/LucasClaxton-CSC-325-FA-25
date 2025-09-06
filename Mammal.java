// Subclass 3: Mammal
public class Mammal extends Animal {
    public Mammal(String name) {
        super(name);
    }

    @Override
    public String makeSound() {
        return " makes a growling sound.";
    }

    @Override
    public String move() {
        return " can run on land.";
    }
}