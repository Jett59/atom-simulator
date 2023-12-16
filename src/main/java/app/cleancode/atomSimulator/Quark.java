package app.cleancode.atomSimulator;

import java.util.List;

public record Quark(float x, float y, float xVelocity, float yVelocity, Color color, float charge)
    implements Colored, ElectroMagnetic {

  public static Quark up(float x, float y, Color initialColor) {
    return new Quark(x, y, 0, 0, initialColor, 1);
  }

  public static Quark down(float x, float y, Color initialColor) {
    return new Quark(x, y, 0, 0, initialColor, -1);
  }

  private Quark withPositionColorAndVelocity(float x, float y, Color color, float xVelocity,
      float yVelocity) {
    return new Quark(x, y, xVelocity, yVelocity, color, charge);
  }

  @Override
  public Particle update(List<Particle> otherParticles, float arenaWidth, float arenaHeight) {
    Vector2 velocity = ElectroMagnetic.calculateVelocity(this, otherParticles);
    var velocityAndColor = Colored.calculateVelocityAndColor(this, otherParticles);
    velocity = new Vector2(velocityAndColor.xVelocity() + velocity.x(),
        velocityAndColor.yVelocity() + velocity.y());
    velocity = Particle.regulateVelocity(x, y, velocity, arenaWidth, arenaHeight);
    Vector2 position = Particle.move(x, y, velocity.x(), velocity.y(), arenaWidth, arenaHeight);
    return withPositionColorAndVelocity(position.x(), position.y(), color, velocity.x(),
        velocity.y());
  }

  @Override
  public float getDiameter() {
    return 100;
  }

}
