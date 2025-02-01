package com.example.chatsdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlibrary.ChatSdk;
import com.example.chatlibrary.models.User;
import com.example.chatsdk.R;
import com.example.chatsdk.adapters.UserAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private String currentUserId;
    private Handler handler = new Handler();
    private Runnable usersUpdater;
    private static final int USER_UPDATE_INTERVAL = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        currentUserId = getIntent().getStringExtra("USER_ID");
        Log.d("UserListActivity", "currentUserId: "+currentUserId);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
        startUsersUpdater();
    }

    private void loadUsers() {
        ChatSdk.getInstance().getAllUsers(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    userAdapter = new UserAdapter(users, UserListActivity.this::onUserSelected);
                    recyclerView.setAdapter(userAdapter);
                } else {
                    Toast.makeText(UserListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(UserListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("UserListActivity", "Error loading users: " + t.getMessage());
            }
        });
    }

    private void onUserSelected(User user) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user1Id", currentUserId);
        intent.putExtra("user2Id", user.getId());
        startActivity(intent);
    }

    private void startUsersUpdater() {
        usersUpdater = new Runnable() {
            @Override
            public void run() {
                loadUsers();
                handler.postDelayed(this, USER_UPDATE_INTERVAL);
            }
        };
        handler.post(usersUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(usersUpdater);
    }
}
