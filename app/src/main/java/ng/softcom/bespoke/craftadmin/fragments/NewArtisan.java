package ng.softcom.bespoke.craftadmin.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.activities.CAArtisanDetail;
import ng.softcom.bespoke.craftadmin.api.interfaces.CAArtisanInterface;
import ng.softcom.bespoke.craftadmin.models.CAArtisan;
import ng.softcom.bespoke.craftadmin.models.CABank;
import ng.softcom.bespoke.craftadmin.models.CABankDetail;
import ng.softcom.bespoke.craftadmin.models.CACertificate;
import ng.softcom.bespoke.craftadmin.models.CAGuarantor;
import ng.softcom.bespoke.craftadmin.models.CASpecialty;
import ng.softcom.bespoke.craftadmin.thirdparty.nicespinner.ESNiceSpinner;
import ng.softcom.bespoke.craftadmin.utils.CAImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewArtisan.NAListener} interface
 * to handle interaction events.
 * Use the {@link NewArtisan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewArtisan extends CAFragment {

    private static final int PICK_IMAGE_ID = 2343;
    private static final int PICK_CERTIFICATE_ID = 2344;

    private NAListener mListener;
    private Button btnIdPicker, btnCertificatePicker, submitBtn;
    private ImageView regulatoryId, certificate;

    private EditText txtBusinessName, txtFirstName, txtMiddleName, txtSurname, txtAddress, txtPhone, txtGuarantorName, txtGuarantorPhone, txtGuarantorAddress, txtAccountNumber, txtAccountName;

    private CAArtisan artisan;

    private Cloudinary cloudinary;

    private List<CASpecialty> specialties;
    private List<CABank> banks;

    private ESNiceSpinner bankSpinner, specialtySpinner;

    private ArrayList<String> banksAdapter = new ArrayList<>();
    private ArrayList<String> specialtiesAdapter = new ArrayList<>();

    private Bitmap certificateBitmap, identityBitmap;

    private TextView location;

    public NewArtisan() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewArtisan.
     */
    public static NewArtisan newInstance() {
        return new NewArtisan();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cloudinary = new Cloudinary(getContext());

        artisan = new CAArtisan();
        banks = new ArrayList<>();
        specialties = new ArrayList<>();

        Gson gson = new Gson();
        String bankString = craft.getSharedPrefs().getString(getString(R.string.banks_cache_key));
        String specialtyString = craft.getSharedPrefs().getString(getString(R.string.specialties_cache_key));

        banks = gson.fromJson(bankString, new TypeToken<List<CABank>>(){}.getType());
        specialties = gson.fromJson(specialtyString, new TypeToken<List<CASpecialty>>(){}.getType());

        if (banks!=null) {
            for (int i = 0; i < banks.size(); i++)
                banksAdapter.add(banks.get(i).getName());
        }

        if (specialties!=null) {
            for (int i = 0; i < specialties.size(); i++)
                specialtiesAdapter.add(specialties.get(i).getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_artisan, container, false);

        v.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset the Form and Switch Screens
                resetForm();
                if (mListener != null) mListener.showListFragment();
            }
        });

        location = (TextView) v.findViewById(R.id.location);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (mListener!=null) {
                    final Location mLocation = mListener.getCurrentLocation();
                    if (mLocation!=null) {
                        final String text = "Current Location: " + craft.round(mLocation.getLongitude(), 4) + ", " + craft.round(mLocation.getLatitude(), 4);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                location.setText(text);
                                artisan.setLocation(new ArrayList<>(Arrays.asList(mLocation.getLongitude(), mLocation.getLatitude())));
                            }
                        });
                    }
                }
            }
        }, 0, 3000);

        submitBtn = (Button) v.findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate the Form and submit
                validateAndSubmit();
            }
        });

        txtBusinessName = (EditText) v.findViewById(R.id.input_company);
        txtFirstName = (EditText) v.findViewById(R.id.input_firstname);
        txtMiddleName = (EditText) v.findViewById(R.id.input_middlename);
        txtSurname = (EditText) v.findViewById(R.id.input_surname);
        txtPhone = (EditText) v.findViewById(R.id.input_phone);
        txtAddress = (EditText) v.findViewById(R.id.input_address);

        txtGuarantorName = (EditText) v.findViewById(R.id.input_guarantor_name);
        txtGuarantorAddress = (EditText) v.findViewById(R.id.input_guarantor_address);
        txtGuarantorPhone = (EditText) v.findViewById(R.id.input_guarantor_phone);

        txtAccountName = (EditText) v.findViewById(R.id.input_account_name);
        txtAccountNumber = (EditText) v.findViewById(R.id.input_nuban_number);

        bankSpinner = (ESNiceSpinner) v.findViewById(R.id.bankSpinner);
        if (banks!=null) bankSpinner.attachDataSource(banksAdapter);

        specialtySpinner = (ESNiceSpinner) v.findViewById(R.id.specialtySpinner);
        if (specialties!=null) specialtySpinner.attachDataSource(specialtiesAdapter);

        regulatoryId = (ImageView) v.findViewById(R.id.regulatoryId);
        certificate = (ImageView) v.findViewById(R.id.certificate);

        btnIdPicker = (Button) v.findViewById(R.id.btnIdPicker);
        btnCertificatePicker = (Button) v.findViewById(R.id.btnCertificatePicker);

        return v;
    }
    
    private void resetForm() {
        txtBusinessName.setText("");
        txtSurname.setText("");
        txtFirstName.setText("");
        txtMiddleName.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        
        bankSpinner.setSelectedIndex(0);
        specialtySpinner.setSelectedIndex(0);
        
        identityBitmap = certificateBitmap = null;

        regulatoryId.setImageBitmap(null);
        certificate.setImageBitmap(null);

        txtGuarantorAddress.setText("");
        txtGuarantorName.setText("");
        txtGuarantorPhone.setText("");

        txtAccountName.setText("");
        txtAccountNumber.setText("");

        artisan = new CAArtisan();
    }

    // Validate the Form and prepare for submission
    private void validateAndSubmit() {
        if (artisan.getLocation() == null || artisan.getLocation().size() == 0 || artisan.getLocation().get(0) == null) {
            showShortToast("Cannot add Artisan data without location info!");
            if (mListener!=null) mListener.initializeLocationRequest();
        } else {
            submitBtn.setEnabled(false);

            String accountName = txtAccountName.getText().toString();
            String accountNumber = txtAccountNumber.getText().toString();

            String guarantorName = txtGuarantorName.getText().toString();
            String guarantorPhone = txtGuarantorPhone.getText().toString();
            String guarantorAddress = txtGuarantorAddress.getText().toString();

            artisan.setBusinessName(txtBusinessName.getText().toString());
            artisan.setFirstName(txtFirstName.getText().toString());
            artisan.setMiddleName(txtMiddleName.getText().toString());
            artisan.setSurname(txtSurname.getText().toString());
            artisan.setPhone(txtPhone.getText().toString());
            artisan.setAddress(txtAddress.getText().toString());

            artisan.setAddress(txtAddress.getText().toString());

            String specialty = specialties.get(specialtySpinner.getSelectedIndex()).getId();
            CASpecialty s = new CASpecialty();
            s.setId(specialty);

            artisan.setSpecialty(s);

            HashMap<String, String> errors = new HashMap<>();

            if (artisan.getFirstName().isEmpty()) { errors.put("firstName", "Artisan's first name is required"); }
            if (artisan.getSurname().isEmpty()) { errors.put("surname", "Surname is required"); }
            if (artisan.getPhone().isEmpty()) { errors.put("phone", "Artisan's Phone Number is required"); }
            if (artisan.getAddress().isEmpty()) { errors.put("address", "Artisan's Address is required"); }

            if (errors.isEmpty()) {

                // Set and Validate other data
                if (identityBitmap != null) {

                    // Guarantor Validation
                    if (guarantorAddress.isEmpty() || guarantorName.isEmpty() || guarantorPhone.isEmpty()) {
                        if (guarantorAddress.isEmpty()) txtGuarantorAddress.setError("Please provide Guarantor's Contact Address");
                        if (guarantorName.isEmpty()) txtGuarantorName.setError("Please provide Guarantor's Name");
                        if (guarantorPhone.isEmpty()) txtGuarantorPhone.setError("Please provide Guarantor's Phone Number");

                        txtGuarantorName.requestFocus();
                        submitBtn.setEnabled(true);
                    } else {
                        // Set Guarantor
                        CAGuarantor guarantor = new CAGuarantor(guarantorName, guarantorPhone, guarantorAddress);
                        artisan.setGuarantors(new ArrayList<>(Arrays.asList(guarantor)));

                        if (!accountName.isEmpty() && !accountNumber.isEmpty()) {
                            if (accountNumber.length() != 10) {
                                txtAccountNumber.setError("NUBAM Account Number should be 10 characters long");
                                submitBtn.setEnabled(true);
                            } else {
                                // Account Information is provided
                                String bank = banks.get(bankSpinner.getSelectedIndex()).getId();
                                CABankDetail bankDetail = new CABankDetail();
                                bankDetail.setAccountName(accountName);
                                bankDetail.setNubanNumber(accountNumber);

                                CABank b = new CABank();
                                b.setId(bank);

                                bankDetail.setBank(b);

                                artisan.setBankDetails(bankDetail);
                                uploadImageBitmaps();
                            }
                        } else {
                            if (accountName.isEmpty() && accountNumber.isEmpty()) {
                                // Proceed Without Bank details
                                uploadImageBitmaps();
                            } else {
                                if (accountName.isEmpty()) showShortToast("Please provide a valid Account Name");
                                if (accountNumber.isEmpty()) showShortToast("Please provide a valid NUBAN Account Number");

                                txtAccountName.requestFocus();
                                submitBtn.setEnabled(true);
                            }
                        }
                    }
                } else {
//                    if (certificateBitmap == null) showShortToast("Please select a Certificate!");
                    if (identityBitmap == null) showShortToast("Please select a Valid Photo ID!");

                    submitBtn.setEnabled(true);
                }
            } else {
                if (errors.containsKey("surname")) {
                    txtSurname.setError(errors.get("surname"));
                }

                if (errors.containsKey("firstName")) {
                    txtFirstName.setError(errors.get("firstName"));
                }

                if (errors.containsKey("phone")) {
                    txtPhone.setError(errors.get("phone"));
                }

                if (errors.containsKey("address")) {
                    txtAddress.setError(errors.get("address"));
                }

                txtSurname.requestFocus();
                submitBtn.setEnabled(true);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnIdPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CAImagePicker.getPickImageIntent(getActivity(), getString(R.string.regulatory_id_prompt));
                startActivityForResult(intent, PICK_IMAGE_ID);
            }
        });

        btnCertificatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CAImagePicker.getPickImageIntent(getActivity(), getString(R.string.certificate_prompt));
                startActivityForResult(intent, PICK_CERTIFICATE_ID);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                identityBitmap = CAImagePicker.getImageFromResult(getActivity(), resultCode, data);
                regulatoryId.setImageBitmap(identityBitmap);
                break;
            case PICK_CERTIFICATE_ID:
                certificateBitmap = CAImagePicker.getImageFromResult(getActivity(), resultCode, data);
                certificate.setImageBitmap(certificateBitmap);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Upload Certificate and Valid ID
     */
    private void uploadImageBitmaps() {
        if (mListener!=null) {
            Location mLocation = mListener.getCurrentLocation();

            if (mLocation!=null) {

                artisan.setLocation(new ArrayList<>(Arrays.asList(mLocation.getLongitude(), mLocation.getLatitude())));

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    certificateBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    final ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                    ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                    identityBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos1);
                    byte[] bitmapdata1 = bos1.toByteArray();
                    final ByteArrayInputStream bs1 = new ByteArrayInputStream(bitmapdata1);

                    mListener.showProgressBar();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final JSONObject certificateResponse = cloudinary.uploader().upload(bs, Cloudinary.emptyMap());
                                final JSONObject identityResponse = cloudinary.uploader().upload(bs1, Cloudinary.emptyMap());

                                if (certificateResponse.has("secure_url")) {
                                    CACertificate certificate = new CACertificate("Practicing Certificate", certificateResponse.optString("secure_url"));
                                    artisan.setCertifications(new ArrayList<>(Arrays.asList(certificate)));
                                }

                                if (identityResponse.has("secure_url")) {
                                    artisan.setIdentification(identityResponse.optString("secure_url"));

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Proceed to Uploading to Server
                                            mListener.hideProgressBar();
                                            postToServer();
                                        }
                                    });
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showShortToast("Error encountered while uploading attachments. Please try again later.");
                                            submitBtn.setEnabled(true);
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                submitBtn.setEnabled(true);
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showShortToast("Cannot proceed without having current location.");
                submitBtn.setEnabled(true);
            }
        }
    }

    /**
     * Submit New Artisan Data to Server
     */
    private void postToServer() {
        String BASE_URL = getString(R.string.web_service_url);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(craft.getAPIClient())
                .build();

        final CAArtisanInterface artisanService = retrofit.create(CAArtisanInterface.class);

        mListener.showProgressBar();

        artisanService.createArtisan(artisan).enqueue(new Callback<CAArtisan>() {
            @Override
            public void onResponse(Call<CAArtisan> call, final Response<CAArtisan> response) {
                mListener.hideProgressBar();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showShortToast("Data save action response received! " + response.code());
                        if (response.code() < 300) {
                            CAArtisan newArtisan = response.body();
                            Intent i = new Intent(getActivity(), CAArtisanDetail.class);
                            i.putExtra(CAArtisanDetail.DATA_KEY, newArtisan.toString());

                            startActivity(i);

                            mListener.showListFragment();
                        } else {
                            showShortToast(response.message());
                            submitBtn.setEnabled(true);

                            Log.d(TAG, "API Response: " + response.message());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<CAArtisan> call, Throwable t) {
                mListener.hideProgressBar();
                submitBtn.setEnabled(true);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NAListener) {
            mListener = (NAListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NAListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface NAListener {
        void showListFragment();
        void showProgressBar();
        void hideProgressBar();

        void initializeLocationRequest();

        Location getCurrentLocation();
    }
}
