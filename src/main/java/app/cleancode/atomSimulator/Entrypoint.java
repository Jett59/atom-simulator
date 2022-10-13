package app.cleancode.atomSimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Entrypoint extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Atoms");
    primaryStage.setFullScreen(true);
    Screen screen = Screen.getPrimary();
    Canvas screenCanvas = new Canvas(screen.getBounds().getWidth() * 2, screen.getBounds().getHeight() * 2);
    screenCanvas.setScaleX(1/2d);
    screenCanvas.setScaleY(1/2d);
    Arena  arena = new Arena(screenCanvas);
    Group root = new Group(screenCanvas);
    Scene scene = new Scene(root);
    Camera camera = new PerspectiveCamera();
    camera.setTranslateX(screenCanvas.getBoundsInParent().getMinX());
    camera.setTranslateY(screenCanvas.getBoundsInParent().getMinY());
    scene.setCamera(camera);
    primaryStage.setScene(scene);
    primaryStage.show();
    new AnimationTimer() {
      
      @Override
      public void handle(long now) {
        arena.render();
      }
    }.start();
  }

}
