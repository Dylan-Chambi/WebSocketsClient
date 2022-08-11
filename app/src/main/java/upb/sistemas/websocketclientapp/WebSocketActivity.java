package upb.sistemas.websocketclientapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketActivity extends AppCompatActivity implements TextWatcher {
    private String username;
    private WebSocket webSocket;
    private String SERVER;
    private int PORT;
    private EditText messageEdit;
    private View sendButton;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);

        username = getIntent().getStringExtra("username");
        SERVER = getIntent().getStringExtra("serverIP");
        PORT = Integer.parseInt(getIntent().getStringExtra("serverPort"));

        OkHttpClient client = new OkHttpClient.Builder()
                .pingInterval(0, TimeUnit.SECONDS).connectTimeout(1000, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url("ws://" + SERVER + ":" + PORT + "/").build();
        webSocket = client.newWebSocket(request, new SocketListener());


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocket.close(1000, "Closing");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        String string = s.toString().trim();

        if (string.isEmpty()) {
            resetMessageEdit();
        } else {
            LayoutParams params = sendButton.getLayoutParams();
            params.width = LayoutParams.WRAP_CONTENT;
            params.height = LayoutParams.WRAP_CONTENT;
            sendButton.setPadding(30, 35, 30, 35);
            sendButton.setLayoutParams(params);
        }

    }

    private void resetMessageEdit() {

        messageEdit.removeTextChangedListener(this);

        messageEdit.setText("");
        LayoutParams params = sendButton.getLayoutParams();
        params.width = 0;
        params.height = 0;
        sendButton.setLayoutParams(params);

        messageEdit.addTextChangedListener(this);

    }

    private class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            System.out.println("Connected");
            runOnUiThread(() -> {
                Toast.makeText(WebSocketActivity.this,
                        "Socket Connection Successful!",
                        Toast.LENGTH_SHORT).show();

                initializeView();
            });

        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            System.out.println("Message: " + text);
            runOnUiThread(() -> {

                try {
                    JSONObject jsonObject = new JSONObject(text);
                    jsonObject.put("isSent", false);

                    messageAdapter.addItem(jsonObject);

                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }
        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            System.out.println("Failed");
            System.out.println(t.getMessage());
            System.out.println(response);
            runOnUiThread(() -> {
                Toast.makeText(WebSocketActivity.this,
                        "Socket Connection Failed!",
                        Toast.LENGTH_SHORT).show();
            });
            finish();
        }
        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosing(webSocket, code, reason);
            System.out.println("Closing");
            runOnUiThread(() -> {
                Toast.makeText(WebSocketActivity.this,
                        "Socket Connection Closed!",
                        Toast.LENGTH_SHORT).show();
            });
            finish();
        }
    }

    private void initializeView() {

        messageEdit = findViewById(R.id.messageEdit);
        sendButton = findViewById(R.id.sendButton);

        recyclerView = findViewById(R.id.recyclerView);

        messageAdapter = new MessageAdapter(getLayoutInflater(), username);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        messageEdit.addTextChangedListener(this);

        sendButton.setOnClickListener(view -> {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", username);
                jsonObject.put("content", messageEdit.getText().toString());

                webSocket.send(jsonObject.toString());

                jsonObject.put("isSent", true);
                messageAdapter.addItem(jsonObject);

                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                resetMessageEdit();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

    }
}

