package com.example.chatsdk.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlibrary.ChatSdk;
import com.example.chatlibrary.models.Message;
import com.example.chatlibrary.models.User;
import com.example.chatlibrary.models.Chat;
import com.example.chatsdk.R;
import com.example.chatsdk.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatName;
    private EditText etMessage;
    private Button btnSend;
    RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private String user1Id;
    private String user2Id;
    private User senderUser;
    private User receiverUser;
    private String chatId;
    private Handler handler = new Handler();
    private Runnable messageUpdater;
    private static final int MESSAGE_UPDATE_INTERVAL = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user1Id = getIntent().getStringExtra("user1Id");
        user2Id = getIntent().getStringExtra("user2Id");
        chatId = getIntent().getStringExtra("CHAT_ID");

        loadUser(user1Id, true);
        loadUser(user2Id, false);

        if(chatId == null || chatId.isEmpty())
            createOrLoadChat();
        else
            loadMessages();

        tvChatName= findViewById(R.id.tvChatName);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.recyclerViewMessages);

        // Initialize the RecyclerView and adapter
        adapter = new MessageAdapter(messages, user1Id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set click listener for the Send button
        btnSend.setOnClickListener(v -> sendMessage());

        startMessageUpdater();
    }
    private void loadUser(String userId, boolean isSender) {
        ChatSdk.getInstance().loadUser(userId, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (isSender) {
                        senderUser = response.body();
                    } else {
                        receiverUser = response.body();
                    }
                    if (senderUser != null && receiverUser != null) {
                        updateChatName();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to load user: "+ userId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("ChatActivity", t.getMessage());
            }
        });
    }

    private void updateChatName() {
        if (senderUser != null && receiverUser != null) {
            Log.d("ChatActivity", "senderUser: "+ senderUser.getUsername());
            Log.d("ChatActivity", "receiverUser: "+ receiverUser.getUsername());
//            if (user1Id.equals(senderUser.getId())) {
                tvChatName.setText(receiverUser.getUsername());
//            } else {
//                tvChatName.setText(senderUser.getUsername());
//            }
        }
    }

    private void loadMessages() {
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

    private void createOrLoadChat() {
        ChatSdk.getInstance().createChat(user1Id, user2Id, new Callback<Chat>() {
            @Override
            public void onResponse(Call<Chat> call, Response<com.example.chatlibrary.models.Chat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatId = response.body().getId();
                    loadMessages();
                }
            }

            @Override
            public void onFailure(Call<Chat> call, Throwable t) {
                Log.e("ChatActivity", "Failed to create chat: " + t.getMessage());
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

        String senderId = user1Id;
        String receiverId = user2Id;

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

    private void startMessageUpdater() {
        messageUpdater = new Runnable() {
            @Override
            public void run() {
                loadMessages(); // בדיקת הודעות חדשות
                handler.postDelayed(this, MESSAGE_UPDATE_INTERVAL);
            }
        };
        handler.post(messageUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(messageUpdater);
    }
}
