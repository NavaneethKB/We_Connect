package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.ChatUsersActivity;
import com.example.weconnect.MainActivity;
import com.example.weconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import Fragments.ProfileFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import model.user;
import static com.example.weconnect.MainActivity.check;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
 private Context mContext;
 private List<user> mUsers;
 private boolean isFragment;
 private boolean isFragmentChat;
 private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<user> mUsers, boolean isFragment, boolean isFragmentChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
        this.isFragmentChat = isFragmentChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
       final user User=mUsers.get(position);
           if(!isFragmentChat) {
               holder.follow.setVisibility(View.VISIBLE);
           }

           holder.username.setText(User.getUsername());
       holder.fullname.setText(User.getName());

        Picasso.get().load(User.getImageurl()).placeholder(R.drawable.person).into(holder.imageProfile);
        isfollowed(User.getId(),holder.follow);

        if(User.getId().equals(firebaseUser.getUid())){


            holder.follow.setVisibility(View.GONE);
        }

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.follow.getText().toString().equals("follow")){

                   FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(User.getId()).setValue(true);


                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("followers").child(User.getId()).setValue(true);


                    addNotification(User.getId());
                }else{
                   // FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(User.getId()).removeValue();

                  //  FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("followers").child( User.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").child(User.getId()).child("followers").child( firebaseUser.getUid()).removeValue();


                }
            }
        });

  holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          if(isFragment){
              if(isFragmentChat) {
                  mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", User.getId()).apply();
                  mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("username", User.getUsername()).apply();

                  Intent intent=new Intent(mContext, ChatUsersActivity.class);

                  mContext.startActivity(intent);

              }else{
                  mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", User.getId()).apply();
                  ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                   check=0;
                  Intent intent2=new Intent(mContext, MainActivity.class);
                  intent2.putExtra("publisherId2","JUST TO MAKE THINGS WORK!");
              }
          }else{
              Intent intent=new Intent(mContext, MainActivity.class);
               intent.putExtra("publisherId",User.getId());
               mContext.startActivity(intent);


          }

      }
  });

    }



    private void isfollowed(final String id, final Button follow) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following");
    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.child(id).exists()){
                follow.setText("following");
            }else{
                follow.setText("follow");

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageProfile;
        public Button follow;
        public TextView fullname;
        public  TextView username;



        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            imageProfile=itemView.findViewById(R.id.image_profile);
           follow=itemView.findViewById(R.id.btn_follow);
           fullname=itemView.findViewById(R.id.fullname);
            username=itemView.findViewById(R.id.username);

        }
    }
    private void addNotification(String userid) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("userid",userid);
        map.put("text","Started Following You");

        map.put("isPost",false);
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }
}
