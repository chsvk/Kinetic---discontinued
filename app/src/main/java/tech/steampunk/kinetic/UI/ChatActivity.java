package tech.steampunk.kinetic.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.steampunk.kinetic.Adapters.ConversationAdapter;
import tech.steampunk.kinetic.R;
import tech.steampunk.kinetic.data.Message;
import tech.steampunk.kinetic.data.Scanner;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    @BindView(R.id.message_content)EditText message;
    @BindView(R.id.camera_intent)FloatingActionButton camera_action;
    @BindView(R.id.send_message)FloatingActionButton send_button;
    @BindView(R.id.conversation_list)RecyclerView conversation_list;
    @BindView(R.id.fabmojiA)FloatingActionButton fabA;
    @BindView(R.id.fabmojiB)FloatingActionButton fabB;
    @BindView(R.id.fabmojiC)FloatingActionButton fabC;
    @BindView(R.id.fabmojiD)FloatingActionButton fabD;
    @BindView(R.id.fabmojiE)FloatingActionButton fabE;
    @BindView(R.id.fabList)LinearLayout fabList;
    @BindView(R.id.close_fabList)FloatingActionButton closeFab;
    private List<Message> conversation;
    private DatabaseReference messageDatabase;
    private DatabaseReference oMessageDatabase;
    private DatabaseReference chatsReference;
    private DatabaseReference ochatsReference;
    private DatabaseReference notificationsReference;
    public static String UID;
    private ConversationAdapter conversationAdapter;
    private HashMap<String, String> contacts;
    private Uri imageUri;
    public String UNumber = null;
    public String MNumber = null;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    public Boolean isOpen = false;
    public String mood = "Neutral";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        Intent in = getIntent();
        contacts = new HashMap<>();
        send_button.setVisibility(View.GONE);
        closeFab.setVisibility(View.GONE);

        fabOpen = AnimationUtils.loadAnimation(this, R.animator.fab_animations_open);
        fabClose = AnimationUtils.loadAnimation(this, R.animator.fab_animations_close);
        rotateForward =AnimationUtils.loadAnimation(this, R.animator.fab_animations_rotate_forward);
        rotateBackward =AnimationUtils.loadAnimation(this, R.animator.fab_animations_roate_backward);
        SharedPreferences preferences = getSharedPreferences("AUTH", MODE_PRIVATE);

        conversation = new ArrayList<>();
        toolbar = findViewById(R.id.chat_activity_toolbar);
        setSupportActionBar(toolbar);
        final String name = in.getStringExtra("Name");
        UID = preferences.getString("UID","Default UID");
        UNumber = preferences.getString("Number", "Default_User");
        MNumber = in.getStringExtra("Number");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(name);

        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UNumber).child("Messages").child(MNumber);
        oMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(MNumber).child("Messages").child(UNumber);
        chatsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UNumber).child("Chats");
        ochatsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(MNumber).child("Chats");
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");

        conversationAdapter = new ConversationAdapter(conversation, this);
        conversation_list.setAdapter(conversationAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        conversation_list.setLayoutManager(mLayoutManager);
        conversation_list.setItemAnimator(new DefaultItemAnimator());

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                send_button.setVisibility(View.GONE);
                camera_action.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2==0){
                    send_button.setVisibility(View.GONE);
                    camera_action.setVisibility(View.VISIBLE);
                }else {
                    if(closeFab.getVisibility() == View.VISIBLE){
                        camera_action.setVisibility(View.GONE);
                    }else{
                        send_button.setVisibility(View.VISIBLE);
                        camera_action.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        send_button.setOnClickListener(v -> {
            if(message.getText().toString().isEmpty()){
                Toast.makeText(ChatActivity.this, "Type a Message to send", Toast.LENGTH_SHORT).show();
            }else {
                Date currentTime = Calendar.getInstance().getTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentTime);
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);
                String newMinutes = String.valueOf(minutes);
                String newHours = String.valueOf(hours);
                if(String.valueOf(minutes).length() == 1){
                    newMinutes = "0" + String.valueOf(minutes);
                }
                if(String.valueOf(hours).length() == 1){
                    newMinutes = "0" + String.valueOf(hours);
                }
                String newTime = hours + ":" + newMinutes;
                Message t= new Message(message.getText().toString().trim(), newTime, UNumber, "Message", mood);
                message.setText("");
                mood = "Neutral";
                fabList.setVisibility(View.GONE);
                SendMessage(t);
            }
        });
        camera_action.setOnClickListener(view -> {
            takePicture();
        });

        send_button.setOnLongClickListener(view -> {
            send_button.setVisibility(View.GONE);
            closeFab.setVisibility(View.VISIBLE);
            animateFab();
            return true;
        });

        closeFab.setOnClickListener(view ->{
            animateFab();
            send_button.setVisibility(View.VISIBLE);
            closeFab.setVisibility(View.GONE);
        });
        fabA.setOnClickListener(view -> {
            mood = "Shades";
            animateFab();
            send_button.setVisibility(View.VISIBLE);
            closeFab.setVisibility(View.GONE);
        });
        fabB.setOnClickListener(view -> {
           mood = "Confused";
           animateFab();
            send_button.setVisibility(View.VISIBLE);
            closeFab.setVisibility(View.GONE);
        });
        fabC.setOnClickListener(view -> {
            mood = "Laughing";
            animateFab();
            send_button.setVisibility(View.VISIBLE);
            closeFab.setVisibility(View.GONE);
        });
        fabD.setOnClickListener(view -> {
            mood = "Tensed";
            animateFab();
            send_button.setVisibility(View.VISIBLE);
            closeFab.setVisibility(View.GONE);
        });
        fabE.setOnClickListener(view -> {
            mood = "Happy";
            animateFab();
            send_button.setVisibility(View.VISIBLE);
            closeFab.setVisibility(View.GONE);
        });
    }

    private void animateFab(){

        if(isOpen){
            fabList.setVisibility(View.GONE);
            fabA.startAnimation(fabClose);
            fabA.startAnimation(rotateForward);
            fabA.setClickable(false);
            fabB.startAnimation(fabClose);
            fabB.startAnimation(rotateForward);
            fabA.setClickable(false);
            fabC.startAnimation(fabClose);
            fabC.startAnimation(rotateForward);
            fabA.setClickable(false);
            fabD.startAnimation(fabClose);
            fabD.startAnimation(rotateForward);
            fabA.setClickable(false);
            fabE.startAnimation(fabClose);
            fabE.startAnimation(rotateForward);
            fabA.setClickable(false);
            isOpen = false;
        }else{
            fabList.setVisibility(View.VISIBLE);
            fabA.startAnimation(fabOpen);
            fabA.startAnimation(rotateBackward);
            fabA.setClickable(true);
            fabB.startAnimation(fabOpen);
            fabB.startAnimation(rotateBackward);
            fabA.setClickable(true);
            fabC.startAnimation(fabOpen);
            fabC.startAnimation(rotateBackward);
            fabA.setClickable(true);
            fabD.startAnimation(fabOpen);
            fabD.startAnimation(rotateBackward);
            fabA.setClickable(true);
            fabE.startAnimation(fabOpen);
            fabE.startAnimation(rotateBackward);
            fabA.setClickable(true);
            isOpen = true;
        }
    }

    private void takePicture() {
        Random random = new Random();
        int key = random.nextInt(1000);
        File photo = new File(Environment.getExternalStorageDirectory(), UNumber+"picture"+key+".jpg");
        imageUri = Uri.fromFile(photo);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try{
                Scanner scanner = new Scanner();
                final Bitmap bitmap = scanner.decodeBitmapUri(ChatActivity.this, imageUri);
                StorageReference myrefernce = FirebaseStorage.getInstance().getReference();
                StorageReference filepath = myrefernce.child("photos").child(UNumber).child(imageUri.getLastPathSegment());
                filepath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(ChatActivity.this,"uploaded",Toast.LENGTH_LONG).show();
                    Date currentTime = Calendar.getInstance().getTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentTime);
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
                    int minutes = calendar.get(Calendar.MINUTE);
                    int seconds = calendar.get(Calendar.SECOND);
                    String newMinutes = String.valueOf(minutes);
                    if(String.valueOf(minutes).length() == 1){
                        newMinutes = "0" + String.valueOf(minutes);
                    }
                    String newTime = hours + ":" + newMinutes;
                    Message image = new Message(String.valueOf(taskSnapshot.getDownloadUrl()), newTime, UNumber, "Image" );
                    SendImage(image);
                    // create a message object
                });
            }catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();

            }
        }
    }

    private void SendImage(Message t){

            HashMap<String, String> completeMessage = new HashMap<>();
            completeMessage.put("Type", t.getType());
            completeMessage.put("url", String.valueOf(t.getUrl()));
            completeMessage.put("Time", t.getTime());
            completeMessage.put("number", UNumber);

            HashMap<String, String> recentMessage = new HashMap<>();
            recentMessage.put("number",MNumber);
            recentMessage.put("message", "Image");

            HashMap<String, String> oRecentMessage = new HashMap<>();
            oRecentMessage.put("number", UNumber);
            oRecentMessage.put("message", "Image");

            HashMap<String, String> notification = new HashMap<>();
            notification.put("Sender", UNumber);
            notification.put("Message", "Image");

            messageDatabase.push().setValue(completeMessage);
            oMessageDatabase.push().setValue(completeMessage);
            chatsReference.child(MNumber).setValue(recentMessage);
            ochatsReference.child(UNumber).setValue(oRecentMessage);
            notificationsReference.child(MNumber).push().setValue(notification);

    }

    private void SendMessage(Message t){
        HashMap<String, String> completeMessage = new HashMap<>();
        completeMessage.put("Type", "Message");
        completeMessage.put("Message", t.getMessage());
        completeMessage.put("Time", t.getTime());
        completeMessage.put("number", UNumber);
        completeMessage.put("mood", t.getMood());

        HashMap<String, String> recentMessage = new HashMap<>();
        recentMessage.put("number",MNumber);
        recentMessage.put("message",t.getMessage());

        HashMap<String, String> oRecentMessage = new HashMap<>();
        oRecentMessage.put("number", UNumber);
        oRecentMessage.put("message", t.getMessage());

        HashMap<String, String> notification = new HashMap<>();
        notification.put("Sender", UNumber);
        notification.put("Message", t.getMessage());

        messageDatabase.push().setValue(completeMessage);
        oMessageDatabase.push().setValue(completeMessage);
        chatsReference.child(MNumber).setValue(recentMessage);
        ochatsReference.child(UNumber).setValue(oRecentMessage);
        notificationsReference.child(MNumber).push().setValue(notification);
    }

    private void launchMediaScanIntent() {
        Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaIntent.setData(imageUri);
        this.sendBroadcast(mediaIntent);
    }

    private void loadMessages() {
        conversation.clear();
        messageDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                conversation.add(m);
                conversationAdapter.notifyDataSetChanged();
                conversation_list.smoothScrollToPosition(conversation.size()-1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        message.requestFocus();
        loadMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(fabList.getVisibility()==View.VISIBLE){
            fabList.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }

}
