package nl.bedrijvendagen.bedrijvendagen;


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
}
