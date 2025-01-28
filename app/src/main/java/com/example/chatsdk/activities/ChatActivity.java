package com.example.chatsdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlibrary.ChatSdk;
import com.example.chatlibrary.models.Message;
import com.example.chatlibrary.models.User;
import com.example.chatsdk.R;
import com.example.chatsdk.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private String userId;
    private User currentUser;
    private String chatId = "example_chat_id"; // Replace with actual chat ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getStringExtra("userId");
        loadUser();

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.recyclerViewMessages);

        // Initialize the RecyclerView and adapter
        adapter = new MessageAdapter(messages, userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load existing messages
        loadMessages();

        // Set click listener for the Send button
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadUser(){
        ChatSdk.getInstance().loadUser(userId, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                } else {
                    Toast.makeText(ChatActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "load user failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("ChatActivity", t.getMessage());
            }
        });
    }
    private void loadMessages() {
        // Fetch messages from the backend using ChatSdk
        ChatSdk.getInstance().getMessagesByChatId(chatId, new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messages.clear(); // Clear the current list to avoid duplicates
                    messages.addAll(response.body()); // Add new messages
                    adapter.notifyDataSetChanged(); // Notify adapter to update the UI
                    recyclerView.scrollToPosition(messages.size() - 1); // Scroll to the latest message
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                t.printStackTrace(); // Log error for debugging
            }
        });
    }


    private void sendMessage() {
        // Get the message content
        String content = etMessage.getText().toString().trim();

        // Validate message content
        if (TextUtils.isEmpty(content)) {
            etMessage.setError("Message cannot be empty");
            return;
        }

        // Replace these with actual sender and receiver IDs
        String senderId = userId; // Assuming the current user is the sender
        String receiverId = "receiver_id"; // Replace with actual receiver ID

        // Send the message using ChatSdk
        ChatSdk.getInstance().sendMessage(chatId, senderId, receiverId, content, new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messages.add(response.body()); // Add the new message to the list
                    adapter.notifyItemInserted(messages.size() - 1); // Notify adapter of new message
                    recyclerView.scrollToPosition(messages.size() - 1); // Scroll to the latest message
                    etMessage.setText(""); // Clear the input field
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                t.printStackTrace(); // Log error for debugging
            }
        });
    }
}
