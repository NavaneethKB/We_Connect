package Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import adapter.TagAdapter;
import adapter.UserAdapter;
import model.user;

public class SearchFragment extends Fragment {
 private RecyclerView recyclerView;
 private SocialAutoCompleteTextView search_bar;
 private List<user>mUsers;
 private  UserAdapter userAdapter;
 private RecyclerView recyclerViewTags;
 private List<String>mHashTags;
 private List<String>mHashTagsCount;
 private List<String>namelist;
 private TagAdapter tagAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView=view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTags=view.findViewById(R.id.recycler_view_tags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));
        mHashTags=new ArrayList<>();
        mHashTagsCount=new ArrayList<>();
        namelist=new ArrayList<>();
        tagAdapter=new TagAdapter(getContext(),mHashTags,mHashTagsCount);
        search_bar=view.findViewById(R.id.search_bar);

        mUsers=new ArrayList<>();
        userAdapter=new UserAdapter(getContext(),mUsers,true,false);
        recyclerView.setAdapter(userAdapter);
        recyclerViewTags.setAdapter(tagAdapter);


        readUsers();
        readTags();
        search_bar.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
               SearchUser(s.toString());
         }

         @Override
         public void afterTextChanged(Editable s) {
                      filter(s.toString());
         }
     });
    return view;
    }

    private void readTags() {

        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashTags.clear();
                mHashTagsCount.clear();

                for(DataSnapshot snapshots: snapshot.getChildren() ){
                    mHashTags.add(snapshots.getKey());

                    mHashTagsCount.add(snapshots.getChildrenCount()+" ");
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(search_bar.getText().toString())){
                    mUsers.clear();
                    for(DataSnapshot Snapshots:snapshot.getChildren()){
                        user user2=Snapshots.getValue(user.class);
                        if(user2.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            namelist.add(user2.getUsername().toString());

                        }
                        mUsers.add(user2);
                    }
                    userAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }
    private void SearchUser(String s){
        Query query=FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username" ) .startAt(s).endAt(s +"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
          mUsers.clear();
          for(DataSnapshot Snapshots:snapshot.getChildren()) {

          user user2=Snapshots.getValue(user.class);
          mUsers.add(user2);
          }
          userAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
  });
    }
 private void filter(String text){
        List<String>mSearchTags=new ArrayList<>();
        List<String>mSearchTagsCount=new ArrayList<>();

        for(String S:mHashTags){
            if(S.toLowerCase().contains(text.toLowerCase())){
                mSearchTags.add(S);
                mSearchTagsCount.add(mHashTagsCount.get(mHashTags.indexOf(S)));
            }
        }
tagAdapter.filter(mSearchTags,mSearchTagsCount);

 }
}