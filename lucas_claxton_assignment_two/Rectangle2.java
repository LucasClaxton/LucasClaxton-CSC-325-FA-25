public class Rectangle2 implements Drawable, Calculable, Describable {
    private final String color;
    private final double width;
    private final double height;

    public Rectangle2(String color, double width, double height) {
        this.color = color;
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
        return "Shape: Rectangle, Sides: 4, Color: " + color;
    }
}