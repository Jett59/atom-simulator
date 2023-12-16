package app.cleancode.atomSimulator;

import java.util.List;
import javafx.scene.paint.Color;

public record Lepton(float x, float y, float xVelocity, float yVelocity, float charge,
    float diameter) implements ElectroMagnetic {

  public static Lepton electron(float x, float y) {
    return new Lepton(x, y, 0, 0, -1, 100);
  }

  @Override
  public Particle update(List<Particle> otherParticles, float arenaWidth, float arenaHeight) {
    Vector2 velocity = ElectroMagnetic.calculateVelocity(this, otherParticles);
    velocity = Particle.regulateVelocity(x, y, velocity, arenaWidth, arenaHeight);
    Vector2 position = Particle.move(x, y, velocity.x(), velocity.y(), arenaWidth, arenaHeight);
    return new Lepton(position.x(), position.y(), velocity.x(), velocity.y(), charge, diameter);
  }

  @Override
  public float getDiameter() {
    return diameter;
  }

  @Override
  public Color getColor() {
    if (charge > 0) {
      return Color.ALICEBLUE;
    } else if (charge < 0) {
      return Color.MEDIUMVIOLETRED;
    } else {
      return Color.GREY;
    }
  }

}
