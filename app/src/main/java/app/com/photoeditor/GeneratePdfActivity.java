package app.com.photoeditor;

import static java.lang.System.out;
import static app.com.photoeditor.AddTextActivity.commonDocumentDirPath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

public class GeneratePdfActivity extends AppCompatActivity {
    ActivityGeneratePdfBinding binding;
    Bitmap bitmap;
    Boolean state = false, whiteMargin, Compress, pass;
    int compressSize = 0;
    String orientation;
    String outputFolder;
    String filePath_;
    File newFile;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGeneratePdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("PdfFiles",MODE_PRIVATE);
        Intent intent = getIntent();
        String FileName = intent.getStringExtra("fileName");
        filePath_ = intent.getStringExtra("filePath");
        newFile=new File(filePath_) ;
        getPreviousData();
        if(Compress){

            String filePath = newFile.getPath();
             bitmap = BitmapFactory.decodeFile(filePath);
        }
        else {
            bitmap = loadPicture(FileName);
        }
        binding.tvFileName.setText("ImgToPdf_" + FileName);
        binding.tvFileSize.setText(newFile.length() / 1024 + "Kb");
        java.util.Date date = new java.util.Date();
        binding.tvDate.setText(date + "");
        binding.imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = true;
                Utils.toast(getApplicationContext(),"Pdf Generated Succesfully");
                binding.textView17.setText("Saved in:"+filePath_);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean(FileName, true);
                myEdit.commit();

            }
        });
        binding.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GeneratePdfActivity.this, MainActivity.class));
            }
        });
        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(filePath_);
            }
        });
        binding.imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state) {
                    Utils.toast(getApplicationContext(), outputFolder);
                } else {
                    Utils.toast(getApplicationContext(), "please save the file");
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

    private void createPDF() {
        Bitmap bitmaps;
        PdfDocument document = new PdfDocument();
        //  int height = 842;
        //int width = 595;
        int height = 1010;
        int width = 714;
        int reqH, reqW;
        reqW = width;

        //  bitmap = BitmapFactory.decodeFile(array.get(i));
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        reqH = width * bitmap.getHeight() / bitmap.getWidth();
        Log.e("reqH", "=" + reqH);
        if (reqH < height) {
            bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
        } else {
            reqH = height;
            reqW = height * bitmap.getWidth() / bitmap.getHeight();
            Log.e("reqW", "=" + reqW);
            bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
        }
        // Compress image by decreasing quality

        //    bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        //bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
        //Create an A4 sized page 595 x 842 in Postscript points.
        //PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(reqW, reqH, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();


        Log.e("PDF", "pdf = " + bitmap.getWidth() + "x" + bitmap.getHeight());
        if (whiteMargin) {
            bitmap = addWhiteBorder(bitmap, 5);
        }
        canvas.drawBitmap(bitmap, 0, 0, null);



        document.finishPage(page);

        File myFile = new File(commonDocumentDirPath("PdfGenerator"), binding.tvFileName.getText().toString() + ".pdf");
        if(myFile.exists()){
            myFile = new File(commonDocumentDirPath("PdfGenerator"), binding.tvFileName.getText().toString()+"_1" + ".pdf");
        }
        outputFolder = myFile.toString();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(myFile);
            document.writeTo(fos);
            document.close();
            fos.close();
            Utils.toast(getApplicationContext(), "Pdf Generated Succesfully");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void share(String path) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(path);
        sharingIntent.setType("application/pdf");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share pdf using"));
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
        if (Compress) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Utils.toast(getApplicationContext(), compressSize + "");
            b.compress(Bitmap.CompressFormat.JPEG, compressSize, out);
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

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        /*Matrix matrix = new Matrix();

        matrix.postRotate(90);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, width, height, true);

        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);*/
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