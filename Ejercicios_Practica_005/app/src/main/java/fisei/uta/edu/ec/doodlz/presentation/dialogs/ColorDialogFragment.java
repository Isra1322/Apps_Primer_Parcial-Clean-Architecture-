package fisei.uta.edu.ec.doodlz.presentation.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import fisei.uta.edu.ec.doodlz.R;
import fisei.uta.edu.ec.doodlz.application.usecases.GetDrawingSettingsUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.UpdateColorUseCase;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingColor;

@AndroidEntryPoint
public class ColorDialogFragment extends DialogFragment {

    public interface ColorDialogListener {
        void onColorSelected(int color);
    }

    @Inject GetDrawingSettingsUseCase getDrawingSettingsUseCase;
    @Inject UpdateColorUseCase updateColorUseCase;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_color, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.title_color_dialog);
        builder.setView(view);

        SeekBar alpha = view.findViewById(R.id.alphaSeekBar);
        SeekBar red = view.findViewById(R.id.redSeekBar);
        SeekBar green = view.findViewById(R.id.greenSeekBar);
        SeekBar blue = view.findViewById(R.id.blueSeekBar);
        View colorView = view.findViewById(R.id.colorView);

        DrawingColor currentColor = getDrawingSettingsUseCase != null
                ? getDrawingSettingsUseCase.execute().getColor()
                : DrawingColor.defaultBlack();

        alpha.setProgress(currentColor.getAlpha());
        red.setProgress(currentColor.getRed());
        green.setProgress(currentColor.getGreen());
        blue.setProgress(currentColor.getBlue());

        colorView.setBackgroundColor(currentColor.getArgb());

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newColor = Color.argb(
                        alpha.getProgress(),
                        red.getProgress(),
                        green.getProgress(),
                        blue.getProgress()
                );
                colorView.setBackgroundColor(newColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        alpha.setOnSeekBarChangeListener(listener);
        red.setOnSeekBarChangeListener(listener);
        green.setOnSeekBarChangeListener(listener);
        blue.setOnSeekBarChangeListener(listener);

        builder.setPositiveButton(R.string.button_set_color, (dialog, which) -> {
            DrawingColor selectedColor = DrawingColor.fromRgba(
                    alpha.getProgress(),
                    red.getProgress(),
                    green.getProgress(),
                    blue.getProgress()
            );

            if (updateColorUseCase != null) {
                updateColorUseCase.execute(selectedColor);
            }

            ColorDialogListener dialogListener = resolveListener();
            if (dialogListener != null) {
                dialogListener.onColorSelected(selectedColor.getArgb());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private ColorDialogListener resolveListener() {
        Fragment parent = getParentFragment();
        if (parent instanceof ColorDialogListener) {
            return (ColorDialogListener) parent;
        }

        Fragment target = requireActivity().getSupportFragmentManager().findFragmentById(R.id.doodleFragment);
        if (target instanceof ColorDialogListener) {
            return (ColorDialogListener) target;
        }

        if (requireActivity() instanceof ColorDialogListener) {
            return (ColorDialogListener) requireActivity();
        }

        return null;
    }
}
