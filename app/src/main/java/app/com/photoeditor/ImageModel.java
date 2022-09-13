package app.com.photoeditor;

import android.graphics.Bitmap;

public class ImageModel {
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    String fileName;
    Bitmap bitmap;
}
