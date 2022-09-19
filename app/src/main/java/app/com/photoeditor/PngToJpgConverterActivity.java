package app.com.photoeditor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import app.com.photoeditor.databinding.ActivityPngToJpgConverterBinding;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class PngToJpgConverterActivity extends AppCompatActivity {
    ActivityPngToJpgConverterBinding binding;
    String fileName;
    Bitmap bitmap;
    public static final int GALLERY_PICTURE = 1;
    String filePath_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPngToJpgConverterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.button.setText("Convert To Jpg");
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PngToJpgConverterActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, false);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CROP, true);//default is false
                startActivityForResult(intent, GALLERY_PICTURE);
                Hide();
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap == null) {
                    Utils.toast(getApplicationContext(), "please Add Image");
                } else {
                    savePicture(fileName, bitmap, PngToJpgConverterActivity.this);
                    Intent intent1 = new Intent(getApplicationContext(), JpgFileNameActivity.class);
                    intent1.putExtra("fileName", fileName);
                    intent1.putExtra("filePath", filePath_);
                    //     intent1.putExtra("bitmap",bitmap);

                    startActivity(intent1);
                    //  saveImage();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {

            if (resultCode == RESULT_OK) {
                String filePaths = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Log.d("FORMAT", getExtension(new File(filePaths).getName()));
             //   Uri selectedImage = data.getData();
                if (!getExtension(new File(filePaths).getName()).contains("png")) {
                    Utils.toast(getApplicationContext(), "please select png file");

                } else {

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    fileName = new File(filePaths).getName();
                    SharedPreferences sh = getSharedPreferences("JpgFiles", MODE_PRIVATE);
                    if(sh.getBoolean(fileName,false)){
                        fileName=fileName+"_1"  ;
                    }
//                    Cursor cursor = getContentResolver().query(
//                            selectedImage, filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String filePath = cursor.getString(columnIndex);
//                    cursor.close();
                    filePath_=filePaths;
                    bitmap = BitmapFactory.decodeFile(filePaths);
                    binding.imageView4.setImageBitmap(bitmap);
                    Hide();

                }

            }
        }
    }

    public String GetMimeType(Context context, Uri uriImage) {
        String strMimeType = null;

        Cursor cursor = context.getContentResolver().query(uriImage,
                new String[]{MediaStore.MediaColumns.MIME_TYPE},
                null, null, null);

        if (cursor != null && cursor.moveToNext()) {
            strMimeType = cursor.getString(0);
        }

        return strMimeType;
    }

    public void saveImage() {
        binding.imageView.setDrawingCacheEnabled(true);
        binding.imageView.buildDrawingCache();
        binding.imageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        Bitmap bitmap = binding.imageView.getDrawingCache();
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root + "/Pictures");
        Log.d("fileName", fileName + ".jpg");
        File myFile = new File(commonDocumentDirPath("PngToJpg"), fileName + ".jpg");
        if (myFile.exists()) {
            Utils.toast(getApplicationContext(), "this image already exists");
            myFile.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            showDialog(myFile.toString());
            binding.imageView.setDrawingCacheEnabled(false);
            Utils.toast(getApplicationContext(), "Image Converted Succesfully");
        } catch (Exception ex) {
            Toast.makeText(this, ex + "", Toast.LENGTH_SHORT).show();
        }


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

    public void showDialog(String path) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PngToJpgConverterActivity.this);
        builder1.setTitle("Success");
        builder1.setMessage("Image Converted Succesfully");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Share",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        share(path);
                    }
                });

        builder1.setNegativeButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(PngToJpgConverterActivity.this, MainActivity.class));
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void share(String path) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(path);
        sharingIntent.setType("application/jpg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share pdf using"));
    }

     void savePicture(String filename, Bitmap b, Context ctx) {
        try {
            ObjectOutputStream oos;
            FileOutputStream out;// = new FileOutputStream(filename);
            out = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(out);
            b.compress(Bitmap.CompressFormat.JPEG, 100, oos);
            oos.close();
            oos.notifyAll();
            out.notifyAll();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Hide() {
        if (bitmap != null) {
            binding.imageView5.setVisibility(View.GONE);
            binding.textView3.setVisibility(View.GONE);
            binding.textView4.setVisibility(View.GONE);
        }
    }
    public static String getExtension(String fileName) {
        char ch;
        int len;
        if(fileName==null ||
                (len = fileName.length())==0 ||
                (ch = fileName.charAt(len-1))=='/' || ch=='\\' || //in the case of a directory
                ch=='.' ) //in the case of . or ..
            return "";
        int dotInd = fileName.lastIndexOf('.'),
                sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if( dotInd<=sepInd )
            return "";
        else
            return fileName.substring(dotInd+1).toLowerCase();
    }
}