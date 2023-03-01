package com.example.toshitgurajalasimmi_task;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Imagedisplay extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private ImageAdapter adapter;
    private ArrayList<String> imageUrls;
    private StorageReference storageref;
    private ImageView imageView;
    ProgressDialog prgd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        //NetworkUtils.isNetworkAvailable(Imagedisplay.this);
        prgd = new ProgressDialog(Imagedisplay.this);
        prgd.setMessage("Fetching Data");
        prgd.setCancelable(false);
        prgd.show();
        View view = LayoutInflater.from(Imagedisplay.this).inflate(R.layout.image_item,null, false);
        imageView = view.findViewById(R.id.image);
        TextView textview;
        Intent intent = getIntent();
        String viewID1 = intent.getStringExtra(MainActivity.viewname);
        String viewID2 = intent.getStringExtra(MainActivity2.viewname1);
        String viewID;
        if (viewID1 == null || viewID1.isEmpty()) {
            if (viewID2 == null || viewID2.isEmpty()) {
                Toast.makeText(getApplicationContext(), "View ID is null or empty", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                viewID = viewID2;
            }
        } else {
            viewID = viewID1;
        }
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels / 20;
        int width = displayMetrics.widthPixels / 4;
        // Set fixed height for each item in the RecyclerView
        recyclerView.addItemDecoration(new ItemDecoration(height, width));

        imageUrls = new ArrayList<>();
        adapter = new ImageAdapter(this, imageUrls);
        recyclerView.setAdapter(adapter);

        storageref = FirebaseStorage.getInstance().getReference().child(viewID);

        storageref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrls.add(uri.toString());
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                if (imageUrls.size() > 0) {
                    Glide.with(getApplicationContext())
                            .load(imageUrls.get(0))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);
                }
                prgd.cancel();

            }

        });


    }

    // Add an ItemDecoration class to set fixed height for each item in the RecyclerView
    private class ItemDecoration extends RecyclerView.ItemDecoration {
        private int itemHeight;
        private int itemWidth;

        public ItemDecoration(int itemHeight, int itemWidth) {
            this.itemHeight = itemHeight;
            this.itemWidth = itemWidth;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = itemHeight;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            final int itemCount = parent.getChildCount();
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < itemCount; i++) {
                final View child = parent.getChildAt(i);
                final int top = child.getBottom();
                final int bottom = top + itemHeight;
                final int leftMargin = ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).leftMargin;
                final int rightMargin = ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).rightMargin;
                final int leftPos = left;
            }
        }

    }

}