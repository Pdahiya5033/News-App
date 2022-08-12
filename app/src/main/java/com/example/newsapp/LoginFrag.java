package com.example.newsapp;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.concurrent.Executor;

import okhttp3.Call;

public class LoginFrag extends Fragment {
    private final String TAG="LoginFrag";
    private static final String EMAIL = "email";
    private static final int REQ_ONE_TAP = 100;
    private boolean showOneTapUI = true;
    ImageView googleImage;
    ImageView fbSignIn;
    Button loginTop,signUpTop,loginBtnBottom;
    EditText emailTVLogin,passwordTVLogin;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    BeginSignInRequest signUpRequest;
    SignInClient oneTapClient;

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
        signUpTop.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_top_not_clicked));
        signUpTop.setTextColor(Color.BLACK);
        loginTop.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_top_btn));
        loginTop.setTextColor(Color.WHITE);
        googleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        fbSignIn = view.findViewById(R.id.fb_login);
        fbSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(getContext(),FBSignInAct.class);
//                startActivity(intent);
//                Log.d(TAG,"????????????"+getActivity());
                signInWithFB();
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
// signing into google account
    private void signInWithGoogle() {
        ProgressDialog progressDialog;
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Google Sign In...");
        progressDialog.show();
        oneTapClient = Identity.getSignInClient(getActivity());
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<BeginSignInResult>() {

                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Bundle bundle=new Bundle();
                        try {
                            progressDialog.dismiss();
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    new Intent(String.valueOf(getActivity())), 0, 0, 0,bundle);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"You don't have any active google accounts",Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
    private void signInWithFB(){
        mAuth=FirebaseAuth.getInstance();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG,user.getDisplayName());
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        AuthCredential authCredential= GoogleAuthProvider.getCredential(idToken,null);
                        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            updateUI();
                                        }
                                        else{
                                            Log.d(TAG,"auth failed");
                                        }
                                    }
                                });

                    }
                } catch (ApiException e) {
                    Log.d(TAG,"exception in on activity result");
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            Log.d(TAG, "One-tap dialog was closed.");
                            // Don't re-prompt the user.
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            Log.d(TAG, "One-tap encountered a network error.");
                            // Try again or just ignore.
                            break;
                        default:
                            Log.d(TAG, "Couldn't get credential from result."
                                    + e.getLocalizedMessage());
                            break;
                    }
                }
                break;
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUI() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,new NewsFragment())
                .addToBackStack(null).commit();
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
