package fisei.uta.edu.ec.tipcalculator.presentation.util;

import java.util.OptionalDouble;

public final class AmountParser {
    // An empty result distinguishes invalid input from a valid zero amount.
    public OptionalDouble parse(String value) {
        if (value == null) {
            return OptionalDouble.empty();
        }
        try {
            return OptionalDouble.of(Double.parseDouble(value) / 100.0);
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }
}
