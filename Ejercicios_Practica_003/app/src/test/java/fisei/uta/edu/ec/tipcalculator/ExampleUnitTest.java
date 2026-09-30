package fisei.uta.edu.ec.tipcalculator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import fisei.uta.edu.ec.tipcalculator.domain.model.TipResult;
import fisei.uta.edu.ec.tipcalculator.domain.usecase.CalculateTipUseCase;
import fisei.uta.edu.ec.tipcalculator.presentation.util.AmountParser;

public class ExampleUnitTest {
    private final AmountParser parser = new AmountParser();
    private final CalculateTipUseCase calculateTip = new CalculateTipUseCase();

    @Test
    public void thousandCentsAtFifteenPercent() {
        assertCalculation("1000", 0.15, 10.0, 1.5, 11.5);
    }

    @Test
    public void twentyFiveHundredCentsAtTwentyPercent() {
        assertCalculation("2500", 0.20, 25.0, 5.0, 30.0);
    }

    @Test
    public void seekBarLimits() {
        assertCalculation("1000", 0.0, 10.0, 0.0, 10.0);
        assertCalculation("1000", 0.30, 10.0, 3.0, 13.0);
    }

    @Test
    public void invalidAndEmptyInputRemainDistinctFromZero() {
        for (String input : new String[] {"", "abc", " ", "1,000", null}) {
            assertFalse(parser.parse(input).isPresent());
            TipResult result = calculateTip.execute(parser.parse(input).orElse(0.0), 0.15);
            assertEquals(0.0, result.getTip(), 0.0);
            assertEquals(0.0, result.getTotal(), 0.0);
        }
        assertTrue(parser.parse("0").isPresent());
        assertEquals(0.0, parser.parse("0").getAsDouble(), 0.0);
    }

    @Test
    public void centsAndMaximumInputArePreserved() {
        assertCalculation("1", 0.15, 0.01, 0.0015, 0.0115);
        assertCalculation("999999", 0.30, 9999.99, 2999.997, 12999.987);
    }

    private void assertCalculation(String input, double percent, double amount,
                                   double tip, double total) {
        double parsed = parser.parse(input).getAsDouble();
        assertEquals(amount, parsed, 0.000000001);
        TipResult result = calculateTip.execute(parsed, percent);
        assertEquals(tip, result.getTip(), 0.000000001);
        assertEquals(total, result.getTotal(), 0.000000001);
    }
}
