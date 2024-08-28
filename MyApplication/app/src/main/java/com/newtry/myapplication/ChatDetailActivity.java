package com.newtry.myapplication;

import android.annotation.SuppressLint;
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
import com.newtry.myapplication.databinding.ActivityChatDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {
        ActivityChatDetailBinding binding;
        FirebaseDatabase database;
        FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();  //to remove the tool bar
        database= FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();

        final String senderId=auth.getUid();  //user that is login is the sender
        String receiverId= getIntent().getStringExtra("userId"); //the user that we click to chat is the receiver

        String userName=getIntent().getStringExtra("userName");
        String profilePic=getIntent().getStringExtra("profilePic");

        binding.tvUserName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.boyimagewithcircle).into(binding.profileImage);
        binding.ivArrow.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                                                   startActivity(intent);
}                                          });



        ArrayList<MessageModel> messageModels = new ArrayList<>();
        ChatAdapter chatAdapter = new ChatAdapter(messageModels,this,receiverId);
        binding.rvChats.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvChats.setLayoutManager(layoutManager);

        final String senderRoom = senderId +receiverId;
        final String receiverRoom = receiverId+senderId;
        binding.ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etMessage.getText().toString().isEmpty())
                {
                    binding.etMessage.setError("Please Enter your message");
                    return;
                }
                String message= binding.etMessage.getText().toString();
                MessageModel model= new MessageModel(senderId,message);
                model.setTimestamp(new Date().getTime());
                binding.etMessage.setText("");
                database.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model);
                Toast.makeText(ChatDetailActivity.this, "set The sender", Toast.LENGTH_SHORT).show();
                //set data on receiver also
                database.getReference().child("chats")
                        .child(receiverRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ChatDetailActivity.this, "Message Send", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        database.getReference().child("chats")
                        .child(senderRoom)//we have used the senderRomm to get the recent data so he/she is the sender
                .addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                         for(DataSnapshot snapshot1:snapshot.getChildren())
                         {
                             MessageModel model= snapshot1.getValue(MessageModel.class);

                            model.setMessageId(snapshot1.getKey());
                             messageModels.add(model);
                         }
                         chatAdapter.notifyDataSetChanged(); //to update at runtime
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatDetailActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });





        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}