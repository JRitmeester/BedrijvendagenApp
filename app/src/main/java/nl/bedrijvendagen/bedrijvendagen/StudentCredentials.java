package nl.bedrijvendagen.bedrijvendagen;

public class StudentCredentials {
    public static int userID = -1;
    public static String firstName = "";
    public static String lastName = "";
    public static boolean hasEmail = true;
    public static String email = "";
    public static String study = "";
    public static String comment = "";

    public static void reset() {
        userID = -1;
        firstName = "";
        lastName = "";
        hasEmail = true;
        email = "";
        study = "";
    }
}
