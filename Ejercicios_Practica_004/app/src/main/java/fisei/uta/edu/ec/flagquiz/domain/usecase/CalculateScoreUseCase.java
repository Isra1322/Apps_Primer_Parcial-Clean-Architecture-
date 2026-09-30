package fisei.uta.edu.ec.flagquiz.domain.usecase;

import fisei.uta.edu.ec.flagquiz.domain.model.QuizResult;

public final class CalculateScoreUseCase {
    public QuizResult execute(int totalGuesses, int correctAnswers) {
        // The existing quiz reports this percentage after all ten correct answers.
        return new QuizResult(totalGuesses, correctAnswers, 1000 / (double) totalGuesses);
    }
}
