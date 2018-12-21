package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TextFileHandler {

    public static void write(Context context, String file, String text) {
        try {

            FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            Log.d("Saving comment", "writing " + text + " to " + file);
            fos.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String read(Context context, String file) {
        try {
            FileInputStream fis = context.openFileInput(file);
            InputStreamReader isr = new InputStreamReader(fis);

            BufferedReader br = new BufferedReader(isr);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = br.readLine()) != null) {
                stringBuffer.append(lines);
            }
            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(file)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String result = "";
//        String line;
//        try {
//            while ((line = reader.readLine()) != null)
//                result += line;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Log.d("Saving comment", "Read  " + result + " from " + file);
//        return result;
        return null;
    }
}
