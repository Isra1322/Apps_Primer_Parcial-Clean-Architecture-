package fisei.uta.edu.ec.doodlz.domain.entities;

import java.util.Objects;

/**
 * Domain entity grouping current drawing configuration (color and line width).
 */
public final class DrawingSettings {
    private final DrawingColor color;
    private final LineWidth lineWidth;

    public DrawingSettings(DrawingColor color, LineWidth lineWidth) {
        this.color = Objects.requireNonNull(color, "color cannot be null");
        this.lineWidth = Objects.requireNonNull(lineWidth, "lineWidth cannot be null");
    }

    public static DrawingSettings createDefault() {
        return new DrawingSettings(DrawingColor.defaultBlack(), LineWidth.defaultWidth());
    }

    public DrawingColor getColor() {
        return color;
    }

    public LineWidth getLineWidth() {
        return lineWidth;
    }

    public DrawingSettings withColor(DrawingColor newColor) {
        return new DrawingSettings(newColor, this.lineWidth);
    }

    public DrawingSettings withLineWidth(LineWidth newLineWidth) {
        return new DrawingSettings(this.color, newLineWidth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawingSettings)) return false;
        DrawingSettings that = (DrawingSettings) o;
        return Objects.equals(color, that.color) && Objects.equals(lineWidth, that.lineWidth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, lineWidth);
    }

    @Override
    public String toString() {
        return "DrawingSettings{" +
                "color=" + color +
                ", lineWidth=" + lineWidth +
                '}';
    }
}
