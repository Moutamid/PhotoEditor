package app.com.photoeditor;

import static app.com.photoeditor.GeneratePdfActivity.byteSizeOf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import app.com.photoeditor.databinding.ActivityFileNameBinding;

public class JpgFileNameActivity extends AppCompatActivity {
    ActivityFileNameBinding binding;
    Boolean compress = false, whitemargin = false, pass = false;
    String orientation;
    int compressSize=0;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button2.setText("Convert To Jpg");
        binding.imageView2.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        Intent intent = getIntent();

        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("bitmap");
        String fileName = intent.getStringExtra("fileName");
        bitmap = loadPicture(fileName);

        binding.textView13.setText(byteSizeOf(bitmap) / 1024 + "Kb");
        binding.tvFileName.setText("PngToJpg_"+fileName);
        binding.imageView2.setImageBitmap(bitmap);
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
                //        intent1.putExtra("bitmap", bitmap);
                startActivity(intent1);
            }
        });
        binding.seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                compressSize=i;
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
}