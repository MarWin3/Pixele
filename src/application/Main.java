package application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Main extends Application {

	private static final int SQUARE_SIZE_X = 40;
	private static final int SQUARE_SIZE_Y = 40;
	private int rightRectTopLeftX = 256;
	private int rightRectTopLeftY = 10;
	private List<WritableImage> images = new ArrayList<>();

	private Group root;
	private Scene scene;

	private Canvas canvas;
	private GraphicsContext context;
	private Button clearButton;

	private WritableImage writableImage;

	@Override
	public void start(Stage primaryStage) {
		try {
			initComponents();
			Image image = new Image(getClass().getResourceAsStream("image.jpg"));
			context.drawImage(image, 0, 0);

			canvas.setOnMouseClicked(e -> {
				writableImage = new WritableImage(SQUARE_SIZE_X, SQUARE_SIZE_Y);
				byte[] bytes = new byte[SQUARE_SIZE_X * SQUARE_SIZE_Y * 4];
				int x = adjustCoordinate((int) e.getX());
				int y = adjustCoordinate((int) e.getY());
				image.getPixelReader().getPixels(x - 20, y - 20, SQUARE_SIZE_X, SQUARE_SIZE_Y,
						PixelFormat.getByteBgraInstance(), bytes, 0, 40 * 4);

				writableImage.getPixelWriter().setPixels(0, 0, SQUARE_SIZE_X, SQUARE_SIZE_Y,
						PixelFormat.getByteBgraInstance(), bytes, 0, 40 * 4);

				images.add(writableImage);
				clear();
				drawSquareImages(sortImages());
			});

			clearButton.setOnAction(e -> {
				clear();
				images.clear();
			});

			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		root = new Group();
		scene = new Scene(root, 576, 256);
		clearButton = new Button("Clear");
		canvas = new Canvas(512, 256);
		context = canvas.getGraphicsContext2D();

		clearButton.getStyleClass().add("clearbutton");

		root.getChildren().add(canvas);
		root.getChildren().add(clearButton);
	}

	private int adjustCoordinate(int i) {
		if (i - 20 <= 0)
			return i + 20;
		if (i + 20 >= 256)
			return 236;
		return i;

	}

	private List<WritableImage> sortImages() {
		return images.stream().sorted(Comparator.comparing(this::getColorValue).reversed()).limit(25)
				.collect(Collectors.toList());
	}

	private void drawSquareImages(List<WritableImage> sortedImg) {
		for (int i = 0; i < sortedImg.size(); i++) {
			if (i > 0 & i % 5 == 0) {
				rightRectTopLeftX = 256;
				rightRectTopLeftY += SQUARE_SIZE_Y + 5;
			}
			context.drawImage(sortedImg.get(i), rightRectTopLeftX, rightRectTopLeftY);
			rightRectTopLeftX += SQUARE_SIZE_X + 5;
		}
	}

	private double getColorValue(WritableImage img) {
		double redValue = 0;
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				Color pxColor = img.getPixelReader().getColor(x, y);
				redValue += pxColor.getRed();
				redValue -= pxColor.getBlue();
				redValue -= pxColor.getGreen();
			}
		}
		return redValue;
	}

	private void clear() {
		context.clearRect(256, 0, 256, 256);
		rightRectTopLeftX = 256;
		rightRectTopLeftY = 10;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
