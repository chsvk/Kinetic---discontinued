package tech.steampunk.kinetic.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.steampunk.kinetic.R;

public class LoginActivity extends Activity {

    @BindView(R.id.Login) EditText login;
    @BindView(R.id.Password)EditText Password;
    @BindView(R.id.Register) Button Register;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mAuthReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login.getText().toString().isEmpty()||Password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please Fill The Required Fields", Toast.LENGTH_SHORT).show();
                }else if(login.getText().toString().trim().length()<10 || login.getText().toString().trim().length()>10){
                    Toast.makeText(LoginActivity.this, "Please Enter a Valid Number!", Toast.LENGTH_SHORT).show();
                }else{
                    final String l = login.getText().toString().trim() + "@kinetic.com";
                    mAuth.createUserWithEmailAndPassword(l,Password.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String uid = user.getUid();
                                        String token = FirebaseInstanceId.getInstance().getToken();
                                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child( "+91" + login.getText().toString().trim()).child("Profile");
                                        HashMap<String, String> userMap = new HashMap<>();
                                        userMap.put("Name", "Default");
                                        userMap.put("Number", "+91" + login.getText().toString().trim());
                                        userMap.put("Status", "On The Move, Its Kinetic!");
                                        userMap.put("DP", "Default_URL");
                                        userMap.put("ThumbNail", "Default_Thumb");
                                        userMap.put("Birthday", "Default");
                                        userMap.put("UID", uid);
                                        userMap.put("DeviceToken", token);
                                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
                                                    editor.putString("Number", "+91" + login.getText().toString().trim());
                                                    editor.putString("Name", "Default");
                                                    editor.putString("UID",mAuth.getCurrentUser().getUid());
                                                    editor.putString("Status", "On The Move, Its Kinetic!");
                                                    editor.putString("Contacts", "Blank");
                                                    editor.apply();
                                                    Toast.makeText(LoginActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                                // LATER ADD SAFETY MEASURES IN CASE THIS FAILS
                                            }
                                        });

                                    }else {
                                        FirebaseAuthException e = (FirebaseAuthException )task.getException();
//                                        Toast.makeText(LoginActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        String message = "The email address is already in use by another account.";
                                        if(e.getMessage()==message){
                                            mAuth.signInWithEmailAndPassword(l,Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){
                                                        String token = FirebaseInstanceId.getInstance().getToken();
                                                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child( "+91" + login.getText().toString().trim()).child("Profile");
                                                        mDatabase.child("DeviceToken").setValue(token);
                                                        SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
                                                        editor.putString("Number", "+91" + login.getText().toString().trim());
                                                        editor.putString("UID",mAuth.getCurrentUser().getUid());
                                                        editor.putString("Name", "Default");
                                                        editor.putString("Status", "On The Move, Its Kinetic!");
                                                        editor.putString("DeviceToken", token);
                                                        editor.putString("Contacts", "Blank");
                                                        editor.apply();
                                                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                        return;
//                                        Toast.makeText(LoginActivity.this, "Registration Failed! Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }


    @Override
    protected void onStart() {
        login.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        super.onStart();
    }

    @Override
    protected void onStop() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        super.onStop();
    }

    @Override
    protected void onPause() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        super.onDestroy();
    }
}
