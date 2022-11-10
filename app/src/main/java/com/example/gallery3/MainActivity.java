package com.example.gallery3;

import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements RecyclerAdapter.CountOfImagesWhenRemoved{
    RecyclerView recyclerView;
    ImageView gallery;
    ImageView camera;
    TextView text;
    private View extract;
    RecyclerAdapter adapter;

    ArrayList<Uri> list;
    int Read_Permission = 0;
    int PERMISSION_CODE = 1;
    private Uri imageUri;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gallery = findViewById(R.id.gallery);

        recyclerView = findViewById(R.id.recylerview);

        extract = findViewById(R.id.extract);

        text = findViewById(R.id.count);

        camera = findViewById(R.id.camera);

        handlePermission();

        list = new ArrayList<>();

        adapter = new RecyclerAdapter(list, MainActivity.this, this);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i  = new Intent(MainActivity.this,CamActivity.class);
                startActivity(i);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), 10);
            }
        });


        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Extract text here", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void handlePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        imageUri = clipData.getItemAt(i).getUri();
                        list.add(imageUri);
                        //image will be uploaded to firebase after being picked from gallery
                        uploadToFirebase();
                        setImage();

                    }

                    adapter.notifyDataSetChanged();
                    text.setText("Photos:(" + list.size() + ")");

                } else {
                    list.add(data.getData());
                    uploadToFirebase();
                    setImage();
                    adapter.notifyDataSetChanged();
                    text.setText("Photos:(" + list.size() + ")");
                }




            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }



    }




    private void setImage() {
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

          if(adapter.getItemCount()>0){
              extract.setVisibility(View.VISIBLE);
          }
          else{
                  extract.setVisibility(View.INVISIBLE);
              }

    }


    @Override
    public void clicked(int getSize)
    {
        text.setText("Photos:(" + list.size() + ")");
    }

    private void uploadToFirebase() {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());          //in firebase images will be stored in images folder with name of any date and time
        String imageFileName = "PNG_" + timeStamp + "_";
        storageReference  = FirebaseStorage.getInstance().getReference().child("images/"+imageFileName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "Images Uploaded", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Toast.makeText(MainActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}