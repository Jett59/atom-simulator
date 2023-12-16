package app.cleancode.atomSimulator;

import java.util.List;
import javafx.scene.paint.Color;

public interface Particle {
  public static float MOMENTUM_SLOWING_FACTOR = 1f;
  
Particle update(List<Particle> otherParticles, float arenaWidth, float arenaHeight);
float x();
float y();
float xVelocity();
float yVelocity();

float getDiameter();
Color getColor();

public static Vector2 regulateVelocity(float x, float y, Vector2 velocity, float arenaWidth, float arenaHeight) {
  float xVelocity = velocity.x();
  float yVelocity = velocity.y();
  float newX = x + xVelocity;
  float newY = y + yVelocity;
  if (newX < 0 || newX >= arenaWidth) {
    xVelocity *= -1;
    newX = x + xVelocity;
    if (newX < 0 || newX >= arenaWidth) {
      xVelocity = 0;
    }
  }
  if (newY < 0 || newY >= arenaHeight) {
    yVelocity *= -1;
    newY = y + yVelocity;
    if (newY < 0 || newY >= arenaHeight) {
      yVelocity = 0;
    }
  }
  return new Vector2(xVelocity, yVelocity);
}

public static Vector2 move(float x, float y, float xVelocity, float yVelocity, float arenaWidth, float arenaHeight) {
  float newX = x + xVelocity;
  float newY = y + yVelocity;
  if (newX < 0 || newX >= arenaWidth) {
    newX = x;
  }
  if (newY < 0 || newY >= arenaHeight) {
    newY = y;
  }
  return new Vector2(newX, newY);
}
}
