package com.newtry.myapplication;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newtry.myapplication.Adapters.ChatAdapter;
import com.newtry.myapplication.Models.MessageModel;
import com.newtry.myapplication.databinding.ActivityGroupChatActivityBinding;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatActivityBinding binding;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityGroupChatActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        String senderId = FirebaseAuth.getInstance().getUid();
        binding.tvUserName.setText("Friends Chats");
        database = FirebaseDatabase.getInstance();
        ArrayList<MessageModel> messageModels = new ArrayList<>();
        ChatAdapter adapter = new ChatAdapter(messageModels, this,null);
        binding.rvChats.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvChats.setLayoutManager(layoutManager);

        binding.ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etMessage.getText().toString().isEmpty())
                {
                    binding.etMessage.setError("Please Enter your Message");
                    return;
                }
                String message = binding.etMessage.getText().toString();
                MessageModel model = new MessageModel(senderId,message);
                model.setTimestamp(new Date().getTime());

                binding.etMessage.setText("");

                database.getReference().child("Group Chat")
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(GroupChatActivity.this, "send", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        // to get the data from the database
        database.getReference().child("Group Chat")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageModels.clear();
                                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    MessageModel model = dataSnapshot.getValue(MessageModel.class);
                                    model.setMessageId(dataSnapshot.getKey());
                                    messageModels.add(model);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}