package app.com.photoeditor;

import static app.com.photoeditor.AddTextActivity.commonDocumentDirPath;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileOutputStream;

import app.com.photoeditor.databinding.ActivityCompressBinding;

public class CompressActivity extends AppCompatActivity {
ActivityCompressBinding binding;
String fileName;
int qualtiy=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCompressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageView.setImageResource(R.drawable.ic_add_image_svgrepo_com);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                11);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                qualtiy=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
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
    public void saveImage(){
        binding.imageView.setDrawingCacheEnabled(true);
        binding.imageView.buildDrawingCache();
        binding.imageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap=binding.imageView.getDrawingCache();
        String root= Environment.getExternalStorageDirectory().getAbsolutePath();
        File file=new File(root+"/Pictures");
        Log.d("fileName",fileName+".jpg");
        String data=fileName.replaceAll(":", ".") + ".jpg";
        File myFile=new File(commonDocumentDirPath("Compressed"),data);
        if(myFile.exists()){
            Utils.toast(getApplicationContext(),"this image already exists");
            myFile.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, qualtiy, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            binding.imageView.setDrawingCacheEnabled(false);
            Utils.toast(getApplicationContext(),"Image Compressed Succesfully");
        }catch (Exception ex){
          //  binding.tvName.setText(ex+"");
        }


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
}