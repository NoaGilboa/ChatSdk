package com.example.chatsdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlibrary.ChatSdk;
import com.example.chatlibrary.models.Chat;
import com.example.chatsdk.R;
import com.example.chatsdk.adapters.ChatAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private RecyclerView rvChats;
    private FloatingActionButton fabAddUsers;
    private String currentUserId;
    private String currentUsername;

    private Handler handler = new Handler();
    private Runnable chatsUpdater;
    private static final int CHAT_UPDATE_INTERVAL = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvWelcome = findViewById(R.id.tvWelcome);
        rvChats = findViewById(R.id.rvChats);
        fabAddUsers = findViewById(R.id.fabAddUsers);

        // Get user details from the Intent
        currentUserId = getIntent().getStringExtra("USER_ID");
        currentUsername = getIntent().getStringExtra("USERNAME");

        // Set the welcome text
        if (currentUsername != null) {
            tvWelcome.setText("Hi " + currentUsername);
        } else {
            Toast.makeText(this, "Error: User information missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load chats for the user
        loadChats();

        // Add users button action
        fabAddUsers.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, UserListActivity.class);
            Log.d("MenuActivity", "currentUserId: "+ currentUserId);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        startChatsUpdater();
    }

    private void loadChats() {
        ChatSdk.getInstance().getChatsForUser(currentUserId, new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Chat> chats = response.body();
                    rvChats.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
                    rvChats.setAdapter(new ChatAdapter(chats, currentUsername, chat -> {
                        // Open ChatActivity when a chat is clicked
                        Intent intent = new Intent(MenuActivity.this, ChatActivity.class);
                        intent.putExtra("CHAT_ID", chat.getId());
                        if(chat.getUser1Id().equals(currentUserId)){
                            intent.putExtra("user1Id", currentUserId);
                            intent.putExtra("user2Id", chat.getUser2Id());
                        }
                        else{
                            intent.putExtra("user1Id", chat.getUser2Id());
                            intent.putExtra("user2Id", currentUserId);
                        }
                        startActivity(intent);
                    }));
                } else {
                    Toast.makeText(MenuActivity.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startChatsUpdater() {
        chatsUpdater = new Runnable() {
            @Override
            public void run() {
                loadChats();
                handler.postDelayed(this, CHAT_UPDATE_INTERVAL);
            }
        };
        handler.post(chatsUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(chatsUpdater);
    }
}
