package com.unipi.cbarbini.mywallchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private Button lgn_button;

    //Firebase
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email= findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        lgn_button =findViewById(R.id.button);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        lgn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stored_email = email.getText().toString();
                String stored_password = password.getText().toString();
                //Check fields
                if(stored_email.isEmpty())
                {   email.setError("Please enter this field");
                    email.requestFocus();
                }
                else if(stored_password.isEmpty())
                {   password.setError("Please enter this field");
                    password.requestFocus();
                }
                else login(stored_email,stored_password);

            }
        });
    }
    public void redirect_reg(View view)
    {
        startActivity(new Intent(this,RegisterActivity.class));
    }
    //user login
    public void login(String email,String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(!task.isSuccessful()){
                //error
                Toast.makeText(LoginActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }else{
                startActivity(new Intent(getApplicationContext(),ChatActivity.class));
                finish();
            }
        }
    });}
}
