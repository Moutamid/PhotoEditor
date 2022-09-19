package app.com.photoeditor;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {
    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    public  static void toast(Context context,String Message){
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }


    static public boolean resetExternalStorageMedia(Context context) {
        if (Environment.isExternalStorageEmulated())
            return (false);
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
        Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);

        context.sendBroadcast(intent);
        return (true);
    }

    static public void notifyMediaScannerService(Context context, String path) {
        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
}
