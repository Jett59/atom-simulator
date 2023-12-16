package app.cleancode.atomSimulator;

import java.util.List;

public interface Colored extends Particle {
  public static float PULL_MULTIPLIER = 2;

  public static enum Color {
    RED, GREEN, BLUE
  };

  Color color();

  default javafx.scene.paint.Color getColor() {
    return switch (color()) {
      case RED -> javafx.scene.paint.Color.RED;
      case GREEN -> javafx.scene.paint.Color.GREEN;
      case BLUE -> javafx.scene.paint.Color.BLUE;
    };
  }

  public static record VelocityAndColor(float xVelocity, float yVelocity, Color color) {
  }

  public static VelocityAndColor calculateVelocityAndColor(Colored particle,
      List<Particle> particles) {
    float newXVelocity = particle.xVelocity() * Particle.MOMENTUM_SLOWING_FACTOR;
    float newYVelocity = particle.yVelocity() * Particle.MOMENTUM_SLOWING_FACTOR;
    boolean isTouchingRed = false;
    boolean isTouchingGreen = false;
    boolean isTouchingBlue = false;
    for (Particle otherParticle : particles) {
      if (otherParticle != particle && otherParticle instanceof Colored otherColoredParticle) {
        float distance =
            MathHelper.distance(particle.x(), otherParticle.x(), particle.y(), otherParticle.y());
        float pull = MathHelper.pull(distance,
            PULL_MULTIPLIER * (particle.color() == otherColoredParticle.color() ? -1 : 1));
        float xDirection = MathHelper.direction(particle.x(), otherParticle.x());
        float yDirection = MathHelper.direction(particle.y(), otherParticle.y());
        if (distance < (particle.getDiameter() + otherParticle.getDiameter()) / 2f) {
          isTouchingRed = isTouchingRed || otherColoredParticle.color() == Color.RED;
          isTouchingGreen = isTouchingGreen || otherColoredParticle.color() == Color.GREEN;
          isTouchingBlue = isTouchingBlue || otherColoredParticle.color() == Color.BLUE;
          pull = -0.5f;
        }
        newXVelocity += pull * xDirection;
        newYVelocity += pull * yDirection;
      }
    }
    Color newColor = particle.color();
    // TODO: revisit and make color exchange.
    return new VelocityAndColor(newXVelocity, newYVelocity, newColor);
  }
}
