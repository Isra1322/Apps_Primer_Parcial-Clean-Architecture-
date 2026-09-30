package fisei.uta.edu.ec.addressbook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fisei.uta.edu.ec.addressbook.databinding.FragmentAddEditBinding;
import fisei.uta.edu.ec.addressbook.presentation.viewmodel.AddEditViewModel;

public class AddEditFragment extends Fragment {

    public interface AddEditFragmentListener {
        void onAddEditCompleted(long contactId);
    }

    private AddEditFragmentListener listener;
    private long contactId = -1;
    private boolean updating = false;
    private AddEditViewModel viewModel;
    private FragmentAddEditBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEditFragmentListener) {
            listener = (AddEditFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddEditFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            updating = arguments.getBoolean("EDITING");
            contactId = arguments.getLong("CONTACT_ID", -1);
        }

        viewModel = new ViewModelProvider(this).get(AddEditViewModel.class);

        viewModel.getContact().observe(getViewLifecycleOwner(), contact -> {
            if (contact != null) {
                binding.nameTextInputLayout.getEditText().setText(contact.getName());
                binding.phoneTextInputLayout.getEditText().setText(contact.getPhone());
                binding.emailTextInputLayout.getEditText().setText(contact.getEmail());
                binding.streetTextInputLayout.getEditText().setText(contact.getStreet());
                binding.cityTextInputLayout.getEditText().setText(contact.getCity());
                binding.stateTextInputLayout.getEditText().setText(contact.getState());
                binding.zipTextInputLayout.getEditText().setText(contact.getZip());
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                binding.nameTextInputLayout.setError(errorMsg);
            } else {
                binding.nameTextInputLayout.setError(null);
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success && listener != null) {
                listener.onAddEditCompleted(contactId);
            }
        });

        if (updating && contactId != -1) {
            viewModel.loadContact(contactId);
        }

        binding.saveFloatingButton.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getView() != null) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
            saveContact();
        });
    }

    private void saveContact() {
        String name = binding.nameTextInputLayout.getEditText().getText().toString();
        String phone = binding.phoneTextInputLayout.getEditText().getText().toString();
        String email = binding.emailTextInputLayout.getEditText().getText().toString();
        String street = binding.streetTextInputLayout.getEditText().getText().toString();
        String city = binding.cityTextInputLayout.getEditText().getText().toString();
        String state = binding.stateTextInputLayout.getEditText().getText().toString();
        String zip = binding.zipTextInputLayout.getEditText().getText().toString();

        Long idToSave = updating ? contactId : null;
        viewModel.saveContact(idToSave, name, phone, email, street, city, state, zip);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}