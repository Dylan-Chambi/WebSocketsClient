package upb.sistemas.websocketclientapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_MESSAGE_RECEIVED = 1;
    private String username;

    private final LayoutInflater inflater;
    private final List<JSONObject> messages = new ArrayList<>();

    public MessageAdapter(LayoutInflater inflater, String username) {
        this.inflater = inflater;
        this.username = username;
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView usernameTV, sendMessageTV, sendTimeTV;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.usernameTV);
            sendMessageTV = itemView.findViewById(R.id.sendMessageTV);
            sendTimeTV = itemView.findViewById(R.id.sendTimeTV);
        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView senderNameTV, recieveTextTV, recieveTimeTV;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            senderNameTV = itemView.findViewById(R.id.senderNameTV);
            recieveTextTV = itemView.findViewById(R.id.recieveTextTV);
            recieveTimeTV = itemView.findViewById(R.id.recieveTimeTV);
        }
    }

    @Override
    public int getItemViewType(int position) {

        JSONObject message = messages.get(position);

        try {
            if (message.getBoolean("isSent")) {

                return TYPE_MESSAGE_SENT;

            } else {

                return TYPE_MESSAGE_RECEIVED;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case TYPE_MESSAGE_SENT:
                view = inflater.inflate(R.layout.item_sent_message, parent, false);
                return new SentMessageHolder(view);
            case TYPE_MESSAGE_RECEIVED:
                view = inflater.inflate(R.layout.item_received_message, parent, false);
                return new ReceivedMessageHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject message = messages.get(position);
        String currentTime = "";
        currentTime += Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);

        try {
            if (message.getBoolean("isSent")) {

                SentMessageHolder messageHolder = (SentMessageHolder) holder;
                messageHolder.usernameTV.setText(username);
                messageHolder.sendMessageTV.setText(message.getString("content"));
                messageHolder.sendTimeTV.setText(currentTime);

            } else {

                ReceivedMessageHolder messageHolder = (ReceivedMessageHolder) holder;
                messageHolder.senderNameTV.setText(message.getString("name"));
                messageHolder.recieveTextTV.setText(message.getString("content"));
                messageHolder.recieveTimeTV.setText(currentTime);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addItem(JSONObject jsonObject) {
        messages.add(jsonObject);
        notifyItemInserted(messages.size() - 1);
    }
}
