package com.example.ihor.googlemaps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout dlActivity_main;
    private EditText etSearch;
    private Bitmap bitmapPhoto = null;
    private Uri selectedImage;
    private ImageButton ibPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initNavigationView();
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        etSearch = (EditText) findViewById(R.id.etSearch);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu);

    }

    private void initNavigationView() {
        dlActivity_main = (DrawerLayout) findViewById(R.id.dlActivity_main);

    }


    private void startLoadPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constant.GALLERY_REQUEST);
    }

    private void loadPhoto(int requestCode, int resultCode, Intent imageReturnedIntent) {
        switch (requestCode) {
            case Constant.GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmapPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ibPhoto.setImageBitmap(bitmapPhoto);
                }
        }

    }


}
