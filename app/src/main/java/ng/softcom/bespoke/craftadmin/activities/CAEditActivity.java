package ng.softcom.bespoke.craftadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.api.interfaces.CAArtisanInterface;
import ng.softcom.bespoke.craftadmin.models.CAArtisan;
import ng.softcom.bespoke.craftadmin.models.CABank;
import ng.softcom.bespoke.craftadmin.models.CABankDetail;
import ng.softcom.bespoke.craftadmin.models.CAGuarantor;
import ng.softcom.bespoke.craftadmin.models.CASpecialty;
import ng.softcom.bespoke.craftadmin.thirdparty.nicespinner.ESNiceSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CAEditActivity extends CANewArtisan {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_artisan);

        Intent i = getIntent();
        Gson gson = new Gson();
        artisan = gson.fromJson(i.getStringExtra("artisan"), CAArtisan.class);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setDisplayHomeAsUpEnabled(true);
            a.setTitle(artisan.getFullName());
        }

        bindViews();
        createEventHandlers();
        setDataValues();
    }

    protected void setDataValues() {

        txtBusinessName.setText(artisan.getBusinessName());
        txtSurname.setText(artisan.getSurname());
        txtMiddleName.setText(artisan.getMiddleName());
        txtFirstName.setText(artisan.getFirstName());

        txtAddress.setText(artisan.getAddress());
        txtPhone.setText(artisan.getPhone());

        txtGuarantorAddress.setText(artisan.getGuarantors().get(0).getAddress());
        txtGuarantorName.setText(artisan.getGuarantors().get(0).getName());
        txtGuarantorPhone.setText(artisan.getGuarantors().get(0).getPhone());

        Log.d(TAG, artisan.toString());

        if (artisan.getBankDetails() != null) {
            int bankSize = banks.size();
            for (int i = 0; i < bankSize; i++) {
                if (banks.get(i).getId().equals(artisan.getBankDetails().getBank().getId())) {
                    bankSpinner.setSelectedIndex(i);
                }
            }
        }

        int specialtySize = specialties.size();
        for (int j = 0; j < specialtySize; j++) {
            if (specialties.get(j).getId().equals(artisan.getSpecialty().getId())) {
                specialtySpinner.setSelectedIndex(j);
            }
        }
    }

    /**
     * Bind View Elements
     */
    protected void bindViews() {
        progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);

        backBtn = (Button) findViewById(R.id.back);

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
    }

    /**
     * Touch event handlers
     */
    protected void createEventHandlers() {

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
                finish();
            }
        });
    }

    protected void validateAndSubmit() {
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
                        postToServer();
                    }
                } else {
                    if (accountName.isEmpty() && accountNumber.isEmpty()) {
                        // Proceed Without Bank details
                        postToServer();
                    } else {
                        if (accountName.isEmpty()) showShortToast("Please provide a valid Account Name");
                        if (accountNumber.isEmpty()) showShortToast("Please provide a valid NUBAN Account Number");

                        txtAccountName.requestFocus();
                        submitBtn.setEnabled(true);
                    }
                }
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

    /**
     * Update Artisan Data
     */
    protected void postToServer() {
        String BASE_URL = getString(R.string.web_service_url);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(craft.getAPIClient())
                .build();

        final CAArtisanInterface artisanService = retrofit.create(CAArtisanInterface.class);

        showProgressBar();

        artisanService.updateArtisan(artisan.getId(), artisan).enqueue(new Callback<CAArtisan>() {
            @Override
            public void onResponse(Call<CAArtisan> call, final Response<CAArtisan> response) {
                hideProgressBar();
                showShortToast("Data Updated!");

                if (response.code() == 200) {
                    CAArtisan newArtisan = response.body();
                    Intent i = new Intent(CAEditActivity.this, CAArtisanDetail.class);
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

}
