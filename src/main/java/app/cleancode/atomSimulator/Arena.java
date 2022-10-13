package app.cleancode.atomSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Arena {
  private static final int PARTICLE_COUNT = 1000;
  private static final float PULL_MULTIPLIER = 1;
  private static float PARTICLE_LENGTH = 50;

  private final Canvas screenCanvas;
  private List<Particle> particles = new ArrayList<>();

  public Arena(Canvas screenCanvas) {
    this.screenCanvas = screenCanvas;
    float particleX = 0, particleY = 0;
    for (int i = 0; i < PARTICLE_COUNT; i++) {
      particles.add(new Particle(particleX, particleY, (float) Math.random(), (float) Math.random(),
          i % 3 != 0 ? 2 : -1));
      particleX++;
      if (particleX % 100 == 0) {
        particleX = 0;
        particleY++;
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
        float newXVelocity = particle.xVelocity();
        float newYVelocity = particle.yVelocity();
        // Try to reduce the velocities back to 0.
        newYVelocity /= 1.001;
        newXVelocity /= 1.001;
        for (Particle otherParticle : particles) {
          if (particle != otherParticle) {
            float distance = (float) Math.sqrt(Math.pow(otherParticle.x() - particle.x(), 2)
                + Math.pow(otherParticle.y() - particle.y(), 2));
            float otherWeightedCharge = otherParticle.charge() / (distance * distance);
            float pull = -(particle.charge() * otherWeightedCharge * PULL_MULTIPLIER);
            float xDirection = Math.signum(otherParticle.x() - particle.x());
            float yDirection = Math.signum(otherParticle.y() - particle.y());
            newXVelocity += pull * xDirection;
            newYVelocity += pull * yDirection;
          }
        }
        float newX = particle.x() + newXVelocity;
        float newY = particle.y() + newYVelocity;
        if (newX < 0 || newX >= (float) screenCanvas.getWidth()) {
          newXVelocity *= -1;
          newX = particle.x() + newXVelocity;
        }
        if (newY < 0 || newY >= (float) screenCanvas.getHeight()) {
          newYVelocity *= -1;
          newY = particle.y() + newYVelocity;
        }
        Particle newParticle =
            new Particle(newX, newY, newXVelocity, newYVelocity, particle.charge());
        newParticles.set(iCopy, newParticle);
      });
    }
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    long time = System.nanoTime() - start;
    System.out.printf("%.3fs\n", time / 1000000000d);
    particles.clear();
    GraphicsContext canvasGraphics = screenCanvas.getGraphicsContext2D();
    canvasGraphics.setFill(Color.BLACK);
    canvasGraphics.fillRect(0, 0, screenCanvas.getWidth(), screenCanvas.getHeight());
    for (int i = 0; i < newParticles.length(); i++) {
      Particle particle = newParticles.get(i);
      Color color;
      if (particle.charge() < 0) {
        color = Color.RED;
      } else if (particle.charge() > 0) {
        color = Color.BLUE;
      } else {
        color = Color.GREY;
      }
      canvasGraphics.setFill(color);
      canvasGraphics.fillOval(particle.x() - PARTICLE_LENGTH / 2d,
          particle.y() - PARTICLE_LENGTH / 2d, PARTICLE_LENGTH, PARTICLE_LENGTH);
      particles.add(particle);
    }
  }
}
