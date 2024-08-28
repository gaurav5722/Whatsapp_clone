package com.newtry.myapplication.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newtry.myapplication.Adapters.UsersAdapter;
import com.newtry.myapplication.Models.Users;
import com.newtry.myapplication.R;
import com.newtry.myapplication.databinding.FragmentChatsBinding;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }
    FragmentChatsBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        database = FirebaseDatabase.getInstance();
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        UsersAdapter adapter = new UsersAdapter(getContext(),list);
        binding.rvUsers.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        binding.rvUsers.setLayoutManager(layoutManager);
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid())) {//remove the current logged in user
                        list.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return binding.getRoot();
    }
}