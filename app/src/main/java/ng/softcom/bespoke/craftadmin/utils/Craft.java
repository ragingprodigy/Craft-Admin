package ng.softcom.bespoke.craftadmin.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Date;

import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.activities.CALoginActivity;
import ng.softcom.bespoke.craftadmin.activities.CASecureActivity;
import ng.softcom.bespoke.craftadmin.models.CAUser;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by oladapo on 27/04/16
 */
public class Craft {

    private static String TAG = "Craft";

    private SystemData sharedPrefs;
    private Context callingActivity;

    private CAUser user;

    private PrettyTime pt;

    public Craft(Context ctx) {
        sharedPrefs = new SystemData(ctx);
        callingActivity = ctx;

        pt = new PrettyTime();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public CAUser getCurrentUser() {
        return getCurrentUser(true);
    }

    public CAUser getCurrentUser(boolean cached) {
        if (user == null || !cached) {
            Gson gson = new Gson();
            user = gson.fromJson(getSharedPrefs().getString(callingActivity.getString(R.string.user_data_key)), CAUser.class);
        }

        return user;
    }

    /**
     * Confirm that the current user is logged in
     */
    public void checkLogin() {

        if (sharedPrefs.getString(callingActivity.getResources().getString(R.string.logged_in_as_key)).isEmpty()) {
            goToLogin();
        }

    }

    /**
     * Display relative time
     *
     * @param timestamp long
     * @return String
     */
    public String ago(long timestamp) {
        return pt.format(new Date(timestamp));
    }

    /**
     * Return a Handle to the Shared Prefs object
     * @return SystemData
     */
    public SystemData getSharedPrefs() {
        return sharedPrefs;
    }

    /**
     * Logout the Current User
     */
    public void doLogout() {
        sharedPrefs.writeString("", callingActivity.getResources().getString(R.string.logged_in_as_key));
        sharedPrefs.writeString("", callingActivity.getString(R.string.user_data_key));
        goToLogin();
    }

    /**
     * Launch CALogin Activity
     */
    private void goToLogin() {

        Intent i = new Intent(callingActivity, CALoginActivity.class);
        callingActivity.startActivity(i);

        if (callingActivity instanceof CASecureActivity) {
            ((CASecureActivity) callingActivity).finish();
        }

    }

    /**
     * Extract the first letters of the first three words of any text
     *
     * @param parts String[]
     * @param subTextBuilder StringBuilder
     */
    public void letterViewData(String[] parts, StringBuilder subTextBuilder) {
        int i = 0;
        int max = subTextBuilder.capacity();
        int ceiling = parts.length > max ? max : parts.length;

        while (i < ceiling) {
            if (parts[i].length() > 0) {
                subTextBuilder.append(parts[i].charAt(0));
            }
            i++;
        }
    }

    /**
     * Http Interceptor that sets Appropriate Request Headers
     *
     * @return OkHttpClient
     */
    public OkHttpClient getAPIClient() {
        final String token = getSharedPrefs().getString(callingActivity.getString(R.string.user_request_token));

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        };

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        builder.interceptors().add(loggingInterceptor);

        return builder.build();
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public Drawable imageFromUrl(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }
}
