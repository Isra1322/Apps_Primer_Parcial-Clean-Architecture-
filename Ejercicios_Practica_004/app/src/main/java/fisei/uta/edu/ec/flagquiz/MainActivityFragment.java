package fisei.uta.edu.ec.flagquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fisei.uta.edu.ec.flagquiz.data.repository.FlagRepository;
import fisei.uta.edu.ec.flagquiz.domain.model.QuizQuestion;
import fisei.uta.edu.ec.flagquiz.domain.model.QuizResult;
import fisei.uta.edu.ec.flagquiz.domain.usecase.GenerateQuizUseCase;
import fisei.uta.edu.ec.flagquiz.domain.usecase.CheckAnswerUseCase;
import fisei.uta.edu.ec.flagquiz.domain.usecase.CalculateScoreUseCase;
import fisei.uta.edu.ec.flagquiz.presentation.util.FlagNameFormatter;

import static fisei.uta.edu.ec.flagquiz.domain.usecase.GenerateQuizUseCase.FLAGS_IN_QUIZ;

public class MainActivityFragment extends Fragment {

    private static final String TAG = "FlagQuiz Activity";
    private static final int CHOICES_PER_ROW = 2;
    private static final long NEXT_FLAG_DELAY_MS = 2000;
    private static final long REVEAL_DURATION_MS = 500;

    private List<QuizQuestion> questions;
    private QuizQuestion currentQuestion;
    private FlagRepository flagRepository;
    private GenerateQuizUseCase generateQuiz;
    private final CheckAnswerUseCase checkAnswer = new CheckAnswerUseCase();
    private final CalculateScoreUseCase calculateScore = new CalculateScoreUseCase();
    private Set<String> regionsSet;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private Handler handler;
    private Animation shakeAnimation;

    private LinearLayout quizLinearLayout;
    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        questions = new ArrayList<>();
        flagRepository = new FlagRepository(requireContext().getAssets());
        generateQuiz = new GenerateQuizUseCase(new SecureRandom());
        handler = new Handler(Looper.getMainLooper());

        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        quizLinearLayout = view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        flagImageView = view.findViewById(R.id.flagImageView);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = view.findViewById(R.id.row4LinearLayout);
        answerTextView = view.findViewById(R.id.answerTextView);

        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextView.setText(getString(R.string.question, 1, FLAGS_IN_QUIZ));
        return view;
    }

    public void updateGuessRows(SharedPreferences sharedPreferences) {
        String choices = sharedPreferences.getString(MainActivity.CHOICES, "4");
        guessRows = Integer.parseInt(choices) / CHOICES_PER_ROW;

        for (LinearLayout layout : guessLinearLayouts) {
            layout.setVisibility(View.GONE);
        }

        for (int row = 0; row < guessRows; row++) {
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }
    }

    public void updateRegions(SharedPreferences sharedPreferences) {
        regionsSet = sharedPreferences.getStringSet(MainActivity.REGIONS, null);
        if (regionsSet == null || regionsSet.isEmpty()) {
            regionsSet = new java.util.HashSet<>(java.util.Arrays.asList(
                    getResources().getStringArray(R.array.regions_list)
            ));
        }
    }

    public void resetQuiz() {
        if (getActivity() == null) return;
        correctAnswers = 0;
        totalGuesses = 0;
        questions = generateQuiz.execute(flagRepository.getFlags(regionsSet),
                guessRows * CHOICES_PER_ROW);

        loadNextFlag();
    }

    private void loadNextFlag() {
        if (questions.isEmpty() || getActivity() == null) return;

        currentQuestion = questions.remove(0);
        String nextImage = currentQuestion.getFlagName();
        answerTextView.setText("");

        questionNumberTextView.setText(getString(R.string.question, (correctAnswers + 1), FLAGS_IN_QUIZ));

        String region = FlagNameFormatter.getRegion(nextImage);

        try (InputStream stream = flagRepository.openFlag(region, nextImage)) {
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);
            animate(false);
        } catch (IOException exception) {
            Log.e(TAG, "Error loading " + nextImage, exception);
        }

        showChoices();
    }

    private void showChoices() {
        for (int row = 0; row < guessRows; row++) {
            for (int column = 0; column < guessLinearLayouts[row].getChildCount(); column++) {
                Button button = (Button) guessLinearLayouts[row].getChildAt(column);
                button.setEnabled(true);
                String flagName = currentQuestion.getChoices().get(row * CHOICES_PER_ROW + column);
                button.setText(FlagNameFormatter.getCountryName(flagName));
            }
        }
    }

    private void animate(boolean animateOut) {
        if (correctAnswers == 0) return;

        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;
        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());

        Animator animator;

        if (animateOut) {
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadNextFlag();
                }
            });
        } else {
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(REVEAL_DURATION_MS);
        animator.start();
    }

    private final OnClickListener guessButtonListener = view -> {
        Button guessButton = (Button) view;
        String guess = guessButton.getText().toString();
        String answer = FlagNameFormatter.getCountryName(currentQuestion.getFlagName());
        ++totalGuesses;

        if (checkAnswer.execute(guess, answer)) {
            showCorrectAnswer(answer);
        } else {
            showIncorrectAnswer(guessButton);
        }
    };

    private void showCorrectAnswer(String answer) {
        ++correctAnswers;
        answerTextView.setText(String.format("%s!", answer));
        if (getContext() != null) {
            answerTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.correct_answer));
        }
        disableButtons();

        if (correctAnswers == FLAGS_IN_QUIZ) {
            showResults();
        } else {
            handler.postDelayed(() -> animate(true), NEXT_FLAG_DELAY_MS);
        }
    }

    private void showIncorrectAnswer(Button guessButton) {
        flagImageView.startAnimation(shakeAnimation);
        answerTextView.setText(R.string.incorrect_answer);
        if (getContext() != null) {
            answerTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.incorrect_answer));
        }
        guessButton.setEnabled(false);
    }

    private void showResults() {
        if (getActivity() == null) return;
        QuizResult result = calculateScore.execute(totalGuesses, correctAnswers);
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.results,
                        result.getTotalGuesses(), result.getPercentage()))
                .setPositiveButton(R.string.reset_quiz, (dialog, id) -> resetQuiz())
                .setCancelable(false)
                .show();
    }

    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++) {
                guessRow.getChildAt(i).setEnabled(false);
            }
        }
    }
}