package com.mumbi.seldomlimited;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;


public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
    private static final String DOMAIN_NAME = "gmail.com";

    private Button button;
    private EditText input_email, input_password, confirmP;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        button = (Button) findViewById(R.id.register);
        input_email = (EditText) findViewById(R.id.email);
        input_password = (EditText)findViewById(R.id.password);
        confirmP = (EditText) findViewById(R.id.conf_pass);
        progressBar = (ProgressBar) findViewById(R.id.R_progressBar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Onclick : attempting to register" );

                //check for null values
                if(!isEmpty(input_email.getText().toString()) && !isEmpty(input_password.getText().toString())
                  && !isEmpty(confirmP.getText().toString())) {

                    //check if user has a company email address
                    if (isValidDomain(input_email.getText().toString())) {

                        //check if passwords match
                        if (doStringsMatch(input_password.getText().toString(), confirmP.getText().toString())) {
                            //initiate registration task
                            RegisterNewEmail(input_email.getText().toString(), input_password.getText().toString());

                        } else {
                            Toast.makeText(Register.this, "passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Register.this, "Please register with the company's mail", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Register.this,"fill out all the fields", Toast.LENGTH_SHORT).show();

                 }
                

            }
        });
        hideSoftKeyboard();
    }


    public void RegisterNewEmail(String user_email, String pass){
        showDialog();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user_email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onComplete: AuthState " + task.isSuccessful());
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                   //send Verification Email
                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    //Redirect the user to the Login Screen
                    redirectLoginScreen();
                    
                }
                if (!task.isSuccessful()){
                    Toast.makeText(Register.this, "Unable to register", Toast.LENGTH_SHORT).show();
                }
                hideDialog();

            }
        });

    }
/*
send verification email to user
 */
    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Register.this, "Sent verification Email", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Register.this, "Couldn't send verification Email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void hideDialog() {
        if(progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void redirectLoginScreen() {
     Log.d(TAG, "redirectingLoginScreen: redirecting to login screen.");
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void showDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private boolean doStringsMatch(String s1, String s2) {

        return s1.equals(s2);

    }
    //Returns true if the user's domain name contains @seldom.com

    private boolean isValidDomain(String em) {
    Log.d(TAG, "isValidDomain: verifying email has correct domain: " +em);
    String domain = em.substring(em.indexOf("@") +1).toLowerCase();
        Log.d(TAG, "isValidDomain: users domain: " + domain);
        return domain.equals(DOMAIN_NAME);

        
    }

    /*
    Return true if the @param is null

     */
    private boolean isEmpty(String string){
        return string.equals("");
    }
    private void hideSoftKeyboard() {
       this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


}
