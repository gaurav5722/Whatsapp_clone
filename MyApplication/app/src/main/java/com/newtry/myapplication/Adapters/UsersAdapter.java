package com.newtry.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newtry.myapplication.ChatDetailActivity;
import com.newtry.myapplication.Models.Users;
import com.newtry.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.viewHolder> {

    ArrayList<Users> list;
    Context context;

    public UsersAdapter(Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Users users = list.get(position);
        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.boyimagewithcircle).into(holder.image);
        //placeholder is for default image
        holder.userName.setText(users.getUserName());
        //To updata the last message
        FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(FirebaseAuth.getInstance().getUid()+users.getUserId())
                                .orderByChild("timestamp")
                                        .limitToLast(1)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.hasChildren())
                                                        {
                                                            for(DataSnapshot snapshot1: snapshot.getChildren())
                                                            {
                                                                holder.lastMessage.setText(snapshot1.child("message").getValue(String.class));
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(context, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });






        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId",users.getUserId());
                intent.putExtra("profilePic",users.getProfilepic());
                intent.putExtra("userName",users.getUserName());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
         ImageView image;
         TextView userName,lastMessage;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.profile_image);
            userName =itemView.findViewById(R.id.tvUsername);
            lastMessage =itemView.findViewById(R.id.tvLastMessage);
        }
    }
}
