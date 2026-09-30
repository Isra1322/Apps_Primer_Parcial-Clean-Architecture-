package fisei.uta.edu.ec.flagquiz.domain.usecase;

public final class CheckAnswerUseCase {
    public boolean execute(String selectedAnswer, String correctAnswer) {
        return selectedAnswer.equals(correctAnswer);
    }
}
