package ng.softcom.bespoke.craftadmin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import ng.softcom.bespoke.craftadmin.models.CAUser;
import ng.softcom.bespoke.craftadmin.utils.CAAlerts;
import ng.softcom.bespoke.craftadmin.utils.Craft;

/**
 * Created by oladapo on 27/04/2016.
 * as part of ng.softcom.bespoke.craftadmin.fragments in Craft Admin
 */
public class CAFragment extends Fragment {

    public Craft craft;
    public CAUser user;

    public static String TAG = CAFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        craft = new Craft(getActivity());
        user = craft.getCurrentUser();
    }

    protected void showLongToast(String message) {
        CAAlerts.showLongToast(getActivity(), message);
    }

    protected void showShortToast(String message) {
        Log.d(TAG, "TOAST: " + message);

        CAAlerts.showShortToast(getActivity(), message);
    }
}
