package tech.steampunk.kinetic.UI;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import tech.steampunk.kinetic.R;

public class AccountActivity extends AppCompatActivity {

    private Toolbar toolbar;
    @BindView(R.id.account_background)ImageView accountBackground;
    @BindView(R.id.account_dp)CircleImageView accountDp;
    @BindView(R.id.account_name)TextView accountName;
    @BindView(R.id.account_status)TextView accountStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        toolbar = findViewById(R.id.account_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        try{
            if(sharedPreferences.getString("Name", " ").isEmpty()){
                accountName.setText("Default");
            }else{
                accountName.setText(sharedPreferences.getString("Name", ""));
            }
            accountStatus.setText(sharedPreferences.getString("Status", ""));
        }catch (Exception e){

        }

    }
}
