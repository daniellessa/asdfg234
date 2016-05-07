package br.com.dalecom.agendamobile.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.dalecom.agendamobile.adapters.NewPropertyAdapter;
import br.com.dalecom.agendamobile.adapters.PropertiesAdapter;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.PropertyParser;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.S3;
import retrofit.Callback;
import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewPropertyActivity extends AppCompatActivity {


    private Property property;
    private List<Property> mList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private NewPropertyAdapter adapter;
    private SearchView searchView;
    private int viewId;

    @Inject
    public RestClient restClient;

    @Inject
    S3 s3;

    @Inject FileUtils fileUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_new_property);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setRecycclerView();


    }

    private void setRecycclerView(){

        mList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewSerchable);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new NewPropertyAdapter(this, mList);
        mRecyclerView.setAdapter(adapter);


        hendleSearch(getIntent());


        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        saveProperty(mList.get(position));
                    }
                })
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        hendleSearch(intent);
    }

    public void hendleSearch(Intent intent){
        if(intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())){
            //String q = intent.getStringExtra(SearchManager.QUERY);
        }else{
            if(searchView != null){
                String q = searchView.getQuery().toString();
                if(q.length() > 3){
                    restClient.getProperties(q, callbackProperty);
                }
            }

        }
    }


    private Callback callbackProperty = new Callback<JsonArray>() {

        @Override
        public void success(JsonArray jsonArray, Response response) {

            PropertyParser parser = new PropertyParser(jsonArray);

            mList = parser.parseFullProperty();
            mRecyclerView.setAdapter(new PropertiesAdapter(NewPropertyActivity.this, mList));
            adapter.notifyDataSetChanged();



        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG, "FAILURE");
        }

    };

    public void saveProperty(Property property){
        if(property != null){

            Property oldProperty = Property.findOne(property.getIdServer());
            property.setNotification(true);

            if(oldProperty == null){
                property.save();
                notificationAdminProperty();
                finish();
            }else{
                oldProperty.delete();
                property.save();
                notificationAdminProperty();
                finish();
            }

        }

    }


    private void notificationAdminProperty(){

        restClient.notifyNewAssociation(property.getIdServer(), new Callback<JsonObject>() {

            @Override
            public void success(JsonObject jsonObject, Response response) {
                Log.d(LogUtils.TAG, "notificationAdminProperty: SUCESS");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LogUtils.TAG, "notificationAdminProperty: FAIL");
            }
        });
    }


    private void messageSnackbar(String text, View view){
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchable, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem item = menu.findItem(R.id.action_searchable);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            searchView = (SearchView) item.getActionView();
        }else{
            searchView = (SearchView) MenuItemCompat.getActionView(item);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hendleSearch(getIntent());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                hendleSearch(getIntent());
                return false;
            }
        });

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
