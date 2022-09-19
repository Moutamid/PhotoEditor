package app.com.photoeditor;

import static app.com.photoeditor.PdfConverterActivity.GALLERY_PICTURE;
import static app.com.photoeditor.PdfConverterActivity.REQUEST_PERMISSIONS;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import app.com.photoeditor.databinding.ActivityAddTextFormActvitityBinding;
import id.zelory.compressor.Compressor;
import kotlinx.coroutines.Dispatchers;

public class AddTextFormActvitity extends AppCompatActivity {
    ActivityAddTextFormActvitityBinding binding;
    String fileName;
    Bitmap bitmap;
    boolean boolean_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTextFormActvitityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fn_permission();
        binding.btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_PICTURE);
            }
        });
        binding.EtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        binding.btnGeneratePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isEmpty(binding.EtDate) || Utils.isEmpty(binding.EtEnterName) || bitmap == null) {
                    Utils.toast(getApplicationContext(), "Please Fill All the Form");
                } else {
                    savePicture(fileName, bitmap, AddTextFormActvitity.this);
                    Intent intent1 = new Intent(getApplicationContext(), AddTextActivity.class);
                    intent1.putExtra("fileName", fileName);
                    intent1.putExtra("Name", binding.EtEnterName.getText().toString());
                    intent1.putExtra("Date", binding.EtDate.getText().toString());
                    startActivity(intent1);
                }
            }
        });
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(AddTextFormActvitity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(AddTextFormActvitity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(AddTextFormActvitity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(AddTextFormActvitity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {

            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                fileName = new File(data.getData().getPath()).getName();
                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                bitmap = BitmapFactory.decodeFile(filePath);
                binding.textView9.setText(fileName);

            }
        }
    }
     void savePicture(String filename, Bitmap b, Context ctx) {
        try {
            ObjectOutputStream oos;
            FileOutputStream out;// = new FileOutputStream(filename);
            out = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(out);
            b.compress(Bitmap.CompressFormat.PNG, 100, oos);
            oos.close();
            oos.notifyAll();
            out.notifyAll();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    public void showDialog() {
        int mYear, mMonth, mDay;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                binding.EtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}