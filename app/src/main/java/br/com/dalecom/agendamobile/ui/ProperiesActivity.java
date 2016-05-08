package br.com.dalecom.agendamobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.activeandroid.query.Select;
import com.google.gson.JsonObject;

import java.util.List;
import javax.inject.Inject;
import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.PropertiesAdapter;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProperiesActivity extends AppCompatActivity {

    private List<Property> mList;
    private PropertiesAdapter adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Inject
    public RestClient restClient;

    @Inject
    public SharedPreference sharedPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ( (AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_properies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent it = new Intent(ProperiesActivity.this, NewPropertyActivity.class);
                startActivity(it);

            }
        });

        setRecyclerView();


    }

    private void setRecyclerView(){

        mList = getallProperties();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_properties);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new PropertiesAdapter(this,mList);
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(LogUtils.TAG, "Property: "+ mList.get(position).getName());
                        sharedPreference.setCurrentProperty(mList.get(position));
                        Log.d(LogUtils.TAG, "sharedPreference Property: " + sharedPreference.getCurrentProperty().getName());
                        finish();
                    }
                })
        );


    }



    public List<Property> getallProperties(){
        return new Select()
                .from(Property.class)
                .orderBy("name ASC")
                .execute();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int PIN = (Integer) data.getSerializableExtra("PIN");
        //restClient.getProperties(PIN, propertyCallback);

        Log.d(LogUtils.TAG, "Result: " + PIN );
    }

    private Callback propertyCallback = new Callback<JsonObject>() {

        @Override
        public void success(JsonObject o, Response response) {

            Property propertyFromServer = new Property();
            propertyFromServer.setId(o.get("id").getAsInt());
            propertyFromServer.setName(o.get("name").getAsString());
            propertyFromServer.setPhoto_path(o.get("photo_path").getAsString());
            propertyFromServer.setBucketPath(o.get("bucket_path").getAsString());
            propertyFromServer.setInfo(o.get("info").getAsString());

            //propertyFromServer.save();
            return;
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG, "Faill request PropertyActivity");
            return;
        }


    };


    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_professional, menu);
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
}
