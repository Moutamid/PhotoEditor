package app.com.photoeditor;

import static app.com.photoeditor.AddTextActivity.commonDocumentDirPath;
import static app.com.photoeditor.GeneratePdfActivity.byteSizeOf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import app.com.photoeditor.databinding.ActivityCompressBinding;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.constraint.Compression;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class CompressActivity extends AppCompatActivity {
    ActivityCompressBinding binding;
    String fileName;
    int qualtiy = 0;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageView.setImageResource(R.drawable.ic_add_image_svgrepo_com);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                11);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                qualtiy = i;
                compress(i);
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
                Intent intent = new Intent(CompressActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, false);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CROP, true);//default is false
                startActivityForResult(intent, 2);
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

    public void saveImage() {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root + "/Pictures");
        Log.d("fileName", fileName + ".jpg");
        String data = fileName.replaceAll(":", ".") + ".jpg";
        File myFile = new File(commonDocumentDirPath("Compressed"), data);
        if (myFile.exists()) {
            Utils.toast(getApplicationContext(), "this image already exists");
            myFile.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, qualtiy, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            binding.imageView.setDrawingCacheEnabled(false);
            Utils.toast(getApplicationContext(), "Image Compressed Succesfully");
        } catch (Exception ex) {
            //  binding.tvName.setText(ex+"");
        }
        refreshGallery(myFile);
    }
    public void refreshGallery(File f) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fileUri = Uri.fromFile(f); //out is your output file
            mediaScanIntent.setData(fileUri);
            sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            fileName = new File(data.getData().getPath()).getName();
            binding.imageView.setImageURI(imageUri);
            binding.imageView.setDrawingCacheEnabled(true);
            binding.imageView.buildDrawingCache();
            binding.imageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            bitmap = binding.imageView.getDrawingCache();
            binding.textView15.setText(byteSizeOf(bitmap) / 1024 + "Kb");

        }
    }
    public void compress(int qualtiy){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, qualtiy, out);
        binding.textView15.setText(byteSizeOf(bitmap) / 1024 + "Kb");
    }

}