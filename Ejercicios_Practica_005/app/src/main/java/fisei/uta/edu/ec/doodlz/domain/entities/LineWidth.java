package fisei.uta.edu.ec.doodlz.domain.entities;

import java.util.Objects;

/**
 * Domain value object representing stroke line width.
 * Independent of Android platform classes.
 */
public final class LineWidth {
    public static final int MIN_WIDTH = 1;
    public static final int MAX_WIDTH = 50;
    public static final int DEFAULT_WIDTH = 5;

    private final int value;

    public LineWidth(int value) {
        if (value < MIN_WIDTH) {
            this.value = MIN_WIDTH;
        } else if (value > MAX_WIDTH) {
            this.value = MAX_WIDTH;
        } else {
            this.value = value;
        }
    }

    public static LineWidth of(int value) {
        return new LineWidth(value);
    }

    public static LineWidth defaultWidth() {
        return new LineWidth(DEFAULT_WIDTH);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineWidth)) return false;
        LineWidth lineWidth = (LineWidth) o;
        return value == lineWidth.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "LineWidth{value=" + value + '}';
    }
}
