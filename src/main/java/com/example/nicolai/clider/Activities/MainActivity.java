package com.example.nicolai.clider.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolai.clider.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button register;
    EditText email_edit, password_edit;
    TextView alreadyUser_txt;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        alreadyUser_txt.setText(Html.fromHtml( getResources().getString(R.string.loginText) + "<b>"+ "<font color=\"#3596ff\">" + " " + getResources().getString(R.string.here) + "</b>"));
        alreadyUser_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }

    //Method for registering a user with Firebase's own method. Also checking the fields for not being empty
    //https://firebase.google.com/docs/auth/
    private void registerUser(){
        if (email_edit.getText()==null || TextUtils.isEmpty(email_edit.getText().toString())){
            email_edit.setError(getResources().getString(R.string.fill));
            return;
        }
        if (password_edit.getText()==null || TextUtils.isEmpty(password_edit.getText().toString())){
            password_edit.setError(getResources().getString(R.string.fill));
            return;
        }
        String email = email_edit.getText().toString().trim();
        String password = password_edit.getText().toString().trim();


        progressDialog.setMessage(getResources().getString(R.string.SigningUp));
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.doneRegistering), Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.failRegisterering), Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
            }
        });
    }

    private void initializeComponents() {
        register = findViewById(R.id.buttonRegister);

        email_edit = findViewById(R.id.textEmail);
        password_edit = findViewById(R.id.textPassword);

        alreadyUser_txt = findViewById(R.id.link);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

}
