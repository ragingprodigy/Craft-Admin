package ng.softcom.bespoke.craftadmin.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.api.interfaces.CAArtisanInterface;
import ng.softcom.bespoke.craftadmin.dialogs.CertificateNameDialog;
import ng.softcom.bespoke.craftadmin.dialogs.CertificateNameDialog.CNDListener;
import ng.softcom.bespoke.craftadmin.models.CAArtisan;
import ng.softcom.bespoke.craftadmin.models.CAGuarantor;
import ng.softcom.bespoke.craftadmin.models.CAWorkPicture;
import ng.softcom.bespoke.craftadmin.utils.CAImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CAArtisanDetail extends CASecureActivity implements CNDListener {

    public static final String DATA_KEY = "MY_DATA_KEY";
    private static final int PICK_IMAGE_ID = 2243;
    private static final int PICK_PROFILE_ID = 12543;

    private Bitmap workPicBitmap, profileBitmap;

    private LinearLayout picsContainer;
    private CAArtisan artisan;

    private TextView name, company, phone, address, specialty, email, bank, account, guarantor, guarantor_address, guarantor_phone;
    private ImageView profile;
    private Button newPicBtn, newProfileBtn, editButton;
    private Cloudinary cloudinary;

    private CAArtisanInterface artisanService;
    private boolean promptForTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_detail);

        ActionBar a = getSupportActionBar();
        if (a!=null) {
            a.setDisplayHomeAsUpEnabled(true);
            a.setTitle(R.string.artisan_details);
        }

        String BASE_URL = getString(R.string.web_service_url);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(craft.getAPIClient())
                .build();

        artisanService = retrofit.create(CAArtisanInterface.class);
        cloudinary = new Cloudinary(this);

        Intent i = getIntent();
        if (i.hasExtra(DATA_KEY)) {
            Gson gson = new Gson();
            artisan = gson.fromJson(i.getStringExtra(DATA_KEY), CAArtisan.class);

            if (artisan == null) {
                showShortToast("Error while re-instantiating Artisan object");
                finish();
            }
        } else {
            showShortToast("Data not attached!");
            finish();
        }

        bindViews();
        drawTextData();
        drawWorkPics();

        drawProfilePic();

        createEventHandlers();
    }

    private void drawProfilePic() {
        profile.setImageBitmap(null);
        Picasso.with(this).load(artisan.getProfileUrl()).into(profile);
    }

    /**
     * Bind text to views
     */
    private void drawTextData() {
        name.setText(artisan.getFullName());
        company.setText(artisan.getBusinessName());
        phone.setText(artisan.getPhone());
        specialty.setText(artisan.getSpecialty().getName());
        address.setText(artisan.getAddress());

        email.setText(artisan.getEmail());

        if (artisan.getBankDetails()!=null) {
            bank.setText(artisan.getBankDetails().getBank().getName());
            account.setText(artisan.getBankDetails().getNubanNumber());
        }

        CAGuarantor g = artisan.getGuarantors().get(0);
        if (g != null) {
            guarantor.setText(g.getName());
            guarantor_address.setText(g.getAddress());
            guarantor_phone.setText(g.getPhone());
        }
    }

    /**
     * View bindings
     */
    private void bindViews() {
        progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);

        picsContainer = (LinearLayout) findViewById(R.id.picsContainer);
        name = (TextView) findViewById(R.id.lbl_name);
        company = (TextView) findViewById(R.id.lbl_company);

        phone = (TextView) findViewById(R.id.lbl_phone);
        address = (TextView) findViewById(R.id.lbl_address);
        email = (TextView) findViewById(R.id.lbl_email);
        specialty = (TextView) findViewById(R.id.lbl_specialty);

        bank = (TextView) findViewById(R.id.lbl_bank);
        account = (TextView) findViewById(R.id.lbl_account);

        guarantor = (TextView) findViewById(R.id.lbl_guarantor);
        guarantor_address = (TextView) findViewById(R.id.lbl_guarantor_address);
        guarantor_phone = (TextView) findViewById(R.id.lbl_guarantor_phone);

        profile = (ImageView) findViewById(R.id.profile);

        newPicBtn = (Button) findViewById(R.id.btn_new_work_pick);
        newProfileBtn = (Button) findViewById(R.id.btn_new_profile);
        editButton = (Button) findViewById(R.id.btn_edit);
    }

    /**
     * Touch event handlers
     */
    private void createEventHandlers() {
        newPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (artisan.getWorkPictures().size() < 5) {
                    Intent intent = CAImagePicker.getPickImageIntent(CAArtisanDetail.this, getString(R.string.work_pic_prompt));
                    startActivityForResult(intent, PICK_IMAGE_ID);
                } else {
                    showLongToast("You cannot have more than 5 work pictures. Please delete one in order to add another");
                }
            }
        });

        newProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CAImagePicker.getPickImageIntent(CAArtisanDetail.this, getString(R.string.profile_pic_prompt));
                startActivityForResult(intent, PICK_PROFILE_ID);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CAArtisanDetail.this, CAEditActivity.class);
                i.putExtra("artisan", artisan.toString());
                startActivity(i);
            }
        });
    }

    /**
     * Populate Work Sample Pics Views
     */
    private void drawWorkPics() {
        View headerView = picsContainer.getChildAt(0);
        picsContainer.removeAllViews();
        picsContainer.addView(headerView);

        int certCount = artisan.getWorkPictures().size();

        for (int i=0; i < certCount; i++) {
            View workPicCard = LayoutInflater.from(this).inflate(R.layout.work_pic_card, null, false);

            // Set Image Content
            // TODO: Add a Loading indicator to Picasso
            final CAWorkPicture workPicture = artisan.getWorkPictures().get(i);
            ImageView imageView = (ImageView) workPicCard.findViewById(R.id.workPic);
            Picasso.with(this).load(workPicture.getUrl()).into(imageView);

            // Set title
            TextView label = (TextView) workPicCard.findViewById(R.id.description);
            label.setText(workPicture.getTitle());

            // Delete Event Handler
            workPicCard.findViewById(R.id.deletePic).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show a Confirmation dialog
                    new AlertDialog.Builder(CAArtisanDetail.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.delete_work_pic))
                        .setMessage(getString(R.string.delete_pic_confirmation))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgressBar();
                                artisanService.deleteWorkPic(artisan.getId(), workPicture.getId()).enqueue(new Callback<CAArtisan>() {
                                    @Override
                                    public void onResponse(Call<CAArtisan> call, Response<CAArtisan> response) {
                                        hideProgressBar();
                                        if (response.code() == 200) {
                                            artisan = response.body();
                                            drawWorkPics();
                                        } else {
                                            showShortToast(response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CAArtisan> call, Throwable t) {
                                        hideProgressBar();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                }
            });

            picsContainer.addView(workPicCard);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                try {
                    workPicBitmap = CAImagePicker.getImageFromResult(CAArtisanDetail.this, resultCode, data);
                    promptForTitle = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PICK_PROFILE_ID:
                profileBitmap = CAImagePicker.getImageFromResult(CAArtisanDetail.this, resultCode, data);
                uploadProfilePicture();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Prompt user to enter a title for the Work Pic selected
     */
    private void getPictTitle() {
        FragmentManager fm = getSupportFragmentManager();

        CertificateNameDialog cnd = CertificateNameDialog.newInstance();
        cnd.show(fm, "_cex");

        promptForTitle = false;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (promptForTitle) {
            getPictTitle();
        }
    }

    /**
     * Send Image to Cloudinary
     */
    private void uploadProfilePicture() {
        if (profileBitmap!= null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            profileBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapData = bos.toByteArray();
            final ByteArrayInputStream bs = new ByteArrayInputStream(bitmapData);

            showProgressBar();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final JSONObject profilePic = cloudinary.uploader().upload(bs, Cloudinary.emptyMap());

                        if (profilePic.has("secure_url")) {
                            artisan.setProfileUrl(profilePic.optString("secure_url"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressBar();
                                    updateArtisanPic();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showShortToast("Error encountered while uploading image. Please try again later.");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Post updated link to the API
     */
    private void updateArtisanPic() {
        CAArtisan payLoad = new CAArtisan();
        payLoad.setProfileUrl(artisan.getProfileUrl());

        showProgressBar();

        artisanService.updateArtisan(artisan.getId(), payLoad).enqueue(new Callback<CAArtisan>() {
            @Override
            public void onResponse(Call<CAArtisan> call, Response<CAArtisan> response) {
                hideProgressBar();

                if (response.code() == 200) {
                    artisan = response.body();
                    drawProfilePic();
                } else {
                    showShortToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<CAArtisan> call, Throwable t) {
                t.printStackTrace();
                hideProgressBar();
            }
        });
    }

    /**
     * User has set a title for the Work Pic. Upload it and update the View
     *
     * @param picTitle String
     */
    @Override
    public void onFinishEditDialog(String picTitle) {
        if (workPicBitmap != null && artisan.getWorkPictures().size() < 5) {
            final CAWorkPicture workPicture = new CAWorkPicture();
            workPicture.setTitle(picTitle);

            ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
            workPicBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos1);
            byte[] bitmapdata1 = bos1.toByteArray();
            final ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata1);

            showProgressBar();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final JSONObject workPicResponse = cloudinary.uploader().upload(bs, Cloudinary.emptyMap());

                        if (workPicResponse.has("secure_url")) {
                            workPicture.setUrl(workPicResponse.optString("secure_url"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressBar();
                                    // Proceed to Uploading to Server
                                    addNewWorkPic(workPicture);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showShortToast("Error encountered while uploading attachments. Please try again later.");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void clearSelectedBitmap() {
        workPicBitmap = null;
    }

    private void addNewWorkPic(CAWorkPicture workPicture) {
        showProgressBar();

        artisanService.addWorkPic(artisan.getId(), workPicture).enqueue(new Callback<CAArtisan>() {
            @Override
            public void onResponse(Call<CAArtisan> call, Response<CAArtisan> response) {
                hideProgressBar();

                if (response.code() == 200) {
                    artisan = response.body();
                    drawWorkPics();
                } else {
                    showShortToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<CAArtisan> call, Throwable t) {
                hideProgressBar();
            }
        });
    }
}
