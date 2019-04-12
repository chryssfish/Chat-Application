package com.unipi.cbarbini.mywallchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText name,email,password;
    private Button reg_button;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference ref_user;
    private DatabaseReference ref_username;

    private boolean UsernameExists=false;

    String stored_username,stored_email,stored_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.editText);
        email = findViewById(R.id.editText2);
        password = findViewById(R.id.editText3);
        reg_button =findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stored_username = name.getText().toString();
                stored_email = email.getText().toString();
                stored_password = password.getText().toString();

                //Check fields
                if(stored_username.isEmpty())
                {name.setError("enter this field");
                 name.requestFocus();
                }
                else if(stored_email.isEmpty())
                {email.setError("enter this field");
                 email.requestFocus();
                }
                else if (!isEmailValid(stored_email))
                {email.setError("Your email is invalid.Format : example@example.com");
                 email.requestFocus();
                }
                else if(stored_password.isEmpty())
                {password.setError("enter this field");
                 password.requestFocus();
                }
                else if(stored_password.length()<6)
                {password.setError("Password must contain at least 6 characters");
                 password.requestFocus();
                }
                else
                {
                    checkUsername(stored_username);
                }
                }
        });
    }
    //authentication of database & set username as Display name for user
    private void signUpUserFirebase(final String username,final String email, final String password){
    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(!task.isSuccessful()){
                //error
                Toast.makeText(getApplicationContext(), "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }else{
                final FirebaseUser newUser = task.getResult().getUser();
                //display username in order to use it later
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();

                newUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    //create user in firebase
                                    StoreUsername(username); //store unique username
                                    RegisterUser(username,email,password);//store a user

                                }else{
                                    //error
                                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }
    });
}
    //store a user
    private void RegisterUser(String username, String email, String password)
    {
        //refer to Users
        ref_user = FirebaseDatabase.getInstance().getReference().child("Users");
        User user = new User(username,email,password);
        ref_user.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    //error
                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }
            }
        });
    }
    //store username so as to know if a username is unique
    public void StoreUsername(final String username)
    {
        FirebaseDatabase.getInstance().getReference().child("Usernames").child(username).setValue("true", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getApplicationContext(),"Something went wrong!Try again...", Toast.LENGTH_SHORT).show();

                }
            }});


    }
    //Check if username exists
    public boolean checkUsername(final String username)
    {
        ref_username = FirebaseDatabase.getInstance().getReference();
        ref_username.child("Usernames").child(username).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        Toast.makeText(getApplicationContext(), "This username already exists.Please select another username!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        signUpUserFirebase(stored_username,stored_email,stored_password); //sign up user
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    return UsernameExists;
    }
    //email format
    private boolean isEmailValid(CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
