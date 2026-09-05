package com.chat.gvg;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://isjmxeqskdrysmjfhxrf.supabase.co";
    private static final String SUPABASE_KEY = "Sb_publishable_TYNY1_BGUNwEKBfvlPVHqw_zV5E8kJl";

    private LinearLayout containerMessages;
    private ScrollView scrollMessages;
    private EditText etMessage;
    private Button btnSend;
    private String userNick = "Anônimo";
    private OkHttpClient client = new OkHttpClient();
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getIntent().hasExtra("USER_NICK")) {
            userNick = getIntent().getStringExtra("USER_NICK");
        }

        containerMessages = findViewById(R.id.containerMessages);
        scrollMessages = findViewById(R.id.scrollMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> sendMessage());

        // Atualiza mensagens a cada 3 segundos
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                loadMessages();
            }
        }, 0, 3000);
    }

    private void loadMessages() {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/messages?select=*&order=created_at.asc")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String data = response.body().string();
                    runOnUiThread(() -> parseAndDisplayMessages(data));
                }
            }
        });
    }

    private void parseAndDisplayMessages(String json) {
        try {
            JSONArray array = new JSONArray(json);
            containerMessages.removeAllViews();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String sender = obj.optString("sender_nick", "Membro");
                String content = obj.optString("content", "");

                TextView tv = new TextView(this);
                tv.setText(sender + ": " + content);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(16);
                tv.setPadding(16, 12, 16, 12);
                tv.setBackgroundColor(0xFF1F2C34);
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 12);
                tv.setLayoutParams(params);

                containerMessages.addView(tv);
            }

            scrollMessages.post(() -> scrollMessages.fullScroll(ScrollView.FOCUS_DOWN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        JSONObject bodyJson = new JSONObject();
        try {
            bodyJson.put("content", msg);
            bodyJson.put("sender_nick", userNick);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(bodyJson.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/messages")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Prefer", "return=minimal")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        etMessage.setText("");
                        loadMessages();
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
