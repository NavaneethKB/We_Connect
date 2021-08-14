package com.example.weconnect;



import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import model.user;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView close;
    private CircleImageView imageProfile;
    private TextView save;
    private TextView changePhoto;
    private MaterialEditText username;
    private MaterialEditText fullname;
    private MaterialEditText bio;
    private FirebaseUser fUSer;
    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        imageProfile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        changePhoto = findViewById(R.id.change_photo);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        fUSer = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child("Uploads");


        FirebaseDatabase.getInstance().getReference().child("Users").child(fUSer.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user User = snapshot.getValue(user.class);
                fullname.setText(User.getName());
                username.setText(User.getUsername());
                bio.setText(User.getBio());
                Picasso.get().load(User.getImageurl()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              startActivity(new Intent(EditProfileActivity.this,MainActivity.class));

            }
        });
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                startActivity(new Intent(EditProfileActivity.this,MainActivity.class));

            }
        });
    }

    private void updateProfile() {
        HashMap<String,Object>map=new HashMap<>();
        map.put("name",fullname.getText().toString());
        map.put("username",username.getText().toString());
        map.put("bio",bio.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUSer.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this,"Changes Applied Sucessfully!",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(EditProfileActivity.this,"Oops Something Went Wrong ",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void uploadImage() {
        final ProgressDialog PD=new ProgressDialog(this) ;
        PD.setMessage("Uploading...");
        PD.show();
        if(mImageUri!=null){
            final StorageReference fileRef=storageRef.child(System.currentTimeMillis()+".jpeg");
            uploadTask=fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult() ;
                        String url=downloadUri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUSer.getUid()).child("imageurl").setValue(url);
                        PD.dismiss();
                    }else{
                        Toast.makeText(EditProfileActivity.this,"Oops!!Upload Failed :-(",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(EditProfileActivity.this,"Image Not Selected",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            uploadImage();
        } else {
            Toast.makeText(EditProfileActivity.this, "Something Went Wrong!Please Try Again", Toast.LENGTH_SHORT).show();
        }


    }
}