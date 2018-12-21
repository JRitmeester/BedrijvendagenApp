package nl.bedrijvendagen.bedrijvendagen;


import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class CompanyCredentials {
    public static boolean auth = false;
    public static int company_id = -1;
    public static String token = "";
    public static String company = "";
    public static String email = "";
    public static String password = "";
    public static String password_raw = "";

    public static void reset() {
        auth = false;
        company_id = -1;
        token = "default";
        company = "default";
        email = "default";
        password = "default";
        password_raw = "default";
    }

    public static String getPassword(final Context c) {
        SharedPreferences sp = c.getSharedPreferences("Login", MODE_PRIVATE);
        return sp.getString("PASSWORD", "");
    }

    public static String getUsername(final Context c) {
        SharedPreferences sp = c.getSharedPreferences("Login", MODE_PRIVATE);
        return sp.getString("USERNAME", "");
    }
}
