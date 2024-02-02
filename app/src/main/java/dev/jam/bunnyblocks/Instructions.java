package dev.jam.bunnyblocks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Instructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(Instructions.this, StartActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
