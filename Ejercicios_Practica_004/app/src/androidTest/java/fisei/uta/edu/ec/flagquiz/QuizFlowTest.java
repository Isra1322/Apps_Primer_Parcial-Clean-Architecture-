package fisei.uta.edu.ec.flagquiz;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.preference.PreferenceManager;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

import fisei.uta.edu.ec.flagquiz.domain.model.QuizQuestion;
import fisei.uta.edu.ec.flagquiz.presentation.util.FlagNameFormatter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QuizFlowTest {
    @Test
    public void quizCompletesRestartsAndAppliesSettings() throws Exception {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                PreferenceManager.getDefaultSharedPreferences(
                        InstrumentationRegistry.getInstrumentation().getTargetContext())
                        .edit()
                        .putString(MainActivity.CHOICES, "4")
                        .putStringSet(MainActivity.REGIONS, new HashSet<>(Arrays.asList(
                                "Africa", "Asia", "Europe", "North_America",
                                "Oceania", "South_America")))
                        .commit());
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity activity = (MainActivity) InstrumentationRegistry.getInstrumentation()
                .startActivitySync(intent);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        onView(withId(R.id.action_settings)).perform(click());
        onView(withText("Number of Choices")).perform(click());
        onView(withText("8")).perform(click());
        onView(withText("Regions")).perform(click());
        // Disable every region through the existing preferences dialog.
        for (String region : new String[]{"Africa", "Asia", "Europe", "North America",
                "Oceania", "South America"}) {
            onView(withText(region)).perform(click());
        }
        onView(withText(android.R.string.ok)).perform(click());
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Activity settings = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED).iterator().next();
            assertTrue(settings instanceof SettingsActivity);
            ((SettingsActivity) settings).onSupportNavigateUp();
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        HashSet<String> seen = new HashSet<>();
        for (int questionNumber = 1; questionNumber <= 10; questionNumber++) {
            final int number = questionNumber;
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                MainActivityFragment fragment = (MainActivityFragment) activity
                        .getSupportFragmentManager().findFragmentById(R.id.quizFragment);
                assertNotNull(fragment);
                QuizQuestion question = currentQuestion(fragment);
                assertTrue(seen.add(question.getFlagName()));
                assertEquals("North_America", FlagNameFormatter.getRegion(question.getFlagName()));
                assertEquals(8, question.getChoices().size());
                TextView progress = activity.findViewById(R.id.questionNumberTextView);
                assertEquals(activity.getString(R.string.question, number, 10),
                        progress.getText().toString());
                String answer = FlagNameFormatter.getCountryName(question.getFlagName());
                Button correct = null;
                Button wrong = null;
                for (int rowId : new int[]{R.id.row1LinearLayout, R.id.row2LinearLayout,
                        R.id.row3LinearLayout, R.id.row4LinearLayout}) {
                    LinearLayout row = activity.findViewById(rowId);
                    for (int column = 0; column < row.getChildCount(); column++) {
                        Button button = (Button) row.getChildAt(column);
                        assertTrue(button.isEnabled());
                        if (answer.contentEquals(button.getText())) correct = button;
                        else wrong = button;
                    }
                }
                assertNotNull(correct);
                assertNotNull(wrong);
                wrong.performClick();
                assertFalse(wrong.isEnabled());
                TextView feedback = activity.findViewById(R.id.answerTextView);
                assertEquals(activity.getString(R.string.incorrect_answer), feedback.getText().toString());
                correct.performClick();
                assertFalse(correct.isEnabled());
                assertEquals(answer + "!", feedback.getText().toString());
            });
            if (questionNumber < 10) Thread.sleep(3200);
        }
        onView(withText(activity.getString(R.string.results, 20, 50.0)))
                .check(matches(isDisplayed()));
        onView(withText(R.string.reset_quiz)).perform(click());
        onView(withId(R.id.questionNumberTextView))
                .check(matches(withText(activity.getString(R.string.question, 1, 10))));
        InstrumentationRegistry.getInstrumentation().runOnMainSync(activity::finish);
    }

    private static QuizQuestion currentQuestion(MainActivityFragment fragment) {
        try {
            Field field = MainActivityFragment.class.getDeclaredField("currentQuestion");
            field.setAccessible(true);
            return (QuizQuestion) field.get(fragment);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
