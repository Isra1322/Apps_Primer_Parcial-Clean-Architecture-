package fisei.uta.edu.ec.doodlz.presentation.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import fisei.uta.edu.ec.doodlz.R;
import fisei.uta.edu.ec.doodlz.application.usecases.GetDrawingSettingsUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.UpdateLineWidthUseCase;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingColor;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingSettings;
import fisei.uta.edu.ec.doodlz.domain.entities.LineWidth;

@AndroidEntryPoint
public class LineWidthDialogFragment extends DialogFragment {

    public interface LineWidthDialogListener {
        void onLineWidthSelected(int width);
    }

    @Inject GetDrawingSettingsUseCase getDrawingSettingsUseCase;
    @Inject UpdateLineWidthUseCase updateLineWidthUseCase;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_line_width, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.title_line_width_dialog);
        builder.setView(view);

        ImageView imageView = view.findViewById(R.id.widthImageView);
        SeekBar seekBar = view.findViewById(R.id.widthSeekBar);

        DrawingSettings settings = getDrawingSettingsUseCase != null
                ? getDrawingSettingsUseCase.execute()
                : DrawingSettings.createDefault();

        int currentWidth = Math.max(1, settings.getLineWidth().getValue());
        int currentColor = settings.getColor().getArgb();

        seekBar.setProgress(currentWidth);
        updatePreview(imageView, currentWidth, currentColor);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int width = Math.max(1, progress);
                updatePreview(imageView, width, currentColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        builder.setPositiveButton(R.string.button_set_line_width, (dialog, which) -> {
            int width = Math.max(1, seekBar.getProgress());

            if (updateLineWidthUseCase != null) {
                updateLineWidthUseCase.execute(LineWidth.of(width));
            }

            LineWidthDialogListener listener = resolveListener();
            if (listener != null) {
                listener.onLineWidthSelected(width);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private void updatePreview(ImageView imageView, int width, int color) {
        Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setStrokeCap(Paint.Cap.ROUND);
        bitmap.eraseColor(Color.TRANSPARENT);

        canvas.drawLine(30, 50, 370, 50, paint);
        imageView.setImageBitmap(bitmap);
    }

    private LineWidthDialogListener resolveListener() {
        Fragment parent = getParentFragment();
        if (parent instanceof LineWidthDialogListener) {
            return (LineWidthDialogListener) parent;
        }

        Fragment target = requireActivity().getSupportFragmentManager().findFragmentById(R.id.doodleFragment);
        if (target instanceof LineWidthDialogListener) {
            return (LineWidthDialogListener) target;
        }

        if (requireActivity() instanceof LineWidthDialogListener) {
            return (LineWidthDialogListener) requireActivity();
        }

        return null;
    }
}
