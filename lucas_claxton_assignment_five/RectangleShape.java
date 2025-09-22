

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangleShape extends DrawableShape {
    private double width;
    private double height;

    public RectangleShape(double x, double y, double width, double height, Color color) {
        super(x, y, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
    }
}