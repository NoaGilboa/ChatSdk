package com.example.chatsdk.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatsdk.R;
import com.example.chatlibrary.models.Message;
import java.util.List;
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private String currentUserId;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            // Sent message layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
        } else {
            // Received message layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageContent.setText(message.getContent());
        holder.messageTimestamp.setText(message.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        // Check if the message was sent by the current user
        if (message.getSenderId().equals(currentUserId)) {
            return 1; // Sent message
        } else {
            return 2; // Received message
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, messageTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.messageContent);
            messageTimestamp = itemView.findViewById(R.id.messageTimestamp);
        }
    }
}