package fisei.uta.edu.ec.flagquiz.domain.model;

public final class QuizResult {
    private final int totalGuesses;
    private final int correctAnswers;
    private final double percentage;

    public QuizResult(int totalGuesses, int correctAnswers, double percentage) {
        this.totalGuesses = totalGuesses;
        this.correctAnswers = correctAnswers;
        this.percentage = percentage;
    }

    public int getTotalGuesses() {
        return totalGuesses;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public double getPercentage() {
        return percentage;
    }
}
