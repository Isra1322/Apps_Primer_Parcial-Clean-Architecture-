package fisei.uta.edu.ec.addressbook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import fisei.uta.edu.ec.addressbook.databinding.FragmentContactsBinding;
import fisei.uta.edu.ec.addressbook.presentation.viewmodel.ContactsViewModel;

public class ContactsFragment extends Fragment {

    public interface ContactsFragmentListener {
        void onContactSelected(long contactId);
        void onAddContact();
    }

    private ContactsFragmentListener listener;
    private ContactsAdapter contactsAdapter;
    private ContactsViewModel viewModel;
    private FragmentContactsBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ContactsFragmentListener) {
            listener = (ContactsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ContactsFragmentListener");
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
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new ItemDivider(getContext()));

        contactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactClickListener() {
            @Override
            public void onClick(long contactId) {
                if (listener != null) {
                    listener.onContactSelected(contactId);
                }
            }
        });
        binding.recyclerView.setAdapter(contactsAdapter);

        binding.addButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddContact();
            }
        });

        viewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        viewModel.getContacts().observe(getViewLifecycleOwner(), contacts -> {
            contactsAdapter.setContacts(contacts);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadContacts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}