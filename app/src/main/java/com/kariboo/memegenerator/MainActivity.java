package com.kariboo.memegenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int PHOTO_INTENT_REQUEST_CODE = 10;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("in onCreate");

        if (savedInstanceState != null) {
            TextView editText = (TextView) findViewById(R.id.editText);
            editText.setText(savedInstanceState.getCharSequence("EDIT_TEXT"));
        }


        setContentView(R.layout.activity_main);

        Button shareButton = (Button) findViewById(R.id.button_share);
        shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sharePhoto();
            }
        });

        //File sharedFile = new File(getCacheDir(), "images/image.png");



        //delete cached file
        //sharedFile.delete();

    }

    private void sharePhoto() {
        createCompositeImage();
        createShareIntent();

    }

    private void createShareIntent() {


    }

    private void createCompositeImage() {
        // Create the composite image
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout_meme);
        frameLayout.setDrawingCacheEnabled(true);

        Bitmap bitmap = frameLayout.getDrawingCache();

        File sharedFile = new File(getCacheDir(), "images");
        sharedFile.mkdirs();


        try {

            //File sharedFile = File.createTempFile("meme", ".png", getCacheDir());

            //sharedFile.mkdirs();

            //FileOutputStream stream = new FileOutputStream(sharedFile);

            Long unique = new Date().getTime();
            System.out.println("unique=" + unique);

            FileOutputStream stream = new FileOutputStream(sharedFile + "/" + unique+ "image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);



            sharedFile = new File(getCacheDir(), "images"+ "/" + unique+ "image.png");

            Uri imageUri = FileProvider.getUriForFile(this, "com.kariboo.fileprovider", sharedFile);
                    //Uri.fromFile(sharedFile);

            System.out.println("URI=" + imageUri);
            //

            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri );
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frameLayout.setDrawingCacheEnabled(false);
        frameLayout.destroyDrawingCache();
    }

    public void pickPhotoFromGallery(View v) {
        requestPermission();
    }

    private void createPhotoIntent() {
        Intent photoIntent = new Intent(Intent.ACTION_PICK);
        File photoDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Uri photoUri = Uri.parse(photoDirectory.getPath());
        photoIntent.setDataAndType(photoUri, "image/*");
        startActivityForResult(photoIntent, PHOTO_INTENT_REQUEST_CODE);
    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            createPhotoIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createPhotoIntent();
                } else {
                    Toast.makeText(this, "Gallery Permission Denied :(", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == PHOTO_INTENT_REQUEST_CODE) {
                Uri photoUri = data.getData();

                ImageView imageView = (ImageView) findViewById(R.id.image_view_meme);

                Picasso.get().load(photoUri).into(imageView);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        TextView editText = (TextView) findViewById(R.id.editText);
        CharSequence editTextValue = editText.getText();
        System.out.println("in onSaveInstanceState editText value=" + editTextValue);

        outState.putCharSequence("EDIT_TEXT", editTextValue);


        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }
}
