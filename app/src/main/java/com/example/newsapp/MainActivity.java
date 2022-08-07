package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extras=getIntent().getExtras();
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

            if(mUser!=null||extras!=null){
                Toast.makeText(this,mUser.getDisplayName(),Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,new NewsFragment())
                        .addToBackStack(null).commit();

            }

        else{
            FragmentManager fragmentManager=getSupportFragmentManager();
            Fragment fragment=new LoginFrag();
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).addToBackStack(null)
                    .commit();
        }

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

}