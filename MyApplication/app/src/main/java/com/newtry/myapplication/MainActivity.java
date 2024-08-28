package com.newtry.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.newtry.myapplication.Adapters.FragmentAdapter;
import com.newtry.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth= FirebaseAuth.getInstance();


        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch(item.getItemId())
//        {
//
//            case R.id.Settings:
//                break;
//
//            case R.id.logout:
//                auth.signOut();
//                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
//                break;
//
//        }
        if(item.getItemId() == R.id.Settings)
        {
//            Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();

            Intent intent= new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.Group_Chat)
        {
            Toast.makeText(this, "GroupChatActivity", Toast.LENGTH_SHORT).show();
            Intent intent  = new Intent(MainActivity.this,GroupChatActivity.class);
            startActivity(intent);

        }
      else if (item.getItemId() == R.id.logout)
        {
            auth.signOut();
            FirebaseAuth.getInstance().signOut();

            // Sign out from Google
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // User is now signed out of Google
                    // Redirect to login screen or update UI accordingly
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                    }   });

            Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
//            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}