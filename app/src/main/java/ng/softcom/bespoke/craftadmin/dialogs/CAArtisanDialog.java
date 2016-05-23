package ng.softcom.bespoke.craftadmin.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.models.CAArtisan;
import ng.softcom.bespoke.craftadmin.utils.Craft;

/**
 * Created by oladapo on 11/05/2016.
 * as part of ng.softcom.bespoke.craftadmin.dialogs in Craft Admin
 */
public class CAArtisanDialog extends DialogFragment {

    private Craft craft;
    private CAArtisan artisan;

    private TextView name, company, phone, address, specialty, email;

    public CAArtisanDialog() { }

    public static CAArtisanDialog newInstance(CAArtisan artisanData) {
        CAArtisanDialog pd = new CAArtisanDialog();
        Bundle args = new Bundle();
        args.putString("artisanData", artisanData.toString());
        pd.setArguments(args);

        return pd;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey("artisanData")) dismiss();

        String artisanData = getArguments().getString("artisanData");

        Gson gson = new Gson();
        artisan = gson.fromJson(artisanData, CAArtisan.class);
        craft = new Craft(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artisan_card, container);

        name = (TextView) view.findViewById(R.id.lbl_name);
        company = (TextView) view.findViewById(R.id.lbl_company);

        phone = (TextView) view.findViewById(R.id.lbl_phone);
        address = (TextView) view.findViewById(R.id.lbl_address);
        email = (TextView) view.findViewById(R.id.lbl_email);
        specialty = (TextView) view.findViewById(R.id.lbl_specialty);

        email.setVisibility(View.GONE);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Show soft keyboard automatically
        if (getDialog()!=null) getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name.setText(artisan.getFullName());
        company.setText(artisan.getBusinessName());
        phone.setText(artisan.getPhone());
        specialty.setText(artisan.getSpecialty().getName());
        address.setText(artisan.getAddress());
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog()!=null) {
            //Grab the window of the dialog, and change the width
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = getDialog().getWindow();
            lp.copyFrom(window.getAttributes());

            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }

}
