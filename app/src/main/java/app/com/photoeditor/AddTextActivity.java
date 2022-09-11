package app.com.photoeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileOutputStream;

import app.com.photoeditor.databinding.ActivityAddTextBinding;

public class AddTextActivity extends AppCompatActivity {
ActivityAddTextBinding binding;
String fileName;
Boolean state=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddTextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageView.setImageResource(R.drawable.ic_add_image_svgrepo_com);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                11);
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });
        binding.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(1);
            }
        });
        binding.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(2);
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               saveImage();
//                view.setDrawingCacheEnabled(true);
//                // this is the important code :)
//                // Without it the view will have a dimension of 0,0 and the bitmap will be null
//
//                view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//
//                view.buildDrawingCache(true);
//                Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
//                view.setDrawingCacheEnabled(false); // clear drawing cache
//
//                bmImage.setImageBitmap(b);
//                Bitmap image = getBitmapFromView(binding.cardView);
//                binding.imageView2.setImageBitmap(image);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
             fileName = new File(data.getData().getPath()).getName();
            binding.imageView.setImageURI(imageUri);


        }
    }
    public void saveImage(){
        binding.cardView.setDrawingCacheEnabled(true);
        binding.cardView.buildDrawingCache();
        binding.cardView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap=binding.cardView.getDrawingCache();
        String root= Environment.getExternalStorageDirectory().getAbsolutePath();
        File file=new File(root+"/Pictures");
        Log.d("fileName",fileName+".jpg");
        String data=fileName.replaceAll(":", ".") + ".jpg";
        File myFile=new File(commonDocumentDirPath("AddTexts"),data);
        if(myFile.exists()){
            Utils.toast(getApplicationContext(),"this image already exists");
            myFile.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            binding.imageView.setDrawingCacheEnabled(false);
            Utils.toast(getApplicationContext(),"Image Edited Succesfully");
        }catch (Exception ex){
          binding.tvName.setText(ex+"");
        }


    }
    public void save(){

    }
    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    private void showBottomSheetDialog(int type) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AddTextActivity.this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_edit);
        EditText text = bottomSheetDialog.findViewById(R.id.editText);
        Button add = bottomSheetDialog.findViewById(R.id.btnDone);

        TextView title = bottomSheetDialog.findViewById(R.id.tvEditDiary);
        bottomSheetDialog.show();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isEmpty(text)) {
                    Utils.toast(AddTextActivity.this, "please add note");
                } else {
                    if(type==1) {
                        binding.tvName.setText(text.getText().toString());
                    }
                    if(type==2){
                        binding.tvDate.setText(text.getText().toString());
                    }
                    bottomSheetDialog.hide();
                }
            }
        });

    }
    public File commonDocumentDirPath(String FolderName) {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success) {
                dir = null;
            }
        }
        return dir;
    }
}