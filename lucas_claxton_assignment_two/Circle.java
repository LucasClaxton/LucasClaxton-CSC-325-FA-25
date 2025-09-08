public class Circle extends Shape {
    private final double radius;

    public Circle(String color, double radius) {
        super(color, "Circle");
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
        return "Shape: " + name + ", Sides: 0, Color: " + color;
    }
}