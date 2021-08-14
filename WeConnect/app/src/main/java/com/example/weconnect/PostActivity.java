package com.example.weconnect;



import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private ImageView close;
    private String imageURl;
    private ImageView imageAdded;
    private TextView post;
    SocialAutoCompleteTextView description;
    private Uri imageuri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close=findViewById(R.id.close);
        imageAdded=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        CropImage.activity().start(PostActivity.this);

    }

    private void upload() {
        final ProgressDialog PD=new ProgressDialog(this);
        PD.setMessage("Uploading....");
        PD.show();

        if(imageuri!=null){
            final StorageReference filePath= FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
            StorageTask uploadtask=filePath.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUrl=task.getResult();
                    imageURl=downloadUrl.toString();
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
                    String postId=ref.push().getKey();
                    HashMap<String,Object>map=new HashMap();
                    map.put("postid",postId);
                    map.put("imageurl",imageURl);
                    map.put("description",description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    ref.child(postId).setValue(map);

                    DatabaseReference hashTagRef=FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hashtags=description.getHashtags();
                    if(!hashtags.isEmpty()){
                        for(String tag:hashtags){
                            map.clear();
                            map.put("tag",tag.toLowerCase());
                            map.put("postID",postId);
                            hashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }
                    }
                    PD.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });


        }else{
            Toast.makeText(PostActivity.this,"Image was not selected",Toast.LENGTH_SHORT);
        }
    }

    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri=result.getUri();
            imageAdded.setImageURI(imageuri);
        }else{
            Toast.makeText(PostActivity.this ,"Image Upload not Sucessful! Try Again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ArrayAdapter<Hashtag>hashtagAdapter=new HashtagArrayAdapter<>(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshots:snapshot.getChildren()){
                    hashtagAdapter.add(new Hashtag(snapshot.getKey(),(int) snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        description.setHashtagAdapter(hashtagAdapter);
    }
}