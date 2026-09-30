package fisei.uta.edu.ec.doodlz.presentation.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import fisei.uta.edu.ec.doodlz.R;
import fisei.uta.edu.ec.doodlz.application.usecases.DetectShakeUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.GetDrawingSettingsUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.PrintDrawingUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.SaveDrawingUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.UpdateColorUseCase;
import fisei.uta.edu.ec.doodlz.application.usecases.UpdateLineWidthUseCase;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingSettings;
import fisei.uta.edu.ec.doodlz.presentation.dialogs.ColorDialogFragment;
import fisei.uta.edu.ec.doodlz.presentation.dialogs.EraseImageDialogFragment;
import fisei.uta.edu.ec.doodlz.presentation.dialogs.LineWidthDialogFragment;
import fisei.uta.edu.ec.doodlz.presentation.views.DoodleView;

@AndroidEntryPoint
public class MainActivityFragment extends Fragment implements
        SensorEventListener,
        ColorDialogFragment.ColorDialogListener,
        LineWidthDialogFragment.LineWidthDialogListener,
        EraseImageDialogFragment.EraseDialogListener {

    @Inject GetDrawingSettingsUseCase getDrawingSettingsUseCase;
    @Inject UpdateColorUseCase updateColorUseCase;
    @Inject UpdateLineWidthUseCase updateLineWidthUseCase;
    @Inject SaveDrawingUseCase saveDrawingUseCase;
    @Inject PrintDrawingUseCase printDrawingUseCase;
    @Inject DetectShakeUseCase detectShakeUseCase;

    private DoodleView doodleView;
    private SensorManager sensorManager;

    public MainActivityFragment() {
        super(R.layout.fragment_main);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        doodleView = view.findViewById(R.id.doodleView);
        setHasOptionsMenu(true);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        if (detectShakeUseCase != null) {
            detectShakeUseCase.reset();
        }

        if (getDrawingSettingsUseCase != null) {
            DrawingSettings settings = getDrawingSettingsUseCase.execute();
            doodleView.setDrawingColor(settings.getColor().getArgb());
            doodleView.setLineWidth(settings.getLineWidth().getValue());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Fragment existingDialog = getParentFragmentManager().findFragmentByTag("erase dialog");

        if (existingDialog == null && detectShakeUseCase != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if (detectShakeUseCase.evaluate(x, y, z)) {
                EraseImageDialogFragment dialog = new EraseImageDialogFragment();
                dialog.show(getParentFragmentManager(), "erase dialog");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.doodle_fragment_menu, menu);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        } else {
            menu.findItem(R.id.color).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.line_width).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.delete_drawing).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.save).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.findItem(R.id.print).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.color) {
            ColorDialogFragment dialog = new ColorDialogFragment();
            dialog.show(getParentFragmentManager(), "color dialog");
            return true;
        } else if (id == R.id.line_width) {
            LineWidthDialogFragment dialog = new LineWidthDialogFragment();
            dialog.show(getParentFragmentManager(), "line width dialog");
            return true;
        } else if (id == R.id.delete_drawing) {
            EraseImageDialogFragment dialog = new EraseImageDialogFragment();
            dialog.show(getParentFragmentManager(), "erase dialog");
            return true;
        } else if (id == R.id.save) {
            saveCurrentDrawing();
            return true;
        } else if (id == R.id.print) {
            printCurrentDrawing();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveCurrentDrawing() {
        if (doodleView == null) {
            return;
        }

        Bitmap bitmap = doodleView.getDrawingBitmap();
        String fileName = "Doodlz_" + System.currentTimeMillis() + ".jpg";

        boolean success = saveDrawingUseCase != null && saveDrawingUseCase.execute(bitmap, fileName);
        int messageResId = success ? R.string.message_saved : R.string.message_error_saving;
        Toast.makeText(getContext(), messageResId, Toast.LENGTH_SHORT).show();
    }

    private void printCurrentDrawing() {
        if (doodleView == null) {
            return;
        }

        Bitmap bitmap = doodleView.getDrawingBitmap();
        String jobName = "Doodlz_" + System.currentTimeMillis();

        if (printDrawingUseCase != null) {
            boolean success = printDrawingUseCase.execute(jobName, bitmap);
            int messageResId = success ? R.string.message_pdf_saved : R.string.message_error_pdf;
            Toast.makeText(getContext(), messageResId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onColorSelected(int color) {
        if (doodleView != null) {
            doodleView.setDrawingColor(color);
        }
    }

    @Override
    public void onLineWidthSelected(int width) {
        if (doodleView != null) {
            doodleView.setLineWidth(width);
        }
    }

    @Override
    public void onEraseConfirmed() {
        if (doodleView != null) {
            doodleView.clear();
        }
    }

    public DoodleView getDoodleView() {
        return doodleView;
    }
}
