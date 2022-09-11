package app.com.photoeditor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import app.com.photoeditor.databinding.ActivityPdfConverterBinding;

public class PdfConverterActivity extends AppCompatActivity {
    ActivityPdfConverterBinding binding;
    public static final int GALLERY_PICTURE = 1;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    String fileName;
    public static final int REQUEST_PERMISSIONS = 1;
    File myfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfConverterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageView.setImageResource(R.drawable.ic_add_image_svgrepo_com);
        fn_permission();
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_PICTURE);
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boolean_save) {

                    Intent intent1 = new Intent(getApplicationContext(), PdfConverterActivity.class);
                    startActivity(intent1);

                } else {
                    createPdf();
                }
            }
        });
    }

    private void createPdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);


        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);


        // write the document content
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root + "/Pictures");
        Log.d("fileName", "test" + ".pdf");

        File myFile = new File(commonDocumentDirPath("PdfGenerator"), fileName + ".pdf");

        try {
            document.writeTo(new FileOutputStream(myFile));
            binding.button.setText("Check PDF");
            boolean_save = true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        showDialog(myFile.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {

            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                fileName = new File(data.getData().getPath()).getName();
                Log.d("imageFormat",GetMimeType(PdfConverterActivity.this,selectedImage));
                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();


                bitmap = BitmapFactory.decodeFile(filePath);
                binding.imageView.setImageBitmap(bitmap);


                binding.button.setClickable(true);
            }
        }
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(PdfConverterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(PdfConverterActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(PdfConverterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(PdfConverterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
            //    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

//    public void create() {
//        File file = new File(Environment.getExternalStorageDirectory() + "/Sample Directory");
//        boolean success = true;
//        if (file.exists()) {
//            Toast.makeText(getApplicationContext(), "Directory does not exist, create it",
//                    Toast.LENGTH_LONG).show();
//        }
//        if (success) {
//            Toast.makeText(getApplication(), "Directory created",
//                    Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(this, "Failed to create Directory",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PdfConverterActivity.this);
        builder1.setTitle("Success");
        builder1.setMessage("Your pdf Created Succesfully");
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
                        startActivity(new Intent(PdfConverterActivity.this, MainActivity.class));
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void share(String path) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(path);
        sharingIntent.setType("application/pdf");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share pdf using"));
    }
    public  String GetMimeType(Context context, Uri uriImage)
    {
        String strMimeType = null;

        Cursor cursor = context.getContentResolver().query(uriImage,
                new String[] { MediaStore.MediaColumns.MIME_TYPE },
                null, null, null);

        if (cursor != null && cursor.moveToNext())
        {
            strMimeType = cursor.getString(0);
        }

        return strMimeType;
    }
}