package fisei.uta.edu.ec.tipcalculator.domain.usecase;

import fisei.uta.edu.ec.tipcalculator.domain.model.TipResult;

public final class CalculateTipUseCase {
    public TipResult execute(double billAmount, double percent) {
        double tip = billAmount * percent;
        double total = billAmount + tip;
        return new TipResult(tip, total);
    }
}
