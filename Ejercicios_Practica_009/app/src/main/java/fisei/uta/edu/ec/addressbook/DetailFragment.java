package fisei.uta.edu.ec.addressbook;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fisei.uta.edu.ec.addressbook.databinding.FragmentContactDetailBinding;
import fisei.uta.edu.ec.addressbook.presentation.viewmodel.DetailViewModel;

public class DetailFragment extends Fragment {

    public interface DetailFragmentListener {
        void onEditContact(long contactId);
        void onContactDeleted();
    }

    private DetailFragmentListener listener;
    private long contactId = -1;
    private DetailViewModel viewModel;
    private FragmentContactDetailBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DetailFragmentListener) {
            listener = (DetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DetailFragmentListener");
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
        setHasOptionsMenu(true);
        binding = FragmentContactDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            contactId = arguments.getLong("CONTACT_ID", -1);
        }

        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        viewModel.getContact().observe(getViewLifecycleOwner(), contact -> {
            if (contact != null) {
                binding.nameTextView.setText(contact.getName());
                binding.phoneTextView.setText(contact.getPhone());
                binding.emailTextView.setText(contact.getEmail());
                binding.streetTextView.setText(contact.getStreet());
                binding.cityTextView.setText(contact.getCity());
                binding.stateTextView.setText(contact.getState());
                binding.zipTextView.setText(contact.getZip());
            }
        });

        viewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success && listener != null) {
                listener.onContactDeleted();
            }
        });

        if (contactId != -1) {
            viewModel.loadContact(contactId);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            if (listener != null && contactId != -1) {
                listener.onEditContact(contactId);
            }
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            deleteContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_title);
        builder.setMessage(R.string.confirm_message);
        builder.setPositiveButton(R.string.button_delete, (dialog, button) -> {
            if (contactId != -1) {
                viewModel.deleteContact(contactId);
            }
        });
        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}