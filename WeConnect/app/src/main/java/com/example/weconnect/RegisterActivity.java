package com.example.weconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.user;

public class RegisterActivity extends AppCompatActivity {
  private EditText name;
  private EditText username;
  private EditText email;
  private EditText password;
  private Button register;
  private TextView login;
  private DatabaseReference RootRef;
  private FirebaseAuth mAuth;
  ProgressDialog PD;
    private List<String> userNameList;

   int flag1 ;

 user user2;

    private List<String>nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userNameList=new ArrayList<>();
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.reg);
        mAuth = FirebaseAuth.getInstance();
        PD = new ProgressDialog(this);

        RootRef = FirebaseDatabase.getInstance().getReference();
        login.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });



    register.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        HashMap<Object,String>mapPasswords=new HashMap<>();
   final String textusername=username.getText().toString();
   final String textemail=email.getText().toString();
   final String textname=name.getText().toString();
   final String textpassword=password.getText().toString();
          if(textusername.isEmpty()||textemail.isEmpty()||textname.isEmpty()||textpassword.isEmpty() ){
              Toast.makeText(RegisterActivity.this,"No credentials can be left empty",Toast.LENGTH_SHORT).show();
          }else if(textpassword.length()<6){
              Toast.makeText(RegisterActivity.this,"Password must contain minimum 6 characters",Toast.LENGTH_SHORT).show();

          }else{
                      Query reference = FirebaseDatabase.getInstance().getReference().child("Users");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot Snapshots:snapshot.getChildren()) {
                                user2 = Snapshots.getValue(user.class);
                                userNameList.add(user2.getUsername().toString());


                            }

                            for(int i=0;i<3;i++){


                                if((userNameList.get(i).toString()).equals(textusername))
                                {
                                    flag1=1;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });




              if(flag1==0) {
                     Log.i("flag"," "+flag1);
                      registerUser(textusername, textemail, textpassword, textname);
             }else{
                        Log.i("flag"," "+flag1);
            Toast.makeText(RegisterActivity.this,"Username already exsits!Try some other",Toast.LENGTH_SHORT).show();
        }



          }
    }


});

    }

    private void registerUser(final String textusername, final String textemail, final String textpassword, final String textname) {
        PD.setMessage("Account Creation Process Initiated...");
                PD.show();
   mAuth.createUserWithEmailAndPassword(textemail,textpassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
       @Override
       public void onSuccess(AuthResult authResult) {
           HashMap<String,Object>map=new HashMap();
           HashMap<String,Object>map2=new HashMap<>();
           map.put("name",textname);
           map.put("email",textemail);
           map.put("username",textusername);
           map.put("id",mAuth.getCurrentUser().getUid());
           map.put("bio","");
           map.put("imageurl","default");
          map2.put(textname,textpassword);
           RootRef.child("Passwords").child(mAuth.getCurrentUser().getUid()).setValue(map2);
       RootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    PD.dismiss();
                    Toast.makeText(RegisterActivity.this,"Update the profile"+"for better experience",Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(RegisterActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK));
                   finish();
                }
               }

       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               PD.dismiss();
               Toast.makeText(RegisterActivity.this,"Some Error Has occured!Please Try Again",Toast.LENGTH_SHORT).show();
               Log.i("not working :-(((",e.getMessage());
           }
       });

       }
   });

    }
}