package ng.softcom.bespoke.craftadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.api.interfaces.CALoginInterface;
import ng.softcom.bespoke.craftadmin.api.responses.CALoginResponse;
import ng.softcom.bespoke.craftadmin.models.CALogin;
import ng.softcom.bespoke.craftadmin.utils.CAAlerts;
import ng.softcom.bespoke.craftadmin.utils.Craft;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CALoginActivity extends AppCompatActivity {

    private static String TAG = CALoginActivity.class.getSimpleName();

    private SmoothProgressBar progressBar;
    private Button loginBtn;
    private EditText txtUsername, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar!=null) toolbar.setLogo(R.drawable.toolbar_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);
        loginBtn = (Button) findViewById(R.id.btn_login);

        txtUsername = (EditText) findViewById(R.id.input_email);
        txtPassword = (EditText) findViewById(R.id.input_password);

        String AUTH_ENDPOINT = getString(R.string.web_service_url);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(AUTH_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final CALoginInterface authService = retrofit.create(CALoginInterface.class);
        final Craft craft = new Craft(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;

                email = txtUsername.getText().toString();
                password = txtPassword.getText().toString();

                if (email.isEmpty()) {
                    txtUsername.setError(getString(R.string.email_required_message));
                } else if (password.isEmpty()) {
                    txtPassword.setError(getString(R.string.password_required_message));
                } else {

                    txtUsername.clearFocus();
                    txtPassword.clearFocus();

                    progressBar.setVisibility(View.VISIBLE);

                    // Authenticate the User
                    Call<CALoginResponse> responseCall = authService.login(new CALogin(email, password));
                    responseCall.enqueue(new Callback<CALoginResponse>() {
                        @Override
                        public void onResponse(Call<CALoginResponse> call, Response<CALoginResponse> response) {

                            // Hide Progress Bar
                            progressBar.setVisibility(View.GONE);

                            if (response.code() == 200) {
                                CALoginResponse apiResponse = response.body();

                                // If ApI response is Okay
                                if (apiResponse.getMessage() == null) {
                                    // Save the Token to Shared Prefs
                                    craft.getSharedPrefs().writeString(apiResponse.getToken(), getString(R.string.user_request_token));
                                    craft.getSharedPrefs().writeString(apiResponse.getUser().getId(), getString(R.string.logged_in_as_key));
                                    // Save the User Object
                                    craft.getSharedPrefs().writeString(apiResponse.getUser().toString(), getString(R.string.user_data_key));
                                    // Go to Main Activity
                                    startActivity(new Intent(CALoginActivity.this, CAMainActivity.class));
                                    finish();
                                } else {
                                    CAAlerts.showLongToast(CALoginActivity.this, apiResponse.getMessage());
                                }
                            } else {
                                Log.d(TAG, "LOGIN RESPONSE: " + response.toString());
                                loginFailed();
                            }
                        }

                        @Override
                        public void onFailure(Call<CALoginResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);

                            loginFailed();
                            Log.d(TAG, "CALogin Failure: " + t.getMessage());
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void loginFailed() {
        CAAlerts.showShortToast(this, "Login Unsuccessful!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_password) {
            // TODO: Switch to Password reset Page
            CAAlerts.showShortToast(this, "Show Password Reset Page");
        }

        return super.onOptionsItemSelected(item);
    }
}
