package adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.CommentActivity;
import com.example.weconnect.FollowersActivity;
import com.example.weconnect.MainActivity;
import com.example.weconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import Fragments.PostDetailFragment;
import Fragments.ProfileFragment;
import model.Post;
import model.user;
import static com.example.weconnect.MainActivity.check;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    private Context mContext;
    private List<Post>mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mcontext, List<Post> mPosts) {
        this.mContext = mcontext;
        this.mPosts = mPosts;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new Viewholder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {
final Post post=mPosts.get(position);


        if((post.getImageurl()).equals("default")) {
            Log.i("postworks",""+post.getImageurl());
            Picasso.get().load(R.drawable.person).into(holder.post_image);
            holder.description.setText(post.getDescription());
        }else{
            Picasso.get().load(post.getImageurl()).into(holder.post_image);
            holder.description.setText(post.getDescription());
        }
        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user User=snapshot.getValue(user.class);
                    if (User.getImageurl().equals("default")) {
                        Picasso.get().load(R.drawable.person).into(holder.imageProfile);
                    }else{
                        Picasso.get().load(User.getImageurl()).into(holder.imageProfile);

                    }


                holder.username.setText(User.getUsername());
                holder.author.setText(User.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        isLiked(post.getPostid(),holder.like);
        noOfLikes(post.getPostid(), holder.noOfLikes);
        getComments(post.getPostid(),holder.noOfComments);
        isSaved(post.getPostid(),holder.save);
       holder.like.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if(holder.like.getTag()=="like"){
                   FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                  addNotification(post.getPostid(),post.getPublisher());

               }else{
                   FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                 FirebaseDatabase.getInstance().getReference().child("Notifications").child(post.getPublisher()).removeValue();
               }
           }
       });
    holder.comment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(mContext, CommentActivity.class);
            intent.putExtra("postId",post.getPostid());
            intent.putExtra("authorId",post.getPublisher());
            mContext.startActivity(intent);

        }
    });
holder.noOfComments.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(mContext, CommentActivity.class);
        intent.putExtra("postId",post.getPostid());
        intent.putExtra("authorId",post.getPublisher());
        mContext.startActivity(intent);
    }
});
   holder.save.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           if(holder.save.getTag()=="save"){
               FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
           }else{
               FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).removeValue();

           }
       }
   });

   holder.imageProfile.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
          check=0;
           mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

           ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

       }
   });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               check=0;
                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                Intent intent2=new Intent(mContext, MainActivity.class);
                intent2.putExtra("publisherId2","JUST TO MAKE THINGS WORK!");

            }
        });
        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check=0;
                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                Intent intent2=new Intent(mContext, MainActivity.class);
                intent2.putExtra("publisherId2","JUST TO MAKE THINGS WORK!");

            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postid",post.getPostid()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment()).commit();
                Intent intent2=new Intent(mContext, MainActivity.class);
                intent2.putExtra("publisherId2","JUST TO MAKE THINGS WORK!");

            }
        });

  holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Intent intent=new Intent(mContext, FollowersActivity.class);
          intent.putExtra("id",post.getPostid());
          intent.putExtra("title","likes");
          mContext.startActivity(intent);

      }
  });

    }



    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public ImageView imageProfile;
        public ImageView post_image;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;

        public TextView username;
        public TextView noOfLikes;
        public TextView author;
        public TextView noOfComments;
        SocialTextView description;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageProfile= itemView.findViewById(R.id.image_profile);
                    post_image= itemView.findViewById(R.id.post_image);
            like= itemView.findViewById(R.id.like);
                    comment= itemView.findViewById(R.id.comment);
            save= itemView.findViewById(R.id.save);
                    more= itemView.findViewById(R.id.more);
            username= itemView.findViewById(R.id.username);
                    noOfLikes= itemView.findViewById(R.id.no_of_likes);
            author= itemView.findViewById(R.id.author);
                    noOfComments= itemView.findViewById(R.id.no_of_comments);
            description= itemView.findViewById(R.id.description);
        }
    }
    private void isSaved(final String postid, final ImageView image) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postid).exists()){
                  image.setImageResource(R.drawable.saved);
                  image.setTag("saved");


                }else{
                    image.setImageResource(R.drawable.save);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

private void isLiked(String postId, final ImageView imageView){
       FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.child(firebaseUser.getUid()).exists()){
                        imageView.setImageResource(R.drawable.fav);
                        imageView.setTag("liked");


               }else{
                   imageView.setImageResource(R.drawable.like);
                   imageView.setTag("like");


               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
}

private  void noOfLikes(String postId, final TextView text){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount()+" Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}

private void getComments(String postId, final TextView text){
        FirebaseDatabase.getInstance().getReference().child("comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getChildrenCount() == 0) {
                        text.setText(" No Comments here");
                    } else if (snapshot1.getChildrenCount() == 1) {
                        text.setText("View " + snapshot.getChildrenCount() + " comment");

                    } else {
                        text.setText("View All " + snapshot.getChildrenCount() + " comments");
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}
    private void addNotification(String postid, String publisherId) {

        HashMap<String,Object>map=new HashMap<>();
        map.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("text","Liked Your Post");
        map.put("postid",postid);
        map.put("isPost",true);
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisherId).push().setValue(map);
    }

}
