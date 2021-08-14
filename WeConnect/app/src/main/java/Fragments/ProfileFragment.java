package Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.EditProfileActivity;
import com.example.weconnect.FollowersActivity;
import com.example.weconnect.OptionsActivity;
import com.example.weconnect.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapter.PhotoAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Post;
import model.user;

import static com.example.weconnect.MainActivity.check;


public class ProfileFragment<chat> extends Fragment {
    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView fullname;
    private TextView bio;
    private TextView username;
    private String profileId;
    private Button editProfile;
    private ImageView myPictures;
    private ImageView savedPictures;
    private FirebaseUser fUser;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;
    private ImageView send;

    private RecyclerView recyclerViewSaves;
    private PhotoAdapter postAdapterSaves;
    private List<Post> mySavedPosts;
    private String data;
    private ImageView chat;


    private BottomNavigationView bottomNavigationView;

    int count2;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        options = view.findViewById(R.id.options);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");

        if (data.equals("none")) {
            profileId = fUser.getUid();
           options.setVisibility(View.VISIBLE);
        } else {

            if(check==1) {
                profileId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                check=0;
                options.setVisibility(View.VISIBLE);
            }else{
                profileId = data;
                check=0;


            }


        }

        imageProfile = view.findViewById(R.id.PROFILE);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        editProfile = view.findViewById(R.id.edit_profile);
        recyclerView = view.findViewById(R.id.recycler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewSaves = view.findViewById(R.id.recycler_view_save);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPosts = new ArrayList<>();
        postAdapterSaves = new PhotoAdapter(getContext(), mySavedPosts);
        recyclerViewSaves.setAdapter(postAdapterSaves);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user User = snapshot.getValue(user.class);
                if (profileId == fUser.getUid()) {
                    if ((User.getImageurl()).equals("default")) {
                        Picasso.get().load(R.drawable.person).into(imageProfile);
                    } else {
                        Picasso.get().load(User.getImageurl()).into(imageProfile);
                    }
                } else {
                    reference.child(profileId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user User = snapshot.getValue(user.class);
                            if ((User.getImageurl()).equals("default")) {
                                Picasso.get().load(R.drawable.person).into(imageProfile);
                            } else {
                                Picasso.get().load(User.getImageurl()).into(imageProfile);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();
        getSavedPosts();

        if (profileId.equals((fUser.getUid()))) {
            editProfile.setText("Edit-Profile");
        } else {
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();

                if (btnText.equals("Edit-Profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else {

                    if (btnText.equals("follow")) {

                        FirebaseDatabase.getInstance().getReference().child("follow").child(fUser.getUid()).child("following").child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("follow").child(profileId).child("followers").child(fUser.getUid()).setValue(true);

                    } else {

                        FirebaseDatabase.getInstance().getReference().child("follow").child(fUser.getUid()).child("following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("follow").child(profileId).child("followers").child(fUser.getUid()).removeValue();

                    }
                }
            }
        });


        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);

            }
        });

        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileId == fUser.getUid()) {
                    recyclerView.setVisibility(View.GONE);
                    recyclerViewSaves.setVisibility(View.VISIBLE);
                }
                }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });
        return view;

    }

    private void getSavedPosts() {
        final List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    savedIds.add(snapshots.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        mySavedPosts.clear();
                        for (DataSnapshot Snapshots : snapshot1.getChildren()) {
                            Post post = Snapshots.getValue(Post.class);
                            for (String id : savedIds) {

                                if ((post.getPostid()).equals(id)) {
                                    mySavedPosts.add(post);
                                }
                            }

                        }

                        postAdapterSaves.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    Post post = snapshots.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        myPhotoList.add(post);
                    }
                }
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    editProfile.setText("following");

                } else {
                    editProfile.setText("follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    Post post = snapshots.getValue(Post.class);

                    if (post.getPublisher().equals(fUser.getUid())) {
                        Log.i("works fine",""+ post.getPublisher()+fUser.getUid());
                        count2++;
                    }

                }
                Log.i("workss", "" + count2);
                posts.setText(("" + count2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                followers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user User = snapshot.getValue(user.class);

                username.setText(User.getUsername());
                fullname.setText(User.getName());
                bio.setText(User.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}