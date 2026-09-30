package fisei.uta.edu.ec.addressbook.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fisei.uta.edu.ec.addressbook.data.repository.ContactRepositoryImpl;
import fisei.uta.edu.ec.addressbook.domain.entity.Contact;
import fisei.uta.edu.ec.addressbook.domain.repository.ContactRepository;

public class ContactsViewModel extends AndroidViewModel {
    private final ContactRepository repository;
    private final MutableLiveData<List<Contact>> contactsLiveData = new MutableLiveData<>();

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        repository = new ContactRepositoryImpl(application);
    }

    public LiveData<List<Contact>> getContacts() {
        return contactsLiveData;
    }

    public void loadContacts() {
        // In a real app this should be done in a background thread (e.g. RxJava, Coroutines, ExecutorService)
        // For simplicity during this refactor and since it's querying a ContentProvider, we do it here.
        List<Contact> contacts = repository.getAllContacts();
        contactsLiveData.postValue(contacts);
    }
}
