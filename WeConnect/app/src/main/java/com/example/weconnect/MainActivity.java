package com.example.weconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import Fragments.ChatFragment;
import Fragments.HomeFragment;
import Fragments.NotificationFragment;
import Fragments.ProfileFragment;
import Fragments.SearchFragment;

import static com.example.weconnect.LoginActivity.count;

public class MainActivity extends AppCompatActivity {
  private BottomNavigationView bottomNavigationView;
  private Fragment selectorFragment;
 public static int check=0;

  static   private ImageView chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressDialog progress = new ProgressDialog(this);
          chat=findViewById(R.id.chat);
          chat.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  selectorFragment=new ChatFragment();
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                  chat.setVisibility(View.GONE);
              }
          });
        progress.setMessage("Loading Up Your Feed....");
        if(count==0) {
             progress.show();
             count=1;
         }
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progress.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 4000);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch( item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment=new HomeFragment();

                        chat.setVisibility(View.VISIBLE);
                        break;
                    case R.id.nav_search:
                        selectorFragment=new SearchFragment();


                        break;

                    case R.id.nav_add:
                        selectorFragment=null;
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        finish();
                        break;
                    case R.id.nav_heart:
                        selectorFragment=new NotificationFragment();
                        chat.setVisibility(View.VISIBLE);

                        break;
                    case R.id.nav_person:
                        selectorFragment=new ProfileFragment();
                        check=1;
                        chat.setVisibility(View.GONE);

                        break;


                }
                 if(selectorFragment!=null){
                     getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                 }

                return true;
            }
        });


        Bundle intent=getIntent().getExtras();
        if(intent!=null ){
         chat.setVisibility(View.GONE);
            String profileId=intent.getString("publisherId");


            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();


            bottomNavigationView.setSelectedItemId(R.id.nav_person);
            chat.setVisibility(View.GONE);
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
    chat.setVisibility(View.VISIBLE);

    }
}