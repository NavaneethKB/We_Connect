package com.example.weconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import adapter.CommentAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Comment;
import model.user;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment>commentList;
     private EditText addComment;
     private CircleImageView imageProfile;
     private TextView post;
     private String postId;
     private String authorId;
     FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent=getIntent();
        postId= intent.getStringExtra("postId");
        authorId=intent.getStringExtra("authorId");
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,commentList,postId);
         recyclerView.setAdapter(commentAdapter);
        addComment=findViewById(R.id.add_comment);
       imageProfile=findViewById(R.id.image_profile);
        post=findViewById(R.id.post);



        fUser= FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString())){
                    Toast.makeText(CommentActivity.this,"No Comment Added",Toast.LENGTH_SHORT).show();
                }else {
                    putComment();

                }
            }
        });

      getComment();

    }

    private void getComment() {
     FirebaseDatabase.getInstance().getReference().child("comments").child(postId).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             commentList.clear();
             for(DataSnapshot snapshots: snapshot.getChildren()){
                 Comment comment=snapshots.getValue(Comment.class);
                 commentList.add(comment);
             }
             commentAdapter.notifyDataSetChanged();
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });

    }


    private void putComment() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        String id=ref.push().getKey();
        HashMap<String,Object>map=new HashMap<>();
        map.put("comment",addComment.getText().toString());
        map.put("publisher",fUser.getUid());
        map.put("id",id);
        addComment.setText(" ");
    ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CommentActivity.this,"Comment Added",Toast.LENGTH_SHORT).show();



                }else{
                    Toast.makeText(CommentActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserImage() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user User=snapshot.getValue(user.class);
               if(User.getImageurl()==("default")){
                   imageProfile.setImageResource(R.drawable.person);
               }else{
                Picasso.get().load(User.getImageurl()).into(imageProfile);
                }
           }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}