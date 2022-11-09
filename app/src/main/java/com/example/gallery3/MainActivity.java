package com.example.gallery3;

import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements RecyclerAdapter.CountOfImagesWhenRemoved{
    RecyclerView recyclerView;
    ImageView gallery;
    TextView text;
    private View extract;

    ArrayList<Uri> list;
    int Read_Permission = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gallery = findViewById(R.id.gallery);

        recyclerView = findViewById(R.id.recylerview);

        extract = findViewById(R.id.extract);

        text = findViewById(R.id.count);




        handlePermission();

        list = new ArrayList<>();

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
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        list.add(imageUri);
                        setImage();

                    }

                    text.setText("Photos:(" + list.size() + ")");
                } else {
                    list.add(data.getData());
                    setImage();
                }

            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }



    }


    private void setImage() {
        RecyclerAdapter adapter = new RecyclerAdapter(list, MainActivity.this, this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

//          if(adapter.getItemCount()>0){
//              extract.setVisibility(View.VISIBLE);
//          }
//          else if(adapter.getItemCount()==0){
//              extract.setVisibility(View.INVISIBLE);
//          }


    }
    @Override
    public void clicked(int getSize)
    {
        text.setText("Photos:(" + list.size() + ")");
    }



}