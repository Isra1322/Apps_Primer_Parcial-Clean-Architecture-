package fisei.uta.edu.ec.doodlz.presentation.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import fisei.uta.edu.ec.doodlz.R;

public class EraseImageDialogFragment extends DialogFragment {

    public interface EraseDialogListener {
        void onEraseConfirmed();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.message_erase);

        builder.setPositiveButton(R.string.button_erase, (dialog, which) -> {
            EraseDialogListener listener = resolveListener();
            if (listener != null) {
                listener.onEraseConfirmed();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private EraseDialogListener resolveListener() {
        Fragment parent = getParentFragment();
        if (parent instanceof EraseDialogListener) {
            return (EraseDialogListener) parent;
        }

        Fragment target = requireActivity().getSupportFragmentManager().findFragmentById(R.id.doodleFragment);
        if (target instanceof EraseDialogListener) {
            return (EraseDialogListener) target;
        }

        if (requireActivity() instanceof EraseDialogListener) {
            return (EraseDialogListener) requireActivity();
        }

        return null;
    }
}
