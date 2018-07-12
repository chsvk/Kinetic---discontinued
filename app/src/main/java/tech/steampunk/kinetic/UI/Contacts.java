package tech.steampunk.kinetic.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.steampunk.kinetic.Adapters.ContactList;
import tech.steampunk.kinetic.R;
import tech.steampunk.kinetic.data.Contact;

public class Contacts extends AppCompatActivity  {

    private Cursor cursor;
    private String name;
    private String phonenumber;
    private List<Contact> StoreContacts;
    private ContactList contactListAdapter;
    @BindView(R.id.contactList)ListView contactListView;
    private Set<Contact> hashSet;
    private Toolbar toolbar;
    private HashMap<String, String> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        contacts = new HashMap<>();
        toolbar = findViewById(R.id.contacts_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        StoreContacts = new ArrayList<>();
        hashSet = new LinkedHashSet<>();
        SharedPreferences editor = getSharedPreferences("AUTH", MODE_PRIVATE);
        if(editor.getString("Contacts", "").equals("Blank")){
            fetchContacts();
        }else{
            loadContacts();
        }

    }

    private void loadContacts() {
        setData();
        contactListAdapter = new ContactList(this,StoreContacts);
        contactListView.setAdapter(contactListAdapter);
        contactListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent in = new Intent(Contacts.this, ChatActivity.class);
            Contact temp = StoreContacts.get(i);
            in.putExtra("Name", temp.getName());
            in.putExtra("Number", temp.getNumber());
            startActivity(in);
            Toast.makeText(Contacts.this, StoreContacts.get(i).getName().toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setData() {
        SharedPreferences cPreferences = getSharedPreferences("Contacts", MODE_PRIVATE);
        for(Map.Entry entry: cPreferences.getAll().entrySet()){
           contacts.put(entry.getKey().toString(), entry.getValue().toString());
        }
        contacts = sortHashMapByValues(contacts);
        for ( Map.Entry<String, String> entry : contacts.entrySet()) {
            Contact t = new Contact(entry.getValue().toString(), entry.getKey().toString());
            StoreContacts.add(t);
        }
        hashSet.addAll(StoreContacts);
        StoreContacts.clear();
        StoreContacts.addAll(hashSet);
        hasDuplicates(StoreContacts);
    }

    public void fetchContacts(){

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        SharedPreferences.Editor editor = getSharedPreferences("Contacts", MODE_PRIVATE).edit();
        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim();
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
            phonenumber = phonenumber.replace(" ", "");
            if (phonenumber.length() >= 10 && phonenumber.length() <= 13) {
                if (phonenumber.startsWith("+91")) {

                } else {
                    if (phonenumber.length() == 10) {
                        phonenumber = "+91" + phonenumber;
                    }
                    if (phonenumber.length() == 11) {
                        phonenumber = phonenumber.replaceFirst("0", "");
                        phonenumber = "+91" + phonenumber;
                    }
                }

                Contact t = new Contact(name, phonenumber);
                editor.putString(t.getNumber(),t.getName());
            }
        }
        editor.apply();
        cursor.close();
        SharedPreferences Aeditor = getSharedPreferences("AUTH", MODE_PRIVATE);
        if(Aeditor.getString("Contacts", "").equals("Blank")){
            SharedPreferences.Editor E = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
            E.putString("Contacts", "FULL");
            E.apply();
            loadContacts();
        }
    }

    public void hasDuplicates(List<Contact> contacts) {
        final List<String> usedNames = new ArrayList<String>();
        Iterator<Contact> it = contacts.iterator();
        while (it.hasNext()) {
            Contact car = it.next();
            final String name = car.getNumber();

            if (usedNames.contains(name)) {
                it.remove();

            } else {
                usedNames.add(name);
            }
        }
    }

    public LinkedHashMap<String, String> sortHashMapByValues(
            HashMap<String, String> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<String> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, String> sortedMap =
                new LinkedHashMap<>();

        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            String val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String comp1 = passedMap.get(key);
                String comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
