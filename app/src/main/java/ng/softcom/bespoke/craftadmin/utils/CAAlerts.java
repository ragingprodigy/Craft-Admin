package ng.softcom.bespoke.craftadmin.utils;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * Created by oladapo on 27/4/16.
 * as part of CraftAdmin
 *
 */
public class CAAlerts {

    public static void showLongToast(Context ctx, String message) {

        showToast(ctx, message, Toast.LENGTH_LONG);

    }

    public static void showShortToast(Context ctx, String message) {

        showToast(ctx, message, Toast.LENGTH_SHORT);

    }

    private static void showToast(Context ctx, String message, Integer length) {

        if (ctx!=null) {
            Toast.makeText(ctx, message, length).show();
        }

    }
}
