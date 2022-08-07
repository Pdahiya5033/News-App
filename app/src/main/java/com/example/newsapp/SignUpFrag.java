package com.example.newsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class SignUpFrag extends Fragment {
    private final String TAG="SignUpFrag";
    private Button loginTop,signUpTop,register;
    private EditText nameET,emailET,phoneET,passwordET;
    private CheckBox termsAndCond;
    private TextView signInTV;
    private FirebaseAuth mAuth;
    private CountryCodePicker countryCodePicker;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle savedInstanceState){
        View view=layoutInflater.inflate(R.layout.signup_fragment,parent,false);
        mAuth=FirebaseAuth.getInstance();
        signUpTop=view.findViewById(R.id.signUpBtnTopFragSignup);
        loginTop=view.findViewById(R.id.loginBtnTopFragSignup);
        nameET=view.findViewById(R.id.nameETSignUp);
        emailET=view.findViewById(R.id.emailETSignUp);
        phoneET=view.findViewById(R.id.phoneETSignup);
        passwordET=view.findViewById(R.id.passwordETSignUp);
        termsAndCond=view.findViewById(R.id.tandcCheck);
        register=view.findViewById(R.id.registerBtnSignUp);
        signInTV=view.findViewById(R.id.signInTextCAFrag);
        countryCodePicker=view.findViewById(R.id.countryCodePicker);
        loginTop.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_top_not_clicked));
        loginTop.setTextColor(Color.BLACK);
        signUpTop.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_top_btn));
        signUpTop.setTextColor(Color.WHITE);
        loginTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new LoginFrag();
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,fragment)
                        .addToBackStack(null).commit();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameET.getText().toString();
                String textEmail=emailET.getText().toString();
                String textPassword=passwordET.getText().toString();
                String textPhone=phoneET.getText().toString();
                if(TextUtils.isEmpty(textEmail)||TextUtils.isEmpty(textPassword)||TextUtils.isEmpty(textPhone)||!termsAndCond.isChecked()||
                TextUtils.isEmpty(name)){
                    Toast.makeText(getContext(),"empty credentials",Toast.LENGTH_SHORT).show();
                }
                else if(textPassword.length()<6){
                    Toast.makeText(getContext(),"Password too short",Toast.LENGTH_SHORT).show();
                }
                else if(textPhone.length()<10){
                    Toast.makeText(getContext(),"invalid contact no.",Toast.LENGTH_SHORT).show();
                }
                else{
                    registeredUser(textEmail,textPassword);
                }
            }
        });
        signInTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,new LoginFrag())
                        .addToBackStack(null).commit();
            }
        });
        return view;
    }

    private void registeredUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(),"Registered Successfully",Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().replace(R.id.fragment_container,new NewsFragment())
                                    .addToBackStack(null).commit();
                        }
                        else{
                            Toast.makeText(getContext(),"registration failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
