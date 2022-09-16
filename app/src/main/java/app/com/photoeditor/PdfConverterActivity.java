package app.com.photoeditor;

import static app.com.photoeditor.PngToJpgConverterActivity.savePicture;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import android.media.Image;
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

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.com.photoeditor.databinding.ActivityPdfConverterBinding;

public class PdfConverterActivity extends AppCompatActivity {
    ActivityPdfConverterBinding binding;
    public static final int GALLERY_PICTURE = 1;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
 //   List<ImageModel> bitmap;

    String fileName;
    public static final int REQUEST_PERMISSIONS = 1;
    File myfile;
    List<Bitmap> listUri;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfConverterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listUri = new ArrayList<>();
     //   bitmap = new ArrayList<>();
        fn_permission();
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_PICTURE);
                Hide();
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePicture(fileName,bitmap,PdfConverterActivity.this);
                Intent intent1 = new Intent(getApplicationContext(), FileNameActivity.class);
                intent1.putExtra("fileName",fileName);
               // intent1.putExtra("bitmap",bitmap);
                startActivity(intent1);


            }
//                if (boolean_save) {
//
//                    Intent intent1 = new Intent(getApplicationContext(), PdfConverterActivity.class);
//                    startActivity(intent1);
//
//                } else {
//                    createPDF();
//                }
//            }
              });

    }

    private void createPdf() {

//        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        float hight = displaymetrics.heightPixels;
//        float width = displaymetrics.widthPixels;
//
//        int convertHighet = (int) hight, convertWidth = (int) width;
//
////        Resources mResources = getResources();
////        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);
//
//        PdfDocument document = new PdfDocument();
//        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
//        PdfDocument.Page page = document.startPage(pageInfo);
//
//        Canvas canvas = page.getCanvas();
//
//
//        Paint paint = new Paint();
//        paint.setColor(Color.parseColor("#ffffff"));
//        canvas.drawPaint(paint);
//
//
//        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
//
//        paint.setColor(Color.BLUE);
//        canvas.drawBitmap(bitmap, 0, 0, null);
//        document.finishPage(page);
//
//
//        // write the document content
//        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
//        File file = new File(root + "/Pictures");
//        Log.d("fileName", "test" + ".pdf");
//
//        File myFile = new File(commonDocumentDirPath("PdfGenerator"), fileName + ".pdf");
//
//        try {
//            document.writeTo(new FileOutputStream(myFile));
//            binding.button.setText("Check PDF");
//            boolean_save = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
//        }
//
//        // close the document
//        document.close();
//        showDialog(myFile.toString());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 7) {
//            if (resultCode == RESULT_OK) {
//                if (data != null) {
//                    // Checking for selection multiple files or single.
//
//                            Uri selectedImage = data.getData();
//                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                                   Bitmap bits=null;
//                            try {
//                                bits = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getClipData().getItemAt(index).getUri());
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                    Log.d("FORMAT",GetMimeType(PngToJpgConverterActivity.this,selectedImage));
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    fileName = new File(data.getData().getPath()).getName();
//                    Cursor cursor = getContentResolver().query(
//                            selectedImage, filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String filePath = cursor.getString(columnIndex);
//                    cursor.close();
//                    bitmap = BitmapFactory.decodeFile(filePath);
//                    binding.imageView.setImageBitmap(bitmap);
////                            fileName = new File(data.getData().getN;
////                            Cursor cursor = getContentResolver().query(
////                                    selectedImage, filePathColumn, null, null, null);
////                            cursor.moveToFirst();
////
////                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////                            String filePath = cursor.getString(columnIndex);
////                            cursor.close();
////                            ImageModel datsa = new ImageModel();
////                            datsa.bitmap = bits;
////                            Log.d("bitmaps",bits+"");
////                            datsa.fileName = fileName;
////                            bitmap.add(datsa);
////                            binding.button.setClickable(true);
////
////                        binding.rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
////                        adapter = new ImageAdapter(getApplicationContext(), bitmap);
////                        binding.rec.setAdapter(adapter);
//
//                }
//            }
//        }
//    }
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {

        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

                Log.d("FORMAT",GetMimeType(PdfConverterActivity.this,selectedImage));
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                fileName = new File(data.getData().getPath()).getName();
                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                bitmap = BitmapFactory.decodeFile(filePath);
                binding.imageView4.setImageBitmap(bitmap);
                Hide();



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

    public  void share(String path) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(path);
        sharingIntent.setType("application/pdf");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share pdf using"));
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

//    private void createPDF() {
//
//            Bitmap bitmaps;
//            PdfDocument document = new PdfDocument();
//            //  int height = 842;
//            //int width = 595;
//            int height = 1010;
//            int width = 714;
//            int reqH, reqW;
//            reqW = width;
//
//            for (int i = 0; i < bitmap.size(); i++) {
//                //  bitmap = BitmapFactory.decodeFile(array.get(i));
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                reqH = width * bitmap.get(i).getBitmap().getHeight() / bitmap.get(i).getBitmap().getWidth();
//                Log.e("reqH", "=" + reqH);
//                if (reqH < height) {
//                    //  bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
//                } else {
//                    reqH = height;
//                    reqW = height * bitmap.get(i).getBitmap().getWidth() / bitmap.get(i).getBitmap().getHeight();
//                    Log.e("reqW", "=" + reqW);
//                    //   bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
//                }
//                // Compress image by decreasing quality
//                // ByteArrayOutputStream out = new ByteArrayOutputStream();
//                //  bitmap.compress(Bitmap.CompressFormat.WEBP, 50, out);
//                //    bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//                //bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
//                //Create an A4 sized page 595 x 842 in Postscript points.
//                //PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
//                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(reqW, reqH, 1).create();
//                PdfDocument.Page page = document.startPage(pageInfo);
//                Canvas canvas = page.getCanvas();
//
//                Log.e("PDF", "pdf = " + bitmap.get(i).getBitmap().getWidth() + "x" + bitmap.get(i).getBitmap().getHeight());
//                canvas.drawBitmap(bitmap.get(i).getBitmap(), 0, 0, null);
//
//                document.finishPage(page);
//            }
//            File myFile = new File(commonDocumentDirPath("PdfGenerator"), fileName + ".pdf");
//
//            FileOutputStream fos;
//            try {
//                fos = new FileOutputStream(myFile);
//                document.writeTo(fos);
//                document.close();
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//           showDialog(fileName.toString());
//
//
//
//        };
    public void Hide(){
        if(bitmap!=null){
            binding.imageView5.setVisibility(View.GONE);
            binding.textView3.setVisibility(View.GONE);
            binding.textView4.setVisibility(View.GONE);
        }
    }

    }


