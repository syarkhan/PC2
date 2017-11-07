package com.example.sheryarkhan.projectcity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sheryarkhan.projectcity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.txtEmail);
        password = (EditText) findViewById(R.id.txtPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            FirebaseMessaging.getInstance().subscribeToTopic("user_"+firebaseAuth.getCurrentUser().getUid());
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public  void BtnRegisterIntentOnClick(View view){
        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    }

    public void BtnLoginOnClick(View view){

        String strEmail =  email.getText().toString().trim();
        String strPassword = password.getText().toString().trim();

        if(TextUtils.isEmpty(strEmail)){
            Toast.makeText(getApplicationContext(),"Please Enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(strPassword)){
            Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    finish();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }else {
                    progressDialog.dismiss();
                    try {
                        throw task.getException();
                    }  catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(getApplicationContext(), "please enter valid information", Toast.LENGTH_LONG).show();

                    }  catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();

                    }catch (FirebaseNetworkException e) {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



    }

}
