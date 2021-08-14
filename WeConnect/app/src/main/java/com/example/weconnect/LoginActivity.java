package com.example.weconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
   private EditText email;
   private EditText password;
   private Button login;
   private TextView registerUser;
   private FirebaseAuth mAuth;
   static int count=0;
    ProgressDialog PD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.log);
        registerUser=findViewById(R.id.signup);
   mAuth=FirebaseAuth.getInstance();

   registerUser.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           startActivity(new Intent(LoginActivity.this,RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
       }
   });

   login.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           String emailtext=email.getText().toString();
           String passwordtext=password.getText().toString();
           if(passwordtext.isEmpty()||emailtext.isEmpty() ){
               Toast.makeText(LoginActivity.this,"No credentials can be left empty",Toast.LENGTH_SHORT).show();
           }else if(passwordtext.length()<6){
               Toast.makeText(LoginActivity.this,"Password must contain minimum 6 characters",Toast.LENGTH_SHORT).show();

           }else{
               loginUser(emailtext,passwordtext);
               InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
               mgr.hideSoftInputFromWindow(login.getWindowToken(), 0);

           }
       }
   });
    }

    private void loginUser(String email2, String password2) {

        mAuth.signInWithEmailAndPassword(email2,password2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();


               Intent intent;
                intent=new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        intent.putExtra("activity","hai");

                finish();

            }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e!=null) {
                    Toast.makeText(LoginActivity.this, "Some Error has occured!Check your ID and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}