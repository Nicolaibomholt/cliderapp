package com.example.nicolai.clider.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolai.clider.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText email_login, password_login;
    Button login;
    TextView sigin;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Reference: https://stackoverflow.com/questions/4032676/how-can-i-change-the-color-of-a-part-of-a-textview?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        initializeComponents();
        sigin.setText(Html.fromHtml( getResources().getString(R.string.signUpText) + "<b>"+ "<font color=\"#449eff\">" + " " + getResources().getString(R.string.here) + "</b>"));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logUserIn();
            }
        });
        sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    //Method for logging user in with Firebase own method - also does checks for fields and creating a progresdialog while waiting for the firebase response.
    //https://firebase.google.com/docs/auth/
    private void logUserIn(){
        String email = email_login.getText().toString().trim();
        String password = password_login.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            email_login.setError(getResources().getString(R.string.fill));
        }
        if (TextUtils.isEmpty(password)){
            password_login.setError(getResources().getString(R.string.fill));
        }
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
        progressDialog.setMessage(getResources().getString(R.string.logginIn));
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    finish();
                    startActivity(new Intent(getApplicationContext(), UserActivity.class));

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.unknownUser), Toast.LENGTH_LONG).show();
            }
        });
    }
    }

    private void initializeComponents() {
        email_login = findViewById(R.id.textEmailLogin);
        password_login = findViewById(R.id.textPasswordLogin);
        login = findViewById(R.id.buttonLogin);
        sigin = findViewById(R.id.linkLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }
}
