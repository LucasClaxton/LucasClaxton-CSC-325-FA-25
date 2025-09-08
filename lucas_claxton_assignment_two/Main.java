public class Main {
    public static void main(String[] args) {
        // Abstract class demo
        Shape circle = new Circle("Red", 5.0);
        Shape rectangle = new Rectangle("Blue", 4.0, 6.0);

        System.out.println("--- Abstract Class Demo ---");
        circle.draw();
        System.out.println(circle.describe());
        System.out.println("Area: " + circle.getArea());

        rectangle.draw();
        System.out.println(rectangle.describe());
        System.out.println("Area: " + rectangle.getArea());

        // Interface-based demo
        Circle2 circle2 = new Circle2("Green", 3.0);
        Rectangle2 rectangle2 = new Rectangle2("Yellow", 2.0, 8.0);

        System.out.println("\n--- Interface-Based Demo ---");
        circle2.draw();
        System.out.println(circle2.describe());
        System.out.println("Area: " + circle2.getArea());

        rectangle2.draw();
        System.out.println(rectangle2.describe());
        System.out.println("Area: " + rectangle2.getArea());
    }
}