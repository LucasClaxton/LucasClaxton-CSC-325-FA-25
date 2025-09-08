public class Circle2 implements Drawable, Calculable, Describable {
    private final String color;
    private final double radius;

    public Circle2(String color, double radius) {
        this.color = color;
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public void draw() {
        System.out.println("Drawing a " + color + " circle.");
    }

    @Override
    public String describe() {
        return "Shape: Circle, Sides: 0, Color: " + color;
    }
}