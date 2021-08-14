package com.example.weconnect;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.MessageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Chat;
import model.user;

public class ChatUsersActivity extends AppCompatActivity {
 private CircleImageView imageView;
 private TextView name;
 private EditText chat;
 private ImageView sent;
 private List<Chat> mChat;
 private FirebaseUser FUser;
 private DatabaseReference reference;
  MessageAdapter messageAdapter;
RecyclerView chatList;
String ID;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);
        final String UserId=getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        String UserName=getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("username", "none");

        ID=UserId;


        imageView=findViewById(R.id.profileimagee);
        name=findViewById(R.id.fullnamee);
        chat=findViewById(R.id.textt);
        sent=findViewById(R.id.sent);
        chatList=findViewById(R.id.listt);
        FUser= FirebaseAuth.getInstance().getCurrentUser();
        chatList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        chatList.setLayoutManager(linearLayoutManager);

        name.setText(UserName);

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=chat.getText().toString();
                if(msg.isEmpty()){
                    Toast.makeText(ChatUsersActivity.this,"Type Something!",Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(FUser.getUid(),ID,msg);
                    chat.setText("");
                }
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user User = snapshot.getValue(user.class);


                if ((User.getImageurl()).equals("default")) {
                    Picasso.get().load(R.drawable.person).into(imageView);
                } else {

                    Picasso.get().load(User.getImageurl()).into(imageView);
                }
            readMessages(FUser.getUid(),ID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
    private void sendMessage(String sender,String receiver,String message){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        String id=ref.push().getKey();
        HashMap<String,Object>map=new HashMap<>();
        map.put("sender",sender);
        map.put("receiver",receiver);
        map.put("message",message);
        map.put("id",id);

        ref.child("Chats").child(id).setValue(map);


    }
    private void readMessages(final String myId, final String userId){

        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot snapshots:snapshot.getChildren()){
                    Chat chat=snapshots.getValue(Chat.class);
                    if(chat.getReceiver().equals(myId )&&(chat.getSender().equals(userId))||chat.getReceiver().equals(userId )&&(chat.getSender().equals(myId))){
                        mChat.add(chat);
                    }
                    messageAdapter=new MessageAdapter(ChatUsersActivity.this,mChat);
                    chatList.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    }