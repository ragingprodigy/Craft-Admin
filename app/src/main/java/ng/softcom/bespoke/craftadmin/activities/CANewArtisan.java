package ng.softcom.bespoke.craftadmin.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
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

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import ng.softcom.bespoke.craftadmin.R;
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

public class CANewArtisan extends CASecureActivity implements android.location.LocationListener {

    protected static final int PICK_IMAGE_ID = 2343;
    protected static final int PICK_CERTIFICATE_ID = 2344;

    protected Button btnIdPicker, btnCertificatePicker, submitBtn, backBtn;
    protected ImageView regulatoryId, certificate;

    protected EditText txtBusinessName, txtFirstName, txtMiddleName, txtSurname, txtAddress, txtPhone, txtGuarantorName, txtGuarantorPhone, txtGuarantorAddress, txtAccountNumber, txtAccountName, txtEmail;

    protected CAArtisan artisan;
    protected Cloudinary cloudinary;

    protected List<CASpecialty> specialties;
    protected List<CABank> banks;

    protected ESNiceSpinner bankSpinner, specialtySpinner;

    protected ArrayList<String> banksAdapter = new ArrayList<>();
    protected ArrayList<String> specialtiesAdapter = new ArrayList<>();

    protected Bitmap certificateBitmap, identityBitmap;

    protected TextView location;
    protected Location mLastLocation;

    protected void makeLocationRequest() {
        LocationManager mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_artisan);
        
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setDisplayHomeAsUpEnabled(true);
            a.setTitle(R.string.new_artisan);
        }

        cloudinary = new Cloudinary(this);

        artisan = new CAArtisan();
        banks = new ArrayList<>();
        specialties = new ArrayList<>();

        Gson gson = new Gson();
        String bankString = craft.getSharedPrefs().getString(getString(R.string.banks_cache_key));
        String specialtyString = craft.getSharedPrefs().getString(getString(R.string.specialties_cache_key));

        banks = gson.fromJson(bankString, new TypeToken<List<CABank>>(){}.getType());
        specialties = gson.fromJson(specialtyString, new TypeToken<List<CASpecialty>>(){}.getType());

        if ( banks!=null ) {
            for (int i = 0; i < banks.size(); i++)
                banksAdapter.add(banks.get(i).getName());
        }

        if ( specialties!=null ) {
            for (int i = 0; i < specialties.size(); i++)
                specialtiesAdapter.add(specialties.get(i).getName());
        }

        bindViews();
        createEventHandlers();
        makeLocationRequest();
    }

    /**
     * Bind View Elements
     */
    protected void bindViews() {
        progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);

        backBtn = (Button) findViewById(R.id.back);
        location = (TextView) findViewById(R.id.location);

        submitBtn = (Button) findViewById(R.id.submit);

        txtBusinessName = (EditText) findViewById(R.id.input_company);
        txtFirstName = (EditText) findViewById(R.id.input_firstname);
        txtMiddleName = (EditText) findViewById(R.id.input_middlename);
        txtSurname = (EditText) findViewById(R.id.input_surname);
        txtPhone = (EditText) findViewById(R.id.input_phone);
        txtAddress = (EditText) findViewById(R.id.input_address);
        txtEmail = (EditText) findViewById(R.id.input_email);

        txtGuarantorName = (EditText) findViewById(R.id.input_guarantor_name);
        txtGuarantorAddress = (EditText) findViewById(R.id.input_guarantor_address);
        txtGuarantorPhone = (EditText) findViewById(R.id.input_guarantor_phone);

        txtAccountName = (EditText) findViewById(R.id.input_account_name);
        txtAccountNumber = (EditText) findViewById(R.id.input_nuban_number);

        bankSpinner = (ESNiceSpinner) findViewById(R.id.bankSpinner);
        if (banks!=null) bankSpinner.attachDataSource(banksAdapter);

        specialtySpinner = (ESNiceSpinner) findViewById(R.id.specialtySpinner);
        if (specialties!=null) specialtySpinner.attachDataSource(specialtiesAdapter);

        regulatoryId = (ImageView) findViewById(R.id.regulatoryId);
        certificate = (ImageView) findViewById(R.id.certificate);

        btnIdPicker = (Button) findViewById(R.id.btnIdPicker);
        btnCertificatePicker = (Button) findViewById(R.id.btnCertificatePicker);
    }

    /**
     * Reset the Form
     */
    protected void resetForm() {
        txtBusinessName.setText("");
        txtSurname.setText("");
        txtFirstName.setText("");
        txtMiddleName.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        txtEmail.setText("");

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
    protected void validateAndSubmit() {
        if (artisan.getLocation() == null || artisan.getLocation().size() == 0 || artisan.getLocation().get(0) == null) {
            showShortToast("Cannot add Artisan data without location info!");
            makeLocationRequest();
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
            artisan.setEmail(txtEmail.getText().toString());

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
                                txtAccountNumber.setError("Account Number should be 10 characters long");
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

    /**
     * Touch event handlers
     */
    protected void createEventHandlers() {
        btnIdPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CAImagePicker.getPickImageIntent(CANewArtisan.this, getString(R.string.regulatory_id_prompt));
                startActivityForResult(intent, PICK_IMAGE_ID);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate the Form and submit
                validateAndSubmit();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset the Form and Switch Screens
                resetForm();
                finish();
            }
        });

        btnCertificatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CAImagePicker.getPickImageIntent(CANewArtisan.this, getString(R.string.certificate_prompt));
                startActivityForResult(intent, PICK_CERTIFICATE_ID);
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (mLastLocation != null) {
                    final String text = "Current Location: " + craft.round(mLastLocation.getLongitude(), 4) + ", " + craft.round(mLastLocation.getLatitude(), 4);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            location.setText(text);
                            artisan.setLocation(new ArrayList<>(Arrays.asList(mLastLocation.getLongitude(), mLastLocation.getLatitude())));
                        }
                    });
                }
            }
        }, 0, 3000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                identityBitmap = CAImagePicker.getImageFromResult(CANewArtisan.this, resultCode, data);
                regulatoryId.setImageBitmap(identityBitmap);
                break;
            case PICK_CERTIFICATE_ID:
                certificateBitmap = CAImagePicker.getImageFromResult(CANewArtisan.this, resultCode, data);
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
    protected void uploadImageBitmaps() {
        Location mLocation = mLastLocation;

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

                showProgressBar();

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

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Proceed to Uploading to Server
                                        hideProgressBar();
                                        postToServer();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
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

    /**
     * Submit New Artisan Data to Server
     */
    protected void postToServer() {
        String BASE_URL = getString(R.string.web_service_url);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(craft.getAPIClient())
                .build();

        final CAArtisanInterface artisanService = retrofit.create(CAArtisanInterface.class);

        showProgressBar();

        artisanService.createArtisan(artisan).enqueue(new Callback<CAArtisan>() {
            @Override
            public void onResponse(Call<CAArtisan> call, final Response<CAArtisan> response) {
                hideProgressBar();
                showShortToast("Data Saved!");

                if (response.code() == 201) {
                    CAArtisan newArtisan = response.body();
                    Intent i = new Intent(CANewArtisan.this, CAArtisanDetail.class);
                    i.putExtra(CAArtisanDetail.DATA_KEY, newArtisan.toString());

                    startActivity(i);
                    finish();
                } else {
                    showShortToast(response.message());
                    submitBtn.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<CAArtisan> call, Throwable t) {
                hideProgressBar();
                submitBtn.setEnabled(true);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
