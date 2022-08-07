package com.example.newsapp;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginFrag extends Fragment {
    private final String TAG="LoginFrag";
    private static final String EMAIL = "email";
    ImageView googleImage;
    ImageView fbSignIn;
    Button loginTop,signUpTop,loginBtnBottom;
    EditText emailTVLogin,passwordTVLogin;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    private TextView registerTV,forgotPassTV;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent,Bundle savedInstanceState){
        callbackManager = CallbackManager.Factory.create();
        View view=layoutInflater.inflate(R.layout.login_fragment,parent,false);
        mAuth=FirebaseAuth.getInstance();
        googleImage=view.findViewById(R.id.google_image);
        fbSignIn=view.findViewById(R.id.fb_login);
        loginTop=view.findViewById(R.id.loginBtnTopFragLogin);
        signUpTop=view.findViewById(R.id.signUpBtnTopFragLogin);
        registerTV=view.findViewById(R.id.registerTVlogin);
        loginBtnBottom=view.findViewById(R.id.loginBtnBottom);
        emailTVLogin=view.findViewById(R.id.emailEditTextLogin);
        passwordTVLogin=view.findViewById(R.id.passwordETLogin);
        googleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getContext(),GoogleSignInAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                Log.d(TAG,"????????????"+getActivity());
            }
        });

        fbSignIn = view.findViewById(R.id.fb_login);
        fbSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),FBSignInAct.class);
                startActivity(intent);
                Log.d(TAG,"????????????"+getActivity());

            }
        });
        signUpTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new SignUpFrag();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        loginBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(emailTVLogin.getText().toString()) || TextUtils.isEmpty(passwordTVLogin.getText().toString())) {
                    Toast.makeText(getContext(),"Empty fields",Toast.LENGTH_SHORT).show();
                }
                else
                    loginUser(emailTVLogin.getText().toString(),passwordTVLogin.getText().toString());
            }
        });
        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new SignUpFrag())
                        .addToBackStack(null).commit();
            }
        });
        return view;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getActivity(),"Login Successful",Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,new NewsFragment())
                        .addToBackStack(null).commit();
            }

        });
        mAuth.signInWithEmailAndPassword(email,password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Invalid credentials",Toast.LENGTH_SHORT).show();
                emailTVLogin.setText("");
                passwordTVLogin.setText("");
            }
        });
    }

}
