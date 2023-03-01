package com.example.toshitgurajalasimmi_task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import com.example.toshitgurajalasimmi_task.NetworkUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class FullScreenView extends AppCompatActivity {
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // NetworkUtils.isNetworkAvailable(FullScreenView.this);
        setContentView(R.layout.activity_full_screen_view);

        imageView = findViewById(R.id.fullscreen_imageview);

        String imageUrl = getIntent().getStringExtra("image_url");

        Glide.with(getApplicationContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Imagedisplay.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, Imagedisplay.class);
        startActivity(intent);
        return true;
    }
}