package fisei.uta.edu.ec.doodlz.domain.entities;

import java.util.Objects;

/**
 * Domain entity representing an ARGB color for drawing.
 * Completely independent of Android platform classes.
 */
public final class DrawingColor {
    private final int alpha;
    private final int red;
    private final int green;
    private final int blue;
    private final int argb;

    public DrawingColor(int alpha, int red, int green, int blue) {
        this.alpha = clamp(alpha);
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
        this.argb = ((this.alpha & 0xFF) << 24)
                | ((this.red & 0xFF) << 16)
                | ((this.green & 0xFF) << 8)
                | (this.blue & 0xFF);
    }

    private DrawingColor(int argb) {
        this.argb = argb;
        this.alpha = (argb >>> 24) & 0xFF;
        this.red = (argb >> 16) & 0xFF;
        this.green = (argb >> 8) & 0xFF;
        this.blue = argb & 0xFF;
    }

    public static DrawingColor fromArgb(int argb) {
        return new DrawingColor(argb);
    }

    public static DrawingColor fromRgba(int alpha, int red, int green, int blue) {
        return new DrawingColor(alpha, red, green, blue);
    }

    public static DrawingColor defaultBlack() {
        return new DrawingColor(255, 0, 0, 0);
    }

    public int getAlpha() {
        return alpha;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getArgb() {
        return argb;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawingColor)) return false;
        DrawingColor that = (DrawingColor) o;
        return argb == that.argb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argb);
    }

    @Override
    public String toString() {
        return "DrawingColor{" +
                "alpha=" + alpha +
                ", red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", argb=" + String.format("#%08X", argb) +
                '}';
    }
}
