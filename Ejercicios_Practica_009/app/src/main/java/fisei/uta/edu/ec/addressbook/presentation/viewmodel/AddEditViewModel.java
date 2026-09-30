package fisei.uta.edu.ec.addressbook.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fisei.uta.edu.ec.addressbook.data.repository.ContactRepositoryImpl;
import fisei.uta.edu.ec.addressbook.domain.entity.Contact;
import fisei.uta.edu.ec.addressbook.domain.repository.ContactRepository;

public class AddEditViewModel extends AndroidViewModel {
    private final ContactRepository repository;
    private final MutableLiveData<Contact> contactLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AddEditViewModel(@NonNull Application application) {
        super(application);
        repository = new ContactRepositoryImpl(application);
    }

    public LiveData<Contact> getContact() {
        return contactLiveData;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccessLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void loadContact(long id) {
        Contact contact = repository.getContactById(id);
        contactLiveData.postValue(contact);
    }

    public void saveContact(Long id, String name, String phone, String email, String street, String city, String state, String zip) {
        if (name == null || name.trim().isEmpty()) {
            errorLiveData.postValue("The name is obligatory.");
            return;
        }

        Contact contact = new Contact(
                id == null ? 0 : id,
                name, phone, email, street, city, state, zip
        );

        boolean success;
        if (id == null || id == 0) {
            long newId = repository.addContact(contact);
            success = newId > 0;
        } else {
            success = repository.updateContact(contact);
        }
        
        saveSuccessLiveData.postValue(success);
    }
}
