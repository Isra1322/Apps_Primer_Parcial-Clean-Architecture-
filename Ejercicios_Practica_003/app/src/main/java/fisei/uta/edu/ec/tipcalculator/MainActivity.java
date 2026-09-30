package fisei.uta.edu.ec.tipcalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.OptionalDouble;

import fisei.uta.edu.ec.tipcalculator.domain.model.TipResult;
import fisei.uta.edu.ec.tipcalculator.domain.usecase.CalculateTipUseCase;
import fisei.uta.edu.ec.tipcalculator.presentation.util.AmountParser;

public class MainActivity extends AppCompatActivity {
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    private final CalculateTipUseCase calculateTipUseCase = new CalculateTipUseCase();
    private final AmountParser amountParser = new AmountParser();

    private double billAmount = 0.0;
    private double percent = 0.15;

    private TextView amountTextView;
    private TextView percentTextView;
    private TextView tipTextView;
    private TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setupAmountInput();
        setupPercentSeekBar();
    }

    private void initializeViews() {
        amountTextView = findViewById(R.id.amountTextView);
        percentTextView = findViewById(R.id.percentTextView);
        tipTextView = findViewById(R.id.tipTextView);
        totalTextView = findViewById(R.id.totalTextView);
        tipTextView.setText(currencyFormat.format(0));
        totalTextView.setText(currencyFormat.format(0));
    }

    private void setupAmountInput() {
        EditText amountEditText = findViewById(R.id.amountEditText);
        amountEditText.addTextChangedListener(amountEditTextWatcher);
    }

    private void setupPercentSeekBar() {
        SeekBar percentSeekBar = findViewById(R.id.percentSeekBar);
        percentSeekBar.setOnSeekBarChangeListener(seekBarListener);
    }

    private void updateCalculation() {
        percentTextView.setText(percentFormat.format(percent));
        TipResult result = calculateTipUseCase.execute(billAmount, percent);
        tipTextView.setText(currencyFormat.format(result.getTip()));
        totalTextView.setText(currencyFormat.format(result.getTotal()));
    }

    private final SeekBar.OnSeekBarChangeListener seekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    percent = progress / 100.0;
                    updateCalculation();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

    private final TextWatcher amountEditTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            OptionalDouble amount = amountParser.parse(text.toString());
            billAmount = amount.orElse(0.0);
            amountTextView.setText(amount.isPresent() ? currencyFormat.format(billAmount) : "");
            updateCalculation();
        }

        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable text) {
        }
    };
}
