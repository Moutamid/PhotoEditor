package app.com.photoeditor;

import static app.com.photoeditor.AddTextActivity.commonDocumentDirPath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import app.com.photoeditor.databinding.ActivityGeneratePdfBinding;
import id.zelory.compressor.Compressor;

public class JpgConverterActvity extends AppCompatActivity {
    ActivityGeneratePdfBinding binding;
    Bitmap bitmap;
    Boolean state = false, whiteMargin, Compress, pass;
    int compressSize = 0;
    String orientation;
    String outputFolder;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGeneratePdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageView3.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        Intent intent = getIntent();
        String FileName = intent.getStringExtra("fileName");
        getPreviousData();
        bitmap=loadPicture(FileName);
        fileName=intent.getStringExtra("fileName");
        binding.tvFileName.setText("PngToJpg_"+FileName);
        binding.tvFileSize.setText(byteSizeOf(bitmap)/1024+"Kb");
        java.util.Date date = new java.util.Date();
        binding.tvDate.setText(date + "");
        binding.imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = true;
                saveImage();
            }
        });
        binding.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JpgConverterActvity.this,MainActivity.class));
                finish();
            }
        });
        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(outputFolder);
            }
        });
        binding.imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state){
                    Utils.toast(getApplicationContext(),outputFolder);
                }
                else{
                    Utils.toast(getApplicationContext(),"please save the file");
                }
            }
        });
    }

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public void saveImage(){
        String root= Environment.getExternalStorageDirectory().getAbsolutePath();
        if (Compress) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressSize, out);

        }
        if (whiteMargin) {
            bitmap = addWhiteBorder(bitmap,5);
        }
        if(orientation=="Vertical"){
            bitmap=GetRotatedBitmap(bitmap,90);
        }
        if(orientation=="Horizontal") {
            bitmap=GetRotatedBitmap(bitmap,180);
        }
        File myFile=new File(commonDocumentDirPath("PngToJpg"),"PngToJpg_"+fileName+".jpg");
        outputFolder=myFile.toString();

        if(myFile.exists()){
            Utils.toast(getApplicationContext(),"this image already exists");
            myFile.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Utils.toast(getApplicationContext(),"Image Converted Succesfully");
        }catch (Exception ex){
            Toast.makeText(this, ex+"", Toast.LENGTH_SHORT).show();
        }


    }
    public  void share(String path) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(path);
        sharingIntent.setType("application/pdf");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share pdf using"));
    }
    private Bitmap loadPicture(String filename) {
        Bitmap b = null;

        try {
            FileInputStream fis = openFileInput(filename);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(fis);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            b = BitmapFactory.decodeStream(ois);
            try {
                ois.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }
    public void getPreviousData() {
        Intent intent = getIntent();
        whiteMargin = intent.getBooleanExtra("whiteMargin", false);
        pass = intent.getBooleanExtra("pass", false);
        Compress = intent.getBooleanExtra("compresss", false);
        compressSize = intent.getIntExtra("compressSize", 100);
        orientation = intent.getStringExtra("orientation");

    }
    private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }
    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap.recycle();/*from  w  w w.  ja v a2s  . co m*/
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }

        return bitmap;
    }

}