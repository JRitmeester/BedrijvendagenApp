package nl.bedrijvendagen.bedrijvendagen;

import android.util.Log;

public class StudentCredentials {
    public static int userID = -1;
    public static String firstName = "default";
    public static String lastName = "default";
    public static boolean hasEmail = false;
    public static String email = "default";
    public static String study = "default";
    public static String comment = "default";

    public static void reset() {
        userID = -1;
        firstName = "default";
        lastName = "default";
        hasEmail = true;
        email = "default";
        study = "default";
    }
}
