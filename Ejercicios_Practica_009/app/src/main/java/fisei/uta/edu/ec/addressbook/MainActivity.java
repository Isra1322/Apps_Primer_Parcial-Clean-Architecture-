package fisei.uta.edu.ec.addressbook;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import fisei.uta.edu.ec.addressbook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        ContactsFragment.ContactsFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (savedInstanceState == null && binding.fragmentContainer != null) {
            ContactsFragment contactsFragment = new ContactsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, contactsFragment);
            transaction.commit();
        }
    }

    @Override
    public void onContactSelected(long contactId) {
        if (binding.fragmentContainer != null) {
            displayContact(contactId, R.id.fragmentContainer);
        }
    }

    @Override
    public void onAddContact() {
        if (binding.fragmentContainer != null) {
            displayAddEditFragment(R.id.fragmentContainer, null);
        }
    }

    private void displayContact(long contactId, int viewID) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle arguments = new Bundle();
        arguments.putLong("CONTACT_ID", contactId);
        detailFragment.setArguments(arguments);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayAddEditFragment(int viewID, Long contactId) {
        AddEditFragment addEditFragment = new AddEditFragment();
        Bundle arguments = new Bundle();
        if (contactId != null) {
            arguments.putBoolean("EDITING", true);
            arguments.putLong("CONTACT_ID", contactId);
        } else {
            arguments.putBoolean("EDITING", false);
        }
        addEditFragment.setArguments(arguments);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onEditContact(long contactId) {
        if (binding.fragmentContainer != null) {
            displayAddEditFragment(R.id.fragmentContainer, contactId);
        }
    }

    @Override
    public void onContactDeleted() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onAddEditCompleted(long contactId) {
        getSupportFragmentManager().popBackStack();
    }
}