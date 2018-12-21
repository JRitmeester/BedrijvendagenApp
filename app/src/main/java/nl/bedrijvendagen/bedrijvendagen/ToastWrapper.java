package nl.bedrijvendagen.bedrijvendagen;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastWrapper {

    private static Toast toast;

    public static void createToast(final String message, final int duration, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(activity, message, duration);
                toast.show();
            }
        });
    }
}
