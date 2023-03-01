package com.example.toshitgurajalasimmi_task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String viewname = "com.example.toshitgurajalasimmi_task.extra.viewid";
    LinearLayout layout;
    Button addbutton;
    private DatabaseReference db;
    Context context = MainActivity.this;
    float textSize = 20.0f;
    private View view;
    int status = 0;
    int flagcheck=0;
    ProgressDialog prgd;

    public interface OnDataLoadedListener {
        void onDataLoaded(Map<String, Integer> dataMap);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //NetworkUtils.isNetworkAvailable(MainActivity.this);
        addbutton = findViewById(R.id.button_add);
        layout = findViewById(R.id.linear_layout);
        addbutton.setOnClickListener(this);
status=0;
        fetchvaluesfromdb(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded(Map<String, Integer> dataMap) {
                for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    Addvaluestoview(key, value);
                }

            }
        });
    }

    private void fetchvaluesfromdb(OnDataLoadedListener listener) {
        prgd = new ProgressDialog(MainActivity.this);
        prgd.setMessage("Fetching Data");
        prgd.setCancelable(false);
        prgd.show();
        db = FirebaseDatabase.getInstance().getReference("DataSets");
        Map<String, Integer> dataMap = new HashMap<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flagcheck=1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    Integer value = snapshot.getValue(Integer.class);
                    dataMap.put(key, value);
                }
                listener.onDataLoaded(dataMap);
                prgd.cancel();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to Load Database", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onClick(View v)
    {
        if(flagcheck==1) {
            addView();
            flagcheck=0;
        }
        else
            Toast.makeText(getApplicationContext(), "Enter Bucket Value", Toast.LENGTH_SHORT).show();

    }
    public void addView()
    {
        View addimagesview= getLayoutInflater().inflate(R.layout.add_images,null,false);
        EditText edittext = (EditText)addimagesview.findViewById(R.id.imagebucketname);
        Button removebutton = (Button)addimagesview.findViewById(R.id.removebtn);
        Button viewbutton = (Button)addimagesview.findViewById(R.id.buttonview);
        Button uploadbutton=(Button)addimagesview.findViewById(R.id.UploadButton);
        Button addbutton=(Button)addimagesview.findViewById(R.id.Add);
        layout.addView(addimagesview);
        removebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db= FirebaseDatabase.getInstance().getReference("DataSets");
                db.child(edittext.getText().toString()).removeValue();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child(edittext.getText().toString()); // replace with your folder name
                storageRef.listAll().addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.delete().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getApplicationContext(), "Folder deleted successfully", Toast.LENGTH_SHORT).show();
                            removeView((View) v.getParent());
                        }).addOnFailureListener(exception -> {
                            Toast.makeText(getApplicationContext(), "Failed to delete folder: ", Toast.LENGTH_SHORT).show();
                        });
                    }
                    removeView((View) v.getParent());
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to delete folder: ", Toast.LENGTH_SHORT).show();
                });
            }
        });
        viewbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==1) {
                    Intent intent = new Intent(MainActivity.this, Imagedisplay.class);
                    String view_id = edittext.getText().toString();
                    intent.putExtra(viewname, view_id);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Add Bucket Value",Toast.LENGTH_SHORT).show();
                }
            }
        });
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edittext.getText().toString().trim().length() != 0){
                    status=1;
                    flagcheck=1;
                edittext.setEnabled(false);
                db= FirebaseDatabase.getInstance().getReference("DataSets");
                db.child(edittext.getText().toString()).setValue(0);
                edittext.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent), PorterDuff.Mode.SRC_IN);
                edittext.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.highlighted_text_color));
                edittext.setPaintFlags(edittext.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                edittext.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            }
            else
                {
                    Toast.makeText(getApplicationContext(),"Add Bucket Name",Toast.LENGTH_SHORT).show();
                }
            }

        });
        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status==1) {
                    Intent intent = new Intent(context, MainActivity2.class);
                    String view_id = edittext.getText().toString();
                    intent.putExtra(viewname, view_id);
                    Toast.makeText(getApplicationContext(), ""+view_id, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Add Bucket Value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //-------------------------------------------Database-----------Addition--------------------------------------------//
    public void Addvaluestoview(String editvalue,int imagecount)
    {

        View addimagesview= getLayoutInflater().inflate(R.layout.add_images,null,false);
        EditText edittext3 = (EditText)addimagesview.findViewById(R.id.imagebucketname);
        layout.addView(addimagesview);
        edittext3.setText(editvalue);
        edittext3.setEnabled(false);
        edittext3.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        edittext3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.highlighted_text_color));
        edittext3.setPaintFlags(edittext3.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        edittext3.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        Button removebutton3 = (Button)addimagesview.findViewById(R.id.removebtn);
        Button viewbutton3 = (Button)addimagesview.findViewById(R.id.buttonview);
        Button uploadbutton3=(Button)addimagesview.findViewById(R.id.UploadButton);
        Button addbutton3=(Button)addimagesview.findViewById(R.id.Add);
        addbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Already In database", Toast.LENGTH_SHORT).show();
            }
        });
        removebutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db= FirebaseDatabase.getInstance().getReference("DataSets");
                db.child(edittext3.getText().toString()).removeValue();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child(edittext3.getText().toString()); // replace with your folder name
                storageRef.listAll().addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.delete().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getApplicationContext(), "Folder deleted successfully", Toast.LENGTH_SHORT).show();
                            removeView((View) v.getParent());
                        }).addOnFailureListener(exception -> {
                            Toast.makeText(getApplicationContext(), "Failed to delete folder: ", Toast.LENGTH_SHORT).show();
                        });
                    }
                    removeView((View) v.getParent());
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to delete folder: ", Toast.LENGTH_SHORT).show();
                });
            }
        });
        viewbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, Imagedisplay.class);
                    String view_id = edittext3.getText().toString();
                    intent.putExtra(viewname, view_id);
                    startActivity(intent);
            }
        });
        uploadbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent= new Intent(context,MainActivity2.class);
                    String view_id=edittext3.getText().toString();
                    intent.putExtra(viewname,view_id);
                Toast.makeText(getApplicationContext(), ""+view_id, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
            }
        });

    }
    public void removeView(View view) {

        layout.removeView(view);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}