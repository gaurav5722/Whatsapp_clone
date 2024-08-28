package com.newtry.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.newtry.myapplication.Models.Users;
import com.newtry.myapplication.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");
        binding.tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(binding.etuserName.getText().toString().isEmpty())
               {
                   binding.etuserName.setError("Please Enter your username");
                   return;
               }
                if(binding.etEmail.getText().toString().isEmpty())
                {
                    binding.etEmail.setError("Please Enter your username");
                    return;
                }
                if(binding.etPassword.getText().toString().isEmpty())
                {
                    binding.etPassword.setError("Please Enter your username");
                    return;
                }

                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    Users user= new Users(binding.etuserName.getText().toString(),binding.etEmail.getText().toString(),binding.etPassword.getText().toString()); //createing the object of the user
                                    String id=task.getResult().getUser().getUid(); //getting the user id
                                    database.getReference().child("Users").child(id).setValue(user);//saving the  data in the database
                                    Toast.makeText(SignupActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });




        getSupportActionBar().hide(); //To hide the action bar on the top on which whatsapp was written

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}