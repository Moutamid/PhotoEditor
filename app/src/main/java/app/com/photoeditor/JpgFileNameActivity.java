package app.com.photoeditor;

import static app.com.photoeditor.AddTextActivity.commonDocumentDirPath;
import static app.com.photoeditor.GeneratePdfActivity.RotateBitmap;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import app.com.photoeditor.databinding.ActivityFileNameBinding;

public class JpgFileNameActivity extends AppCompatActivity {
    ActivityFileNameBinding binding;
    Boolean compress = false, whitemargin = false, pass = false;
    String orientation,filePath_;;
    int compressSize=0;
    Bitmap bitmap;
    File newFile;
    File PdfFile;
    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("bitmap");
        fileName = intent.getStringExtra("fileName");
        filePath_ = intent.getStringExtra("filePath");
        setCompress(fileName, filePath_, 100);
        binding.textView13.setText(newFile.length() / 1024 + "KB");
        binding.tvFileName.setText("PngToJpg_"+fileName);
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), JpgConverterActvity.class);
                intent1.putExtra("fileName", fileName);

//                intent1.putExtra("bitmap", bitmap);
                startActivity(intent1);
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), JpgConverterActvity.class);
                intent1.putExtra("fileName", fileName);
                intent1.putExtra("whiteMargin",whitemargin);
                intent1.putExtra("pass",pass);
                intent1.putExtra("compresss",compress);
                intent1.putExtra("compressSize",compressSize);
                intent1.putExtra("orientation",orientation);
                intent1.putExtra("filePath", newFile.getPath());
                //        intent1.putExtra("bitmap", bitmap);
                startActivity(intent1);
            }
        });
        binding.seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                compressSize=i;
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
                compress = b;
                chk(b);
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
             rotate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        binding.textView13.setText(newFile.length() / 1024 + "KB");

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
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                createPDF();
//            }
//        }, 1000);


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
    public void chks(Boolean b) {
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



    }
public  void rotate(){
    if (orientation.equals("Default")) {
        bitmap = RotateBitmap(bitmap, 0);
    } else if (orientation.equals("90 degree left")) {
        bitmap = RotateBitmap(bitmap, -90);

    } else if (orientation.equals("90 degree right")) {
        bitmap = RotateBitmap(bitmap, 90);
    }
    binding.imageView2.setImageBitmap(bitmap);
}
}