package tech.steampunk.kinetic.UI;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import tech.steampunk.kinetic.R;
import tech.steampunk.kinetic.data.Contact;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private FloatingActionButton fab;
    private RecyclerView active_chats;
    private FirebaseRecyclerAdapter<Contact, ACviewHolder> firebaseRecyclerAdapter;
    private DatabaseReference chatsReference;
    public static HashMap<String, String> contacts;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        contacts = new HashMap<>();
        setData();
        SharedPreferences preferences = getActivity().getSharedPreferences("AUTH", MODE_PRIVATE);
        final String UNumber = preferences.getString("Number", "Default_User");


        active_chats = v.findViewById(R.id.active_chats_list);
        chatsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UNumber).child("Chats");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        active_chats.setLayoutManager(mLayoutManager);
        active_chats.setItemAnimator(new DefaultItemAnimator());
        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), Contacts.class)));
        try {
            chatsReference.keepSynced(true);

        }catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }

    private void setData() {
        SharedPreferences cPreferences = getActivity().getSharedPreferences("Contacts", MODE_PRIVATE);
        for(Map.Entry entry: cPreferences.getAll().entrySet()){
            contacts.put(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contact, ACviewHolder>(
                    Contact.class, R.layout.single_active_chat, ACviewHolder.class, chatsReference

            ) {
                @Override
                protected void populateViewHolder(ACviewHolder viewHolder, Contact model, int position) {
                    viewHolder.msg(model);
                    final String name = model.getName();
                    final String number = model.getNumber();
                    viewHolder.view.setOnClickListener(view -> {
                        Intent in = new Intent(getActivity(), ChatActivity.class);
                        if(contacts.get(number).toString().isEmpty()){
                            in.putExtra("Name", name);
                        }else{
                            in.putExtra("Name", contacts.get(number).toString());
                        }
                        in.putExtra("Number", number);
                        startActivity(in);
                    });
                }
            };
            active_chats.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.notifyDataSetChanged();

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ACviewHolder extends RecyclerView.ViewHolder{

        View view;
        public ACviewHolder(View itemView) {
            super(itemView);
            view=itemView;
        }

        public void msg(Contact contact){
            TextView name= view.findViewById(R.id.chat_name);
            TextView recentMessage = view.findViewById(R.id.chat_recent_message);
            name.setText(contact.getNumber());
            try{
                name.setText(contacts.get(contact.getNumber()));
            }catch (Exception e){

            }

            recentMessage.setText(contact.getMessage());
        }
    }
}
