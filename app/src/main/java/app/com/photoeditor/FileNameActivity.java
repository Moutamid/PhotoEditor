package app.com.photoeditor;

import static app.com.photoeditor.AddTextActivity.commonDocumentDirPath;
import static app.com.photoeditor.GeneratePdfActivity.RotateBitmap;
import static app.com.photoeditor.GeneratePdfActivity.byteSizeOf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.nanchen.compresshelper.CompressHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import app.com.photoeditor.databinding.ActivityFileNameBinding;

public class FileNameActivity extends AppCompatActivity {
    ActivityFileNameBinding binding;
    Boolean compress = false, whitemargin = false, pass = false;
    String orientation, filePath_;
    int compressSize = 0;
    Bitmap bitmap;
    File newFile;
    File PdfFile;
    String fileName;
    String spnOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        filePath_ = intent.getStringExtra("filePath");
        binding.tvFileName.setText("ImgToPdf_" + fileName);
        binding.textView5.setText("Compress Pdf");
        setCompress(fileName, filePath_, 100);
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), GeneratePdfActivity.class);
                intent1.putExtra("fileName", fileName);
                intent1.putExtra("whiteMargin", whitemargin);
                intent1.putExtra("pass", pass);
                intent1.putExtra("compresss", compress);
                intent1.putExtra("compressSize", compressSize);
                intent1.putExtra("orientation", orientation);
                intent1.putExtra("filePath", PdfFile.getPath());
                //        intent1.putExtra("bitmap", bitmap);
                startActivity(intent1);
            }
        });
        binding.seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                compressSize = i;
                setCompress(fileName, filePath_, i);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                whitemargin = b;
            }
        });
        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chk(b);
                compress = b;
            }
        });
        binding.switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                pass = b;

            }
        });
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                orientation = adapterView.getSelectedItem().toString();
                if (orientation.equals("Default")) {
                    bitmap = RotateBitmap(bitmap, 0);
                } else if (orientation.equals("90 degree left")) {

                    bitmap = RotateBitmap(bitmap, -90);

                } else if (orientation.equals("90 degree right")) {

                    bitmap = RotateBitmap(bitmap, 90);
                }
                binding.imageView2.setImageBitmap(bitmap);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

    public void setCompress(String fileName, String filePath_, int quality) {
        File oldFile = new File(filePath_);
        newFile = new CompressHelper.Builder(this)
                .setQuality(quality)
                .setFileName(fileName)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                        File.separator +
                        getResources().getString(R.string.app_name))
                .build()
                .compressToFile(oldFile);
        chk(binding.switch1.isChecked());

    }

    public void chk(Boolean b) {
        if (b) {
            String filePath = newFile.getPath();
            bitmap = BitmapFactory.decodeFile(filePath);
            binding.seekBar2.setVisibility(View.VISIBLE);

        } else {
            bitmap = loadPicture(fileName);
            binding.seekBar2.setVisibility(View.GONE);
        }
        Log.d("BitmapVaule", bitmap + "");
        binding.imageView2.setImageBitmap(bitmap);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createPDF();
            }
        }, 1000);


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
            bitmaps = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
        } else {
            reqH = height;
            reqW = height * bitmap.getWidth() / bitmap.getHeight();
            Log.e("reqW", "=" + reqW);
            bitmaps = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
        }

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(reqW, reqH, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmaps, 0, 0, null);
        document.finishPage(page);
        PdfFile = new File(commonDocumentDirPath("PdfGenerator"), binding.tvFileName.getText().toString() + ".pdf");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(PdfFile);
            document.writeTo(fos);
            document.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        binding.textView13.setText(PdfFile.length() / 1024 + "KB");

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void rotate() {
        if (orientation.equals("Default")) {
            bitmap = RotateBitmap(bitmap, 0);
        } else if (orientation.equals("90 degree left")) {
            bitmap = RotateBitmap(bitmap, -90);

        } else if (orientation.equals("90 degree right")) {
            bitmap = RotateBitmap(bitmap, 90);
        }

    }
}