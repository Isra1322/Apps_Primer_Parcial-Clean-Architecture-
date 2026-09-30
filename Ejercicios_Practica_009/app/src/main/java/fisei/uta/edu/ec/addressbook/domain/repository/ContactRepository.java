package fisei.uta.edu.ec.addressbook.domain.repository;

import java.util.List;
import fisei.uta.edu.ec.addressbook.domain.entity.Contact;

public interface ContactRepository {
    List<Contact> getAllContacts();
    Contact getContactById(long id);
    long addContact(Contact contact);
    boolean updateContact(Contact contact);
    boolean deleteContact(long id);
}
