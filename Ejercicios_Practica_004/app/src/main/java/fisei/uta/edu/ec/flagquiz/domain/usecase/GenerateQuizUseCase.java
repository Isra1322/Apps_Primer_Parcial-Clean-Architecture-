package fisei.uta.edu.ec.flagquiz.domain.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import fisei.uta.edu.ec.flagquiz.domain.model.QuizQuestion;

public final class GenerateQuizUseCase {
    public static final int FLAGS_IN_QUIZ = 10;
    private final Random random;

    public GenerateQuizUseCase(Random random) {
        this.random = random;
    }

    public List<QuizQuestion> execute(List<String> availableFlags, int numberOfChoices) {
        List<String> flags = new ArrayList<>(new LinkedHashSet<>(availableFlags));
        if (flags.isEmpty()) {
            return Collections.emptyList();
        }
        if (flags.size() < FLAGS_IN_QUIZ || numberOfChoices < 2
                || numberOfChoices > flags.size()) {
            throw new IllegalArgumentException("Not enough flags or invalid number of choices");
        }

        List<String> selectedFlags = new ArrayList<>();
        while (selectedFlags.size() < FLAGS_IN_QUIZ) {
            String flag = flags.get(random.nextInt(flags.size()));
            if (!selectedFlags.contains(flag)) {
                selectedFlags.add(flag);
            }
        }

        List<QuizQuestion> questions = new ArrayList<>();
        for (String flag : selectedFlags) {
            Collections.shuffle(flags, random);
            flags.add(flags.remove(flags.indexOf(flag)));
            List<String> choices = new ArrayList<>(flags.subList(0, numberOfChoices));
            choices.set(random.nextInt(numberOfChoices), flag);
            questions.add(new QuizQuestion(flag, choices));
        }
        return questions;
    }
}
