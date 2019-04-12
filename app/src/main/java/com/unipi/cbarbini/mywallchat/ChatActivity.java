package com.unipi.cbarbini.mywallchat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.unipi.cbarbini.mywallchat.R.*;

public class ChatActivity extends AppCompatActivity {
    private EditText message;
    private Button send_message,btn_logout,btn_voice;
    private ListView listview_messages;
    private FirebaseListAdapter<GroupChat> adapter;

    //Firebase
    private DatabaseReference dbref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_chat);

        message = findViewById(id.editText);
        send_message = findViewById(id.button);
        btn_logout = findViewById(id.button2);
        listview_messages = findViewById(id.listview);
        btn_voice=findViewById(id.btn_voice);

        DisplayMessages();

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stored_message = message.getText().toString();
                if (stored_message.isEmpty())
                    Toast.makeText(getApplicationContext(), "Hey!Send a message to your friends", Toast.LENGTH_SHORT).show();
                else sendMessage(stored_message);
                // Clear the input
                message.setText("");
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        //voice message
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please speak!");
                startActivityForResult(intent,123);
            }
        });
    }

    public void sendMessage(String stored_message) {
        dbref = FirebaseDatabase.getInstance().getReference().child("Messages");
        GroupChat message = new GroupChat(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), stored_message);
        dbref.push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    //error
                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    DisplayMessages();
                }
            }
        });
    }

    public void DisplayMessages() {
      //refer to messages
     dbref=FirebaseDatabase.getInstance().getReference().child("Messages");

        adapter = new FirebaseListAdapter<GroupChat>(this,GroupChat.class,layout.listviewme,FirebaseDatabase.getInstance().getReference().child("Messages")) {

            @Override
            protected void populateView(View v,final GroupChat model, int position) {
                //listviewme.xml layout file

                TextView txt_user = (TextView)v.findViewById(id.textView);
                TextView txt_me= (TextView)v.findViewById(id.textView5);
                TextView txt_message = (TextView)v.findViewById(id.textView2);
                TextView txt_time = (TextView)v.findViewById(id.textView3);
                RelativeLayout relativeLayout=(RelativeLayout)v.findViewById(id.relativelayout);
                ImageView senderimage=(ImageView) v.findViewById(id.senderimage);
                ImageView groupusersimage=(ImageView) v.findViewById(id.groupusersimage);

                //populate view my message
                if (model.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()))
                {
                    relativeLayout.setBackgroundColor(Color.parseColor("#e6e2ff"));
                    groupusersimage.setVisibility(View.GONE);
                    senderimage.setVisibility(View.VISIBLE);

                    txt_me.setText("Me");
                    txt_user.setText("");

                }
                //populate view others users messages
                else
                {   relativeLayout.setBackgroundColor(Color.parseColor("#f2d4ce"));
                    senderimage.setVisibility(View.GONE);
                    groupusersimage.setVisibility(View.VISIBLE);

                    txt_user.setText(model.getUser());
                    txt_me.setText("");

                }
                txt_message.setText(model.getMessage());
                txt_time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));

            }
        };

        listview_messages.setAdapter(adapter);
    }
    //method for voice messaging
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==123 && resultCode==RESULT_OK){
            ArrayList<String> matches =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String result = matches.get(0);
            message.setText(result);
        }
    }
    //signout
    private void LogOut()
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
}