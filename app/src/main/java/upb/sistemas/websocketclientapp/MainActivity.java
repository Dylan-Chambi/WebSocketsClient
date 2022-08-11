package upb.sistemas.websocketclientapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import upb.sistemas.websocketclientapp.databinding.ActivityMainBinding;
import upb.sistemas.websocketclientapp.databinding.ContentMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ContentMainBinding contentBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        contentBinding = binding.contentMain;
        setContentView(binding.getRoot());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);


        EditText userName = findViewById(R.id.usernameET);
        EditText serverIP = findViewById(R.id.serverIP_ET);
        EditText serverPort = findViewById(R.id.portET);

        findViewById(R.id.connectBtn)
                .setOnClickListener(v -> {
                    Intent intent = new Intent(this, WebSocketActivity.class);
                    intent.putExtra("username", userName.getText().toString());
                    intent.putExtra("serverIP", serverIP.getText().toString());
                    intent.putExtra("serverPort", serverPort.getText().toString());
                    startActivity(intent);
                });
    }

}

