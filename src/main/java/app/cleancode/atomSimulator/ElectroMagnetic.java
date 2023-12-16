package app.cleancode.atomSimulator;

import java.util.List;

public interface ElectroMagnetic extends Particle {
  public static float PULL_MULTIPLIER = 1;

  float charge();

  public static Vector2 calculateVelocity(ElectroMagnetic particle, List<Particle> otherParticles) {
    float xVelocity = particle.xVelocity() * Particle.MOMENTUM_SLOWING_FACTOR;
    float yVelocity = particle.yVelocity() * Particle.MOMENTUM_SLOWING_FACTOR;
    for (Particle otherParticle : otherParticles) {
      if (otherParticle != particle
          && otherParticle instanceof ElectroMagnetic otherElectroMagneticParticle) {
        float distance = MathHelper.distance(particle.x(), otherParticle.x(), particle.y(), otherParticle.y());
        float pull = MathHelper.pull(distance, PULL_MULTIPLIER * -(otherElectroMagneticParticle.charge() * particle.charge()));
        float xDirection = MathHelper.direction(particle.x(), otherParticle.x());
        float yDirection = MathHelper.direction(particle.y(), otherParticle.y());
        xVelocity += pull * xDirection;
        yVelocity += pull * yDirection;
      }
    }
    return new Vector2(xVelocity, yVelocity);
  }
}
