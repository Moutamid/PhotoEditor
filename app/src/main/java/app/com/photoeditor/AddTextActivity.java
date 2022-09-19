package app.com.photoeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import app.com.photoeditor.databinding.ActivityAddTextBinding;

public class AddTextActivity extends AppCompatActivity {
ActivityAddTextBinding binding;
String fileName;
String Date;
String Name;
Boolean state=false;
Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddTextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle b=getIntent().getExtras();
        fileName=b.getString("fileName","nothing");
        Date=b.getString("Date");
        bitmap=loadPicture(fileName);
        binding.tvDate.setText(Date);
        binding.tvName.setText(b.getString("Name"));
        binding.imageView.setImageBitmap(bitmap);
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
        binding.cardView.setDrawingCacheEnabled(true);
        binding.cardView.buildDrawingCache();
        binding.cardView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap=binding.cardView.getDrawingCache();
        String root= Environment.getExternalStorageDirectory().getAbsolutePath();
        File file=new File(root+"/Pictures");
        Log.d("fileName",fileName+".jpg");
        String data=fileName.replaceAll(":", ".") + ".jpg";
        File myFile;
        myFile=new File(commonDocumentDirPath("AddTexts"),data);
        if(myFile.exists()){
            fileName=fileName+"_1";
            data=fileName.replaceAll(":", ".") + ".jpg";
            myFile=new File(commonDocumentDirPath("AddTexts"),data);
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

    public static File commonDocumentDirPath(String FolderName) {
        File dir = null;
        File i=new File("path");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/"+ "PhotoEditor"+"/"+FolderName);
        } else {
            dir = new File(Environment.getExternalStorageDirectory()+"/"+R.string.app_name+"/"+ FolderName);
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
    public Bitmap loadPicture(String filename) {
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
}