package com.example.find4rescue.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.find4rescue.R;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MessageViewHolder> {

    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;
    private List<String> usernames;
    private List<String> messages;
    private Context context;

    public CommentsAdapter(Context context, List<String> usernames, List<String> messages) {
        Log.d("CommentsAdapter", "Usernames Array: " + usernames);
        Log.d("CommentsAdapter", "Messages Array: " + messages);
        this.context = context;
        this.usernames = usernames;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.MessageViewHolder holder, int position) {
        String message = messages.get(position);
        String username = usernames.get(position);
        holder.bindMessage(username, message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        String username = usernames.get(position);
        return username.equals(ParseUser.getCurrentUser().getUsername());
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(String username, String message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        TextView body;
        TextView name;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            body = (TextView)itemView.findViewById(R.id.tvBody);
            name = (TextView)itemView.findViewById(R.id.tvName);
        }

        @Override
        void bindMessage(String username, String message) {
            body.setText(message);
            name.setText(username);
        }

    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        TextView body;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            body = (TextView)itemView.findViewById(R.id.tvBody);
        }

        @Override
        void bindMessage(String username, String message) {
            body.setText(message);
        }
    }

}
