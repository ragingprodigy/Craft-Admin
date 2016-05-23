package ng.softcom.bespoke.craftadmin.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.models.CAUser;
import ng.softcom.bespoke.craftadmin.utils.CAAlerts;
import ng.softcom.bespoke.craftadmin.utils.Craft;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.activities in Craft Admin
 */
public class CASecureActivity extends AppCompatActivity {

    /**
     * Util Object
     */
    public Craft craft;
    public CAUser user;
    public SmoothProgressBar progressBar;

    public static String TAG = CASecureActivity.class.getSimpleName();

    protected void showLongToast(String message) {
        CAAlerts.showLongToast(this, message);
    }

    protected void showShortToast(String message) {
        CAAlerts.showShortToast(this, message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        craft = new Craft(this);
        craft.checkLogin();

        user = craft.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyBoard();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideKeyBoard();
    }

    protected void hideProgressBar() {
        if (progressBar == null)
            createProgressBar();

        progressBar.setVisibility(View.GONE);
    }

    protected void showProgressBar() {
        if (progressBar == null)
            createProgressBar();

        progressBar.setVisibility(View.VISIBLE);
    }

    protected void createProgressBar() {
        progressBar = new SmoothProgressBar(this);
    }

    /**
     * Hide Soft Keyboard
     */
    public void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus()!=null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.new_record:
                startActivity(new Intent(this, CANewArtisan.class));
                return true;
            case R.id.action_logout:
                doLogout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void doLogout() {
        //Ask the user if they want to quit
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.really_quit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        craft.doLogout();
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
