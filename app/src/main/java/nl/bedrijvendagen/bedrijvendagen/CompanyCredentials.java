package nl.bedrijvendagen.bedrijvendagen;

/**
 * Created by Jeroen on 31-12-2017.
 */

public class CompanyCredentials {
    public static boolean auth = false;
    public static int company_id = -1;
    public static String token = "";
    public static String company = "";
    public static String email = "";
    public static String password = "";

    public static void reset() {
        auth = false;
        company_id = -1;
        token = "default";
        company = "default";
        email = "default";
        password = "default";
    }
}
