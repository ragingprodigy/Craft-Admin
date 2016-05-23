package ng.softcom.bespoke.craftadmin.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.util.List;

import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.activities.CAArtisanDetail;
import ng.softcom.bespoke.craftadmin.adapters.CAArtisansListAdapter;
import ng.softcom.bespoke.craftadmin.adapters.CABaseAdapter;
import ng.softcom.bespoke.craftadmin.api.interfaces.CAArtisanInterface;
import ng.softcom.bespoke.craftadmin.models.CAArtisan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtisansFragment.AFListener} interface
 * to handle interaction events.
 * Use the {@link ArtisansFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtisansFragment extends CAFragment {

    private AFListener mListener;
    private CAArtisanInterface artisanService;

    private CABaseAdapter<CAArtisan, ?> adapter;
    private DynamicListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyState;

    private AlphaInAnimationAdapter animationAdapter;

    public ArtisansFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArtisansFragment.
     */
    public static ArtisansFragment newInstance() {
        return new ArtisansFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new CAArtisansListAdapter();
        ((CAArtisansListAdapter) adapter).setContext(getActivity());

        animationAdapter = new AlphaInAnimationAdapter(adapter);

        String BASE_URL = getString(R.string.web_service_url);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(craft.getAPIClient())
                .build();

        artisanService = retrofit.create(CAArtisanInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artisans, container, false);

        listView = (DynamicListView) rootView.findViewById(R.id.list);
        animationAdapter.setAbsListView(listView);
        listView.setAdapter(adapter);

        listView.enableSwipeToDismiss(new OnDismissCallback() {
            @Override
            public void onDismiss(ViewGroup listView, int[] reverseSortedPositions) {
                for (final int position : reverseSortedPositions) {
                    CAArtisan artisan = adapter.getItem(position);

                    mListener.showProgressBar();
                    artisanService.deleteArtisan(artisan.getId()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            mListener.hideProgressBar();
                            if (response.code() == 204) {
                                // Remove from Cache too
                                adapter.remove(position);
                                craft.getSharedPrefs().writeString(adapter.getData().toString(), getString(R.string.artsans_list_cache_key));
                            } else {
                                showShortToast("Unable to delete Artisan data from server. " + response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            mListener.hideProgressBar();
                        }
                    });
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        emptyState = rootView.findViewById(R.id.emptyState);
        
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Setup Swipe Action
         */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFromRemote();
            }
        });

        /**
         * Item Click Listener for the ListView
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CAArtisan artisan = adapter.getItem(position);
//                FragmentManager fm = getChildFragmentManager();
//                CAArtisanDialog adFragment = CAArtisanDialog.newInstance(artisan);
//                adFragment.show(fm, "fragment_artisan_detail");
                CAArtisan artisan = adapter.getItem(position);

                Intent i = new Intent(getActivity(), CAArtisanDetail.class);
                i.putExtra(CAArtisanDetail.DATA_KEY, artisan.toString());
                startActivity(i);
//                startActivity(new Intent(getActivity(), CAArtisanDetail.class));
            }
        });
        
        updateAdapter();
    }

    /**
     * Pull Blogs From Cache and Remote and Send them to the ListView
     */
    private void updateAdapter() {

        // Cache First
        String cached = craft.getSharedPrefs().getString(getString(R.string.artsans_list_cache_key));
        if (!cached.isEmpty()) {
            Gson gson = new Gson();

            List<CAArtisan> artisans = gson.fromJson(cached, new TypeToken<List<CAArtisan>>(){}.getType());
            setDataAndUpdateUI(artisans);
        }

        // Load Data from server
        loadFromRemote();
    }

    /**
     * Load Blogs List from Server
     */
    private void loadFromRemote() {
        mListener.showProgressBar();

        artisanService.getArtisans().enqueue(new Callback<List<CAArtisan>>() {
            @Override
            public void onResponse(Call<List<CAArtisan>> call, Response<List<CAArtisan>> response) {
                if (isAdded() && mListener != null) {
                    mListener.hideProgressBar();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (response.code() == 200) {
                        // Save to Cache
                        craft.getSharedPrefs().writeString(response.body().toString(), getString(R.string.artsans_list_cache_key));
                        // Update Adapter
                        setDataAndUpdateUI(response.body());

                        if (response.body().size() == 0)
                            showShortToast(getString(R.string.no_artisans));
                    } else {
                        showShortToast(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CAArtisan>> call, Throwable t) {
                showShortToast("Failed request..." + t.getMessage() );

                if (mListener != null) mListener.hideProgressBar();
                t.printStackTrace();
            }
        });
    }

    /**
     * Bind Data and make call to updateUI
     * @param artisans List<CAArtisan>
     */
    private void setDataAndUpdateUI(List<CAArtisan> artisans) {
        adapter.setData(artisans);
        adapter.notifyDataSetChanged();

        updateUI();
    }

    /**
     * Toggle View visibility
     */
    private void updateUI() {
        if (adapter.getCount() > 0) {
            emptyState.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AFListener) {
            mListener = (AFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AFListener");
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
    public interface AFListener {
        void showProgressBar();
        void hideProgressBar();
    }
}
