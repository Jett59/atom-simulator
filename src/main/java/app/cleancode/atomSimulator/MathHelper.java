package app.cleancode.atomSimulator;

public class MathHelper {
  public static float distance(float x1, float x2, float y1, float y2) {
    return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  public static float direction(float a, float b) {
    return Math.signum(b - a);
  }

  public static float pull(float distance, float multiplier) {
    return (float) Math.min(distance, Math.sqrt(Math.pow(1f / (distance * distance) * multiplier, 2) / 2));
  }
}
