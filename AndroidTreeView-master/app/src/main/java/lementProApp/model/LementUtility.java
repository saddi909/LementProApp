package lementProApp.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Saadi on 14/07/2015.
 */
public class LementUtility {


    public static String convertJSONDate(String date){
        if(date.equals("null")){
            return "";
        }
        String timestamp = date.replace("/Date(", "").replace(")/", "");;
        Date parsedDate = new Date(Long.parseLong(timestamp));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String formattedDate = sdf.format(parsedDate);
        return formattedDate;
    }
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
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
        return resizedBitmap;
    }
}
