package com.newtry.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.newtry.myapplication.Models.Users;
import com.newtry.myapplication.databinding.ActivitySingnInBinding;

public class SignInActivity extends AppCompatActivity {
    ActivitySingnInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySingnInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your Account");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);



        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etEmail.getText().toString().isEmpty())
                {
                    binding.etEmail.setError("Enter your emal");
                    return;
                }
                if(binding.etPassword.getText().toString().isEmpty())
                {
                    binding.etPassword.setError("Enter your password");
                    return;
                }
                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignInActivity.this, "Logged In successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    int RC_SIGN_IN=65;
 private void signIn()
 {
     Intent signInIntent = mGoogleSignInClient.getSignInIntent();
     startActivityForResult(signInIntent,RC_SIGN_IN);

 }
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        //Result returned from launching the intent from googleSignInApi.getSignInIntent(...);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //Google Sign in was successfull,authenticate with firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG","firebaseAuthWithGoogle :"+account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }catch(ApiException e)
            {
                //Google Sign in failed ,update Ui Approprately
                Log.w("TAG","Google sign in failed",e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            //saving the user data in the firebase
                            //start
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid()); //to set the userid
                            users.setUserName(user.getDisplayName()); //to set the username
                            users.setProfilepic(user.getPhotoUrl().toString()); //to set the profile of the user
                           database.getReference().child("Users")
                                   .child(user.getUid()).setValue(users);

                            //end
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "SignIn With Google Sucessfull", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignInActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignInActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
