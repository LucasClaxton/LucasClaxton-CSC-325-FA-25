public class Rectangle extends Shape {
    private final double width;
    private final double height;

    public Rectangle(String color, double width, double height) {
        super(color, "Rectangle");
        this.width = width;
        this.height = height;
    }

    @Override
    public double getArea() {
        return width * height;
    }

    @Override
    public void draw() {
        System.out.println("Drawing a " + color + " rectangle.");
    }

    @Override
    public String describe() {
        return "Shape: " + name + ", Sides: 4, Color: " + color;
    }
}