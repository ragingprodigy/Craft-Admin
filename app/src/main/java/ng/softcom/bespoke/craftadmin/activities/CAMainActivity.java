package ng.softcom.bespoke.craftadmin.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.api.interfaces.CAMetaInterface;
import ng.softcom.bespoke.craftadmin.fragments.ArtisansFragment;
import ng.softcom.bespoke.craftadmin.fragments.ArtisansFragment.AFListener;
import ng.softcom.bespoke.craftadmin.fragments.NewArtisan;
import ng.softcom.bespoke.craftadmin.fragments.NewArtisan.NAListener;
import ng.softcom.bespoke.craftadmin.models.CABank;
import ng.softcom.bespoke.craftadmin.models.CASpecialty;
import ng.softcom.bespoke.craftadmin.views.ESViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CAMainActivity extends CASecureActivity implements AFListener, NAListener, android.location.LocationListener {

    private FloatingActionButton fab;
    private ViewPager mViewPager;
    private FragmentPagerAdapter fpAdapter;

    private ViewPager.OnPageChangeListener pcListener;
    private Menu myMenu;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) toolbar.setLogo(R.drawable.toolbar_logo);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        progressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);

        createEventHandlers();
        pullMetaData();

        makeLocationRequest();
    }

    @Override
    public void initializeLocationRequest() {
        makeLocationRequest();
    }

    private void makeLocationRequest() {
        LocationManager mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Issue meta data requests
     */
    private void pullMetaData() {
        // Load Meta Data and Cache Them
        String BASE_URL = getString(R.string.web_service_url);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(craft.getAPIClient())
                .build();

        final CAMetaInterface metaService = retrofit.create(CAMetaInterface.class);

        // Get List of Banks
        metaService.banksList().enqueue(new Callback<List<CABank>>() {
            @Override
            public void onResponse(Call<List<CABank>> call, Response<List<CABank>> response) {
                if (response.code() == 200) {
                    String bankResponse = response.body().toString();
                    craft.getSharedPrefs().writeString(bankResponse, getString(R.string.banks_cache_key));
                } else {
                    showShortToast("Couldn't get list of Banks");
                }
            }

            @Override
            public void onFailure(Call<List<CABank>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // Get List of Specialties
        metaService.specialtiesList().enqueue(new Callback<List<CASpecialty>>() {
            @Override
            public void onResponse(Call<List<CASpecialty>> call, Response<List<CASpecialty>> response) {
                if (response.code() == 200) {
                    String specialtiesResponse = response.body().toString();
                    craft.getSharedPrefs().writeString(specialtiesResponse, getString(R.string.specialties_cache_key));
                } else {
                    showShortToast("Couldn't get list of Specialties");
                }
            }

            @Override
            public void onFailure(Call<List<CASpecialty>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Create Event Handlers
     */
    private void createEventHandlers() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch View Pager Views
                // mViewPager.setCurrentItem(1);
                startActivity(new Intent(CAMainActivity.this, CANewArtisan.class));
            }
        });

        fpAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return ArtisansFragment.newInstance();
                    default:
                        return NewArtisan.newInstance();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        mViewPager = (ESViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(fpAdapter);

        pcListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setTitle(getString(R.string.artisans_list));
                        fab.setVisibility(View.VISIBLE);

                        if (myMenu != null) {
                            MenuItem menuItem = myMenu.findItem(R.id.new_record);
                            if (menuItem != null) {
                                menuItem.setVisible(true);
                            }
                        }
                        break;
                    case 1:
                        setTitle(getString(R.string.action_new_artisan));
                        fab.setVisibility(View.GONE);

                        if (myMenu != null) {
                            MenuItem menuItem = myMenu.findItem(R.id.new_record);
                            if (menuItem != null) {
                                menuItem.setVisible(false);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };

        mViewPager.addOnPageChangeListener(pcListener);
    }

    @Override
    public void showListFragment() {
        mViewPager.removeAllViews();
        mViewPager.setAdapter(fpAdapter);
        fpAdapter.notifyDataSetChanged();

        mViewPager.setCurrentItem(0);
        pcListener.onPageSelected(0);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public Location getCurrentLocation() {
        return mLastLocation;
    }

    @Override
    public void onLocationChanged(Location loc) {
        mLastLocation = loc;
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
