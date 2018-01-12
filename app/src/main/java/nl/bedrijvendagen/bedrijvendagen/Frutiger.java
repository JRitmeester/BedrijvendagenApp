package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class Frutiger {
    public static void setTypeface(Context c, TextView tv) {
        // Create Frutiger font, first with .otf, as files were provided in .otf format, after in .ttf is .otf fails for whatever reason.
        Typeface frutiger;
        frutiger = Typeface.createFromAsset(c.getAssets(), "fonts/FrutigerLTPro-Roman.otf");
        tv.setTypeface(frutiger);
    }
}
