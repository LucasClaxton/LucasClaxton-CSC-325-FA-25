

    import javafx.collections.FXCollections; 
    import javafx.collections.ObservableList; 
    import javafx.scene.canvas.Canvas; 
    import javafx.scene.canvas.GraphicsContext; 
    import javafx.scene.control.Button; 
    import javafx.scene.control.RadioButton; 
    import javafx.scene.control.ToggleGroup; 
    import javafx.scene.layout.BorderPane; 
    import javafx.scene.layout.HBox; 
    import javafx.scene.paint.Color;

public class DrawingController {
    private BorderPane mainPane;
    private Canvas canvas;
    private GraphicsContext gc;
    private ObservableList<DrawableShape> shapes;
    private ToggleGroup shapeToggleGroup;

    public DrawingController() {
        mainPane = new BorderPane();
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        shapes = FXCollections.observableArrayList();
        setupControls();
        setupCanvas();
        drawShapes();
    }

    public BorderPane getMainPane() {
        return mainPane;
    }

    private void setupControls() {
        HBox controlBox = new HBox(10);
        
        RadioButton rectangleBtn = new RadioButton("Rectangle");
        RadioButton circleBtn = new RadioButton("Circle");
        
        shapeToggleGroup = new ToggleGroup();
        rectangleBtn.setToggleGroup(shapeToggleGroup);
        circleBtn.setToggleGroup(shapeToggleGroup);
        rectangleBtn.setSelected(true);
        
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            shapes.clear();
            clearCanvas();
        });
        
        controls.getChildren().addAll(rectangleBtn, circleBtn, clearButton);
        mainPane.setTop(controlBox);
    }

    private void setupCanvas() {
        canvas.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();
            DrawableShape shape = null;
            if (((RadioButton) shapeToggleGroup.getSelectedToggle()).getText().equals("Rectangle")) {
                // Draw a rectangle of fixed width/height and blue color
                shape = new RectangleShape(x, y, 50, 30, Color.BLUE);
            } else {
                // Draw a circle of fixed radius and red color
                shape = new CircleShape(x, y, 25, Color.RED);
            }
            shapes.add(shape);
            drawShapes();
        });
        mainPane.setCenter(canvas);
    }

    private void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawShapes() {
        clearCanvas();
        for (DrawableShape shape : shapes) {
            shape.draw(gc);
        }
    }
}
