package fisei.uta.edu.ec.addressbook.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.edu.ec.addressbook.data.DatabaseDescription;
import fisei.uta.edu.ec.addressbook.domain.entity.Contact;
import fisei.uta.edu.ec.addressbook.domain.repository.ContactRepository;

public class ContactRepositoryImpl implements ContactRepository {
    private final Context context;

    public ContactRepositoryImpl(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                DatabaseDescription.Contact.CONTENT_URI,
                null, null, null,
                DatabaseDescription.Contact.COLUMN_NAME + " COLLATE NOCASE ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contacts.add(cursorToContact(cursor));
            }
            cursor.close();
        }
        return contacts;
    }

    @Override
    public Contact getContactById(long id) {
        Uri contactUri = DatabaseDescription.Contact.buildContactUri(id);
        Cursor cursor = context.getContentResolver().query(contactUri, null, null, null, null);
        Contact contact = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contact = cursorToContact(cursor);
            }
            cursor.close();
        }
        return contact;
    }

    @Override
    public long addContact(Contact contact) {
        ContentValues values = contactToContentValues(contact);
        Uri newContactUri = context.getContentResolver().insert(DatabaseDescription.Contact.CONTENT_URI, values);
        if (newContactUri != null) {
            return Long.parseLong(newContactUri.getLastPathSegment());
        }
        return -1;
    }

    @Override
    public boolean updateContact(Contact contact) {
        ContentValues values = contactToContentValues(contact);
        Uri contactUri = DatabaseDescription.Contact.buildContactUri(contact.getId());
        int rowsUpdated = context.getContentResolver().update(contactUri, values, null, null);
        return rowsUpdated > 0;
    }

    @Override
    public boolean deleteContact(long id) {
        Uri contactUri = DatabaseDescription.Contact.buildContactUri(id);
        int rowsDeleted = context.getContentResolver().delete(contactUri, null, null);
        return rowsDeleted > 0;
    }

    private Contact cursorToContact(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_NAME));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_PHONE));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_EMAIL));
        String street = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_STREET));
        String city = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_CITY));
        String state = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_STATE));
        String zip = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact.COLUMN_ZIP));
        return new Contact(id, name, phone, email, street, city, state, zip);
    }

    private ContentValues contactToContentValues(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(DatabaseDescription.Contact.COLUMN_NAME, contact.getName());
        values.put(DatabaseDescription.Contact.COLUMN_PHONE, contact.getPhone());
        values.put(DatabaseDescription.Contact.COLUMN_EMAIL, contact.getEmail());
        values.put(DatabaseDescription.Contact.COLUMN_STREET, contact.getStreet());
        values.put(DatabaseDescription.Contact.COLUMN_CITY, contact.getCity());
        values.put(DatabaseDescription.Contact.COLUMN_STATE, contact.getState());
        values.put(DatabaseDescription.Contact.COLUMN_ZIP, contact.getZip());
        return values;
    }
}
