package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TextFileHandler {

    public static void write(Context context, String filePath, String... text) {
        try {
            FileOutputStream fileout = context.openFileOutput(filePath, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            for (int i = 0; i < text.length; i++) {
                outputWriter.write(text[i]);
            }
            outputWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String read(Context context, String filePath) {
        try {
            InputStream instream = context.openFileInput(filePath);

            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String result = "";
                String line = "";
                try {
                    while ((line = buffreader.readLine()) != null)
                        result += line;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
