package app.cleancode.atomSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Arena {
  private static final int ELECTRON_COUNT = 10;
  private static final int QUARK_COUNT = 10;
  private static final float ARENA_SIZE_MULTIPLIER = 4;

  private final Canvas screenCanvas;
  private List<Particle> particles = new ArrayList<>();
  private final float width, height;

  public Arena(Canvas screenCanvas) {
    this.screenCanvas = screenCanvas;
    this.width = (float) screenCanvas.getWidth() * ARENA_SIZE_MULTIPLIER;
    this.height = (float) screenCanvas.getHeight() * ARENA_SIZE_MULTIPLIER;
    for (int i = 0; i < ELECTRON_COUNT; i++) {
      particles.add(Lepton.electron((float) Math.random() * width, (float) Math.random() * height));
    }
    Colored.Color[] quarkColors = Colored.Color.values();
    for (int i = 0; i < QUARK_COUNT; i++) {
      Colored.Color initialColor = quarkColors[(int) (Math.random() * quarkColors.length)];
      if (i % 2 == 0) {
        particles.add(
            Quark.up((float) Math.random() * width, (float) Math.random() * height, initialColor));
      } else {
        particles.add(Quark.down((float) Math.random() * width, (float) Math.random() * height,
            initialColor));
      }
    }
  }

  public void render() {
    AtomicReferenceArray<Particle> newParticles = new AtomicReferenceArray<>(particles.size());
    Thread[] threads = new Thread[particles.size()];
    long start = System.nanoTime();
    for (int i = 0; i < particles.size(); i++) {
      // Copy to avoid local variable must be final message.
      int iCopy = i;
      Particle particle = particles.get(i);
      threads[i] = Thread.startVirtualThread(() -> {
        newParticles.set(iCopy, particle.update(particles, width, height));
      });
    }
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    particles.clear();
    GraphicsContext canvasGraphics = screenCanvas.getGraphicsContext2D();
    canvasGraphics.setFill(Color.BLACK);
    canvasGraphics.fillRect(0, 0, screenCanvas.getWidth(), screenCanvas.getHeight());
    for (int i = 0; i < newParticles.length(); i++) {
      Particle particle = newParticles.get(i);
      float diameter = particle.getDiameter();
      Color color = particle.getColor();
      canvasGraphics.setFill(color);
      canvasGraphics.fillOval((particle.x() - diameter / 2d) / ARENA_SIZE_MULTIPLIER,
          (particle.y() - diameter / 2d) / ARENA_SIZE_MULTIPLIER, diameter / ARENA_SIZE_MULTIPLIER,
          diameter / ARENA_SIZE_MULTIPLIER);
      particles.add(particle);
    }
    long time = System.nanoTime() - start;
    System.out.printf("%.3fs\n", time / 1000000000d);
  }
}
