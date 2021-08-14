package Fragments;

import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapter.NotificationAdapter;
import model.Notification;


public class NotificationFragment extends Fragment {
 private RecyclerView recyclerView;
 private NotificationAdapter notificationAdapter;
 private List<Notification>notificationList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
             View view=inflater.inflate(R.layout.fragment_notification, container, false);

             recyclerView=view.findViewById(R.id.recycler_view);
             recyclerView.setHasFixedSize(true);
             recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
             notificationList=new ArrayList<>();
             notificationAdapter=new NotificationAdapter(getContext(),notificationList);
             recyclerView.setAdapter(notificationAdapter);
             readNotifications();





             return view;
    }

    private void readNotifications() {
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot snapshots:snapshot.getChildren()){
                    notificationList.add(snapshots.getValue(Notification.class));

                }
                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}