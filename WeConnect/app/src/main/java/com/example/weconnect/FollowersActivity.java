package com.example.weconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.UserAdapter;
import model.user;

public class FollowersActivity extends AppCompatActivity {
       private  String id;
       private String title;
       private List<String>idList;




       RecyclerView recyclerView;
       private UserAdapter userAdapter;
       private List<user>mUsers;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);


        Intent intent=getIntent();
        id=intent.getStringExtra("id");

        title=intent.getStringExtra("title");

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers=new ArrayList<>();
        userAdapter=new UserAdapter(this,mUsers,false,false);
        recyclerView.setAdapter(userAdapter);


        idList=new ArrayList<>();

        switch (title){
            case"followers":
                getFollowers();
                break;
            case"following":
                getFollowing();
                break;
            case"likes":
                getlikes();
                Log.i("works fine","check like 1   "+id);

                break;


        }




    }

    private void getFollowing() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(id).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();

                for(DataSnapshot snapshots :snapshot.getChildren()){
                    idList.add(snapshots.getKey());
                }
                showUsers();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(id).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot snapshots :snapshot.getChildren()){
                    idList.add(snapshots.getKey());
                }
                showUsers();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getlikes() {

        FirebaseDatabase.getInstance().getReference().child("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                 idList.clear();
                 for(DataSnapshot snapshots :snapshot.getChildren()){
                     idList.add(snapshots.getKey());

                 }
                 showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot snapshots : snapshot.getChildren()){
                    user User=snapshots.getValue(user.class);
                    for(String id :idList){
                        if((User.getId()).equals(id)){
                            mUsers.add(User);
                        }
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}