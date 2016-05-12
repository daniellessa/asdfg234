package br.com.dalecom.agendamobile.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.EventsAdapter;
import br.com.dalecom.agendamobile.adapters.PropertiesAdapter;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.EventParser;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewHomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private User currentUser;
    private ImageView imageView;
    private Toolbar toolbar;

    @Inject SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_new_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryLight));

        setCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent it = new Intent(NewHomeActivity.this, NewPropertyActivity.class);
                    startActivity(it);

                }
            });
        }

    }

    private void setCurrentUser(){

        currentUser = sharedPreference.getCurrentUser();
        imageView = (ImageView) findViewById(R.id.icon_perfil);

        try {

            if (currentUser.getLocalImageLocation().length() > 0) {
                imageView.setImageURI(Uri.parse(currentUser.getLocalImageLocation()));
            }

            if (currentUser.getBucketPath().length() == 0 && currentUser.getPhotoPath().length() > 0) {
                new DownloadImageGoogle().execute();
            }
        } catch (NullPointerException ex) {
            Intent it = new Intent(this, LoginActivity.class);
            startActivity(it);
        }
    }

    private class DownloadImageGoogle extends AsyncTask<Void, Void, Void> {

        private Bitmap image;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                InputStream is = new URL(currentUser.getPhotoPath()).openStream();
                image = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (image != null)
                imageView.setImageBitmap(image);
            Drawable drawableIcon = new BitmapDrawable(getResources(), image);
            getSupportActionBar().setLogo(drawableIcon);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:

                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private List<Event> mListNotExpired;
        private RecyclerView mRecyclerView;
        private RecyclerView.LayoutManager layoutManager;
        private EventsAdapter adapter;

        private List<Property> mList;
        private PropertiesAdapter adapterProperty;
        private RecyclerView mRecyclerViewProperty;
        private RecyclerView.LayoutManager layoutManagerProperty;

        @Inject
        SharedPreference sharedPreference;

        @Inject
        RestClient restClient;

        @Inject
        EventManager eventManager;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ((AgendaMobileApplication) getActivity().getApplicationContext()).getAppComponent().inject(this);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {

                case 1:
                    final View view = inflater.inflate(R.layout.fragment_my_appointments_not_expired, container, false);
                    final RelativeLayout layoutNull = (RelativeLayout) view.findViewById(R.id.layout_null);
                    final RelativeLayout layoutProgress = (RelativeLayout) view.findViewById(R.id.layout_progress);
                    final RelativeLayout layoutError = (RelativeLayout) view.findViewById(R.id.layout_error);
                    if(mListNotExpired == null) {
                        layoutProgress.setVisibility(View.VISIBLE);
                        restClient.getEventsNotExpired(new Callback<JsonArray>() {
                            @Override
                            public void success(JsonArray jsonArray, Response response) {

                                EventParser servicesParser = new EventParser(jsonArray);
                                mListNotExpired = servicesParser.parseFullEvents();
                                setRecyclerViewNotExpired(view, mListNotExpired);
                                layoutProgress.setVisibility(View.GONE);
                                if(mListNotExpired == null){
                                    layoutNull.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //configurar view para list vazia
                                layoutProgress.setVisibility(View.GONE);
                                layoutError.setVisibility(View.VISIBLE);
                            }
                        });
                    }else{
                        setRecyclerViewNotExpired(view, mListNotExpired);
                    }
                    return view;
                case 2:
                    final View view2 = inflater.inflate(R.layout.content_home, container, false);
                    mList = getallProperties();
                    mRecyclerViewProperty = (RecyclerView) view2.findViewById(R.id.recyclerView_properties);
                    layoutManagerProperty = new LinearLayoutManager(getContext());
                    mRecyclerViewProperty.setLayoutManager(layoutManagerProperty);
                    adapterProperty = new PropertiesAdapter(getContext(), mList);
                    mRecyclerViewProperty.setAdapter(adapterProperty);

                    mRecyclerViewProperty.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    sharedPreference.setCurrentProperty(mList.get(position));
                                    Calendar currentDate = Calendar.getInstance();
                                    eventManager.startNewEvent(currentDate);
                                    eventManager.setCurrentProperty(mList.get(position));

                                    Intent it = new Intent(getContext(), PropertyActivity.class);
                                    startActivity(it);
                                }
                            })
                    );

                    FloatingActionButton fab = (FloatingActionButton) view2.findViewById(R.id.fab);
                    if (fab != null) {
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent it = new Intent(getContext(), NewPropertyActivity.class);
                                startActivity(it);

                            }
                        });
                    }
                    return view2;
            }

            return null;

        }

        public List<Property> getallProperties() {
            return new Select()
                    .from(Property.class)
                    .orderBy("name ASC")
                    .execute();
        }

        private void setRecyclerViewNotExpired(View view, List<Event> mList){


            mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_my_appointments);
            layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            adapter = new EventsAdapter(getContext(),mList);
            mRecyclerView.setAdapter(adapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                        }
                    })
            );

        }
    }




        /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Próximos Agendamentos";
                case 1:
                    return "Agendar";
            }
            return null;
        }
    }
}
