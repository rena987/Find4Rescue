package com.example.find4rescue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.find4rescue.R;
import com.example.find4rescue.models.Comments;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Comment;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MessageViewHolder>{

    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;

    private Context context;
    private JSONArray comments;
    private JSONArray usernames;

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
        try {
            String comment = comments.get(position).toString();
            String username = usernames.get(position).toString();
            holder.bindMessage(username, comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return comments.length();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (isMe(position)) {
                return MESSAGE_OUTGOING;
            } else {
                return MESSAGE_INCOMING;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MESSAGE_OUTGOING;
    }

    private boolean isMe(int position) throws JSONException {
        String username = usernames.get(position).toString();
        if (username == ParseUser.getCurrentUser().getUsername()) {
            return true;
        } else {
            return false;
        }
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(String username, String comment);
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
        void bindMessage(String username, String comment) {
            body.setText(comment);
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
        void bindMessage(String username, String comment) {
            body.setText(comment);
        }


    }

}
