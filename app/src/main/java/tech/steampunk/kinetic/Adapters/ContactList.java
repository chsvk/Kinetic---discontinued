package tech.steampunk.kinetic.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import tech.steampunk.kinetic.R;
import tech.steampunk.kinetic.data.Contact;

/**
 * Created by Vamshi on 9/7/2017.
 */

public class ContactList extends ArrayAdapter<Contact> {

    private List<Contact> tContacts;
    private Context context;
    private DatabaseReference databaseReference;

    public ContactList(@NonNull Context context,List<Contact> contacts) {
        super(context,R.layout.single_contact, contacts);
        this.context = context;
        this.tContacts = contacts;
    }

    @Override
    public int getCount() {
        return tContacts.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        View vi = inflater.inflate(R.layout.single_contact, parent, false);
        ImageView contactDP = vi.findViewById(R.id.contactDP);
        TextView contactName = vi.findViewById(R.id.contactName);
        TextView contactNumber = vi.findViewById(R.id.contactNumber);
        final ImageView contactInvite = vi.findViewById(R.id.ContactInvite);
        final Contact t = tContacts.get(position);
        contactName.setText(t.getName());
        contactNumber.setText(t.getNumber());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(t.getNumber())){
                    contactInvite.setVisibility(View.GONE);
                }else {
                    contactInvite.setOnClickListener(view -> Toast.makeText(context, "Invite This Contact!", Toast.LENGTH_SHORT).show());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return vi;
    }

}
