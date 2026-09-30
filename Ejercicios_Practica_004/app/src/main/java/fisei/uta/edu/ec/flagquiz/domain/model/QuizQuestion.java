package fisei.uta.edu.ec.flagquiz.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class QuizQuestion {
    private final String flagName;
    private final List<String> choices;

    public QuizQuestion(String flagName, List<String> choices) {
        this.flagName = flagName;
        this.choices = Collections.unmodifiableList(new ArrayList<>(choices));
    }

    public String getFlagName() {
        return flagName;
    }

    public List<String> getChoices() {
        return choices;
    }
}
