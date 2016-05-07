package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import java.util.List;
import javax.inject.Inject;
import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.ServiceAdapter;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import br.com.dalecom.agendamobile.utils.ServicesParser;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PropertyActivity extends AppCompatActivity {

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
    private ImageView propertyImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    public static FragmentManager fragmentManager;

    @Inject EventManager eventManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        ((AgendaMobileApplication) getApplicationContext()).getAppComponent().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        fragmentManager = getSupportFragmentManager();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryLight));
        tabLayout.setSelectedTabIndicatorHeight(10);
        setFindsByIds();



    }

    private void setFindsByIds(){
        propertyImage = (ImageView) findViewById(R.id.icon_property);
        propertyImage.setImageURI(Uri.parse(eventManager.getCurrentProperty().getLocalImageLocation()));

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setTitle(eventManager.getCurrentProperty().getName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_property, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (id){
            case R.id.action_settings:
                //
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
        private Switch mySwitch;
        private RecyclerView mRecyclerView;
        private RecyclerView.LayoutManager layoutManager;
        private ServiceAdapter adapter;
        private List<Service> mList;
        private ProgressDialog dialog;
        private float lat, lng;
        private static View view;

        private SupportMapFragment fragment;
        private GoogleMap map;

        @Inject
        EventManager eventManager;
        @Inject
        RestClient restClient;

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

            switch (getArguments().getInt(ARG_SECTION_NUMBER)){

                case 1:
                    view = inflater.inflate(R.layout.fragment_property_services, container, false);

                    if(mList == null) {

                        dialog = ProgressDialog.show(getActivity(), "Aguarde", "Carregando Serviços...", false, true);

                        restClient.getServiceForProperty(eventManager.getCurrentProperty().getIdServer(), new Callback<JsonArray>() {
                            @Override
                            public void success(JsonArray jsonArray, Response response) {

                                ServicesParser servicesParser = new ServicesParser(jsonArray);
                                mList = servicesParser.parseFullServices();
                                setRecyclerView(view);
                                dialog.dismiss();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                //configurar view para list vazia
                                dialog.dismiss();
                            }
                        });
                    }else{
                        setRecyclerView(view);
                        dialog.dismiss();
                    }

                    break;

                case 2:
                    view = inflater.inflate(R.layout.fragment_property_map, container, false);
                    break;

                case 3:

                    view = inflater.inflate(R.layout.fragment_property_about, container, false);
                    TextView propertyName = (TextView) view.findViewById(R.id.property_name);
                    TextView propertyPhone = (TextView) view.findViewById(R.id.property_phone);
                    TextView propertyAdress = (TextView) view.findViewById(R.id.property_adress);
                    TextView propertyOpen = (TextView) view.findViewById(R.id.property_open);
                    TextView propertyOpenHours = (TextView) view.findViewById(R.id.property_open_hour);
                    Switch mySwitch = (Switch) view.findViewById(R.id.switch_property_notify);
                    RelativeLayout propertyCall = (RelativeLayout) view.findViewById(R.id.layout_property_call);

                    if(eventManager.getCurrentProperty().isNotification())
                        mySwitch.setChecked(true);
                    else
                        mySwitch.setChecked(false);

                    mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            Log.d(LogUtils.TAG, "Checked: "+ isChecked);
                            Property property = Property.findOne(eventManager.getCurrentProperty().getIdServer());
                            property.setNotification(isChecked);
                            property.save();
                        }
                    });


                    propertyName.setText(eventManager.getCurrentProperty().getName());
                    propertyPhone.setText(eventManager.getCurrentProperty().getPhone());

                    String address = eventManager.getCurrentProperty().getStreet()+", "+
                            eventManager.getCurrentProperty().getNumber()+" - "+
                            eventManager.getCurrentProperty().getCity();
                    propertyAdress.setText(address);

                    propertyOpen.setText(eventManager.getCurrentProperty().getOpenDay());
                    propertyOpenHours.setText(eventManager.getCurrentProperty().getOpenHour());

                    propertyCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+eventManager.getCurrentProperty().getPhone()));
                            startActivity(it);
                        }
                    });
                    break;
            }

            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                Log.d(LogUtils.TAG, "onActivityCreated");
                FragmentManager fm = getChildFragmentManager();
                fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
                if (fragment == null) {
                    fragment = SupportMapFragment.newInstance();
                    fm.beginTransaction().replace(R.id.map, fragment).commit();
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if (map == null && getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                lat = eventManager.getCurrentProperty().getLat();
                lng = eventManager.getCurrentProperty().getLng();
                Log.d(LogUtils.TAG,"LatLng: "+ lat +" - "+ lng);
                map = fragment.getMap();
                map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 12.0f));
            }
        }


        private void setRecyclerView(View view){


            mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_services);
            layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            adapter = new ServiceAdapter(getContext(),mList);
            mRecyclerView.setAdapter(adapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            eventManager.setServiceIntoEvent(mList.get(position));
                            goToProfessionals();
                        }
                    })
            );

        }

        private void goToProfessionals(){
            Intent it = new Intent(getActivity(), ProfessionalsActivity.class);
            startActivity(it);
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
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Serviços";
                case 1:
                    return "Mapa";
                case 2:
                    return "Sobre";
            }
            return null;
        }
    }

}
