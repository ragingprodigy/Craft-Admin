package ng.softcom.bespoke.craftadmin.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.utils.CAAlerts;

/**
 * Created by oladapo on 20/05/2016.
 * as part of ng.softcom.bespoke.craftadmin.dialogs in Craft Admin
 */
public class CertificateNameDialog extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText mEditText;

    public interface CNDListener {
        void onFinishEditDialog(String discussionTitle);
        void clearSelectedBitmap();
    }

    public CertificateNameDialog() { }

    public static CertificateNameDialog newInstance() {
        return new CertificateNameDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_work_pic_title, container);
        mEditText = (EditText) view.findViewById(R.id.input_status);

        // Show soft keyboard automatically
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mEditText.setOnEditorActionListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = getDialog().getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            String title = mEditText.getText().toString();
            if (title.isEmpty()) {
                CAAlerts.showShortToast(getActivity(), "Please enter a valid title!");
            } else {
                CNDListener activity = (CNDListener) getActivity();
                activity.onFinishEditDialog(title);
                this.dismiss();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        CNDListener activity = (CNDListener) getActivity();
        activity.clearSelectedBitmap();

    }
}
