package fisei.uta.edu.ec.tipcalculator.domain.model;

public final class TipResult {
    private final double tip;
    private final double total;

    public TipResult(double tip, double total) {
        this.tip = tip;
        this.total = total;
    }

    public double getTip() {
        return tip;
    }

    public double getTotal() {
        return total;
    }
}
