package com.example.toshitgurajalasimmi_task;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {
 private Button chooseimg;
 private Button upload;
 private TextView uploadtext;
 private EditText filename;
 private ImageView img;
 private ProgressBar bar;
 String ViewID;
 private Uri imguri;
 int val=0;
 private StorageReference storageref;
 private StorageTask uploaddat;
 Context context = MainActivity2.this;
 public static final String viewname1 = "com.example.toshitgurajalasimmi_task.extra.viewid1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView textview;
        Intent intent1 = getIntent();
        String ViewID1 = intent1.getStringExtra(MainActivity.viewname);
        ViewID=ViewID1;
        chooseimg = findViewById(R.id.choosefile);
        upload = findViewById(R.id.Upload);
        uploadtext = findViewById(R.id.uploadtext);
        img = findViewById(R.id.imageview);
        bar = findViewById(R.id.progressBar);
        storageref= FirebaseStorage.getInstance().getReference(ViewID1);
        chooseimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploaddat != null && uploaddat.isInProgress()) {
                    Toast.makeText(MainActivity2.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {

                   getval();

                }
                if(val>=5)
                {
                    Toast.makeText(getApplicationContext(),"Database Full (5/5)",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    uploadFile();
                }

            }

            private void uploadFile() {
                if (imguri != null) {
                    StorageReference fileReference = storageref.child(System.currentTimeMillis()
                            + "." + getFileExtension(imguri));

                    uploaddat = fileReference.putFile(imguri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bar.setProgress(0);
                                        }
                                    }, 500);
                                    addvalue();
                                    Toast.makeText(MainActivity2.this, "Upload successful", Toast.LENGTH_LONG).show();
//
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    bar.setProgress((int) progress);
                                }
                            });
            }
                else
                    Toast.makeText(getApplicationContext(), "Select File", Toast.LENGTH_SHORT).show();
            }

            private void addvalue() {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("DataSets");
                myRef.runTransaction(new Transaction.Handler() {
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer value = mutableData.child(ViewID1).getValue(Integer.class);
                        val = value;
                        mutableData.child(ViewID1).setValue(value + 1);
                        val=value+1;
                        return Transaction.success(mutableData);
                    }


                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                    }
                });
            }

        });
        uploadtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          Intent intent = new Intent(context, Imagedisplay.class);
          intent.putExtra(viewname1,ViewID1);
          startActivity(intent);
            }
        });
    }

    private void getval() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("DataSets");
        myRef.runTransaction(new Transaction.Handler() {
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer value = mutableData.child(ViewID).getValue(Integer.class);
                val=value;

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }

        });
    }

    private void openFileChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }
    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null
                            && data.getData() != null) {
                        imguri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    imguri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        img.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });
    private String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}
