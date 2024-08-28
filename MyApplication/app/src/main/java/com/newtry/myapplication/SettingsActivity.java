package com.newtry.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.newtry.myapplication.Models.Users;
import com.newtry.myapplication.databinding.ActivitySettingsBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        binding= ActivitySettingsBinding.inflate(getLayoutInflater());
        binding.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        storage=FirebaseStorage.getInstance();
        auth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        binding.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                // */ <-- for all type
                startActivityForResult(intent,33);
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=binding.etUserName.getText().toString();
                String status= binding.etStatus.getText().toString();

                // to update the value in firebase we use hashmap

                HashMap<String,Object> obj = new HashMap<>();
                obj.put("userName",username);
                obj.put("status",status);
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(SettingsActivity.this, "file updated", Toast.LENGTH_SHORT).show();

            }
        });

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users = snapshot.getValue(Users.class);
                                Picasso.get().load(users.getProfilepic())
                                        .placeholder(R.drawable.boyimagewithcircle)
                                        .into(binding.ivProfile);
                                binding.etStatus.setText(users.getStatus());
                                binding.etUserName.setText(users.getUserName());

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

























        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null)
        {
            Uri uri=data.getData();
            binding.ivProfile.setImageURI(uri);  //To set the image on the
            // To store the image in the storage

            StorageReference reference =storage.getReference("profile pictures")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SettingsActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                .child("profilepic").setValue(uri.toString());
                    }
                });


                }
            });
        }
    }
}