package fisei.uta.edu.ec.addressbook.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fisei.uta.edu.ec.addressbook.data.repository.ContactRepositoryImpl;
import fisei.uta.edu.ec.addressbook.domain.entity.Contact;
import fisei.uta.edu.ec.addressbook.domain.repository.ContactRepository;

public class DetailViewModel extends AndroidViewModel {
    private final ContactRepository repository;
    private final MutableLiveData<Contact> contactLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccessLiveData = new MutableLiveData<>();

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = new ContactRepositoryImpl(application);
    }

    public LiveData<Contact> getContact() {
        return contactLiveData;
    }

    public LiveData<Boolean> getDeleteSuccess() {
        return deleteSuccessLiveData;
    }

    public void loadContact(long id) {
        Contact contact = repository.getContactById(id);
        contactLiveData.postValue(contact);
    }

    public void deleteContact(long id) {
        boolean success = repository.deleteContact(id);
        deleteSuccessLiveData.postValue(success);
    }
}
