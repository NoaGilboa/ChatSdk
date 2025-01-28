package com.example.chatsdk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlibrary.models.User;
import com.example.chatsdk.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final UserSelectionListener userSelectionListener;

    public UserAdapter(List<User> userList, UserSelectionListener userSelectionListener) {
        this.userList = userList;
        this.userSelectionListener = userSelectionListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUsername.setText(user.getUsername());
        holder.itemView.setOnClickListener(v -> userSelectionListener.onUserSelected(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }

    public interface UserSelectionListener {
        void onUserSelected(User user);
    }
}
