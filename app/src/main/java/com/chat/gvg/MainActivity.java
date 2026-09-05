package com.chat.gvg;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // SUAS CREDENCIAIS DO SUPABASE
    private static final String SUPABASE_URL = "https://isjmxeqskdrysmjfhxrf.supabase.co";
    private static final String SUPABASE_KEY = "Sb_publishable_TYNY1_BGUNwEKBfvlPVHqw_zV5E8kJl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("Chat GvG - Conectado ao Supabase!");
        textView.setTextSize(20);
        textView.setPadding(40, 40, 40, 40);
        
        setContentView(textView);
    }
}
