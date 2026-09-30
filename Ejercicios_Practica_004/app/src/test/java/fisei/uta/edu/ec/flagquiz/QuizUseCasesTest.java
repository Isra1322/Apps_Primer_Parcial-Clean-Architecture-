package fisei.uta.edu.ec.flagquiz;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import fisei.uta.edu.ec.flagquiz.domain.model.QuizQuestion;
import fisei.uta.edu.ec.flagquiz.domain.model.QuizResult;
import fisei.uta.edu.ec.flagquiz.domain.usecase.CalculateScoreUseCase;
import fisei.uta.edu.ec.flagquiz.domain.usecase.CheckAnswerUseCase;
import fisei.uta.edu.ec.flagquiz.domain.usecase.GenerateQuizUseCase;
import fisei.uta.edu.ec.flagquiz.presentation.util.FlagNameFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QuizUseCasesTest {
    private static final String[] REGIONS = {
            "Africa", "Asia", "Europe", "North_America", "Oceania", "South_America"
    };

    @Test
    public void allRegionSelectionsSupportEveryChoiceCountWithoutRepeatedQuestions() {
        GenerateQuizUseCase generate = new GenerateQuizUseCase(new Random(42));
        for (int selection = 1; selection < 64; selection++) {
            List<String> flags = loadFlags(selection);
            List<String> original = new ArrayList<>(flags);
            for (int choices : new int[]{2, 4, 6, 8}) {
                for (int repetition = 0; repetition < 20; repetition++) {
                    List<QuizQuestion> questions = generate.execute(flags, choices);
                    assertEquals(10, questions.size());
                    HashSet<String> selected = new HashSet<>();
                    for (QuizQuestion question : questions) {
                        assertTrue(selected.add(question.getFlagName()));
                        assertTrue(flags.contains(question.getFlagName()));
                        assertEquals(choices, question.getChoices().size());
                        assertEquals(choices, new HashSet<>(question.getChoices()).size());
                        assertTrue(question.getChoices().contains(question.getFlagName()));
                        assertTrue(flags.containsAll(question.getChoices()));
                        String region = FlagNameFormatter.getRegion(question.getFlagName());
                        assertTrue(new File("src/main/assets/" + region + "/"
                                + question.getFlagName() + ".png").isFile());
                    }
                }
                assertEquals(original, flags);
            }
        }
    }

    @Test
    public void tenCorrectAnswersWithRetriesKeepOriginalPercentage() {
        List<QuizQuestion> questions = new GenerateQuizUseCase(new Random(7))
                .execute(loadFlags(63), 8);
        CheckAnswerUseCase check = new CheckAnswerUseCase();
        int attempts = 0;
        int correct = 0;
        for (QuizQuestion question : questions) {
            String answer = FlagNameFormatter.getCountryName(question.getFlagName());
            String wrong = question.getChoices().stream()
                    .filter(flag -> !flag.equals(question.getFlagName())).findFirst().get();
            attempts++;
            assertFalse(check.execute(FlagNameFormatter.getCountryName(wrong), answer));
            attempts++;
            assertTrue(check.execute(answer, answer));
            correct++;
        }
        QuizResult result = new CalculateScoreUseCase().execute(attempts, correct);
        assertEquals(20, result.getTotalGuesses());
        assertEquals(10, result.getCorrectAnswers());
        assertEquals(50.0, result.getPercentage(), 0.0);
        assertEquals(1000 / 13.0,
                new CalculateScoreUseCase().execute(13, 10).getPercentage(), 0.0);
        assertEquals(100.0,
                new CalculateScoreUseCase().execute(10, 10).getPercentage(), 0.0);
    }

    @Test
    public void namesKeepSpacesPunctuationAndCountryHyphens() {
        assertEquals("United States of America", FlagNameFormatter.getCountryName(
                "North_America-United_States_of_America"));
        assertEquals("North_America", FlagNameFormatter.getRegion(
                "North_America-United_States_of_America"));
        assertEquals("Timor-Leste", FlagNameFormatter.getCountryName("Asia-Timor-Leste"));
        assertEquals("Eq. Guinea", FlagNameFormatter.getCountryName("Africa-Eq._Guinea"));
    }

    @Test
    public void emptyAssetsDoNotCreateQuestions() {
        assertTrue(new GenerateQuizUseCase(new Random(1))
                .execute(new ArrayList<>(), 4).isEmpty());
    }

    private List<String> loadFlags(int selection) {
        List<String> flags = new ArrayList<>();
        for (int index = 0; index < REGIONS.length; index++) {
            if ((selection & (1 << index)) != 0) {
                String[] names = new File("src/main/assets/" + REGIONS[index]).list();
                assertNotNull(names);
                Arrays.sort(names);
                for (String name : names) {
                    flags.add(name.replace(".png", ""));
                }
            }
        }
        return flags;
    }
}
