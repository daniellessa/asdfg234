package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.ProfessionalsAdapter;
import br.com.dalecom.agendamobile.adapters.expandable.ExpandableAdapter;
import br.com.dalecom.agendamobile.adapters.expandable.Header;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.HeaderParser;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.ProfessionalParser;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfessionalsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RelativeLayout layoutNull;
    private TextView textViewNull;
    public ProgressDialog dialog;
    private RecyclerView.LayoutManager layoutManager;
    private ProfessionalsAdapter adapter;
    private Toolbar toolbar;
    private Property currentProperty;
    private List<User> mList;
    private List<Header> mListHeader;

    @Inject public RestClient restClient;

    @Inject public SharedPreference sharedPreference;

    @Inject public EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_professionals);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setSubtitle("por tipo de serviço");
        layoutNull = (RelativeLayout) findViewById(R.id.layout_null);
        textViewNull = (TextView) findViewById(R.id.text_null);

        currentProperty = sharedPreference.getCurrentProperty();
        restClient.getProfessionals(eventManager.getCurrentProperty().getIdServer(), eventManager.getCurrentService().getIdServer(), callbackProfessionals);
        dialog = ProgressDialog.show(ProfessionalsActivity.this,"Aguarde","Procurando profissionais...",false,true);

    }

    private void setRecyclerView(){
        
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_professionals);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ProfessionalsAdapter(this,mList);
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        eventManager.setUserProfIntoEvent(mList.get(position));
                        Intent it = new Intent(ProfessionalsActivity.this, TimesActivity.class);
                        startActivity(it);
                    }
                })
        );

    }

    private Callback callbackProfessionals = new Callback<JsonArray>(){


        @Override
        public void success(JsonArray jsonArray, Response response) {

            if(jsonArray.size() < 1){
                textViewNull.setText("Ops!\nNenhum profissional cadastrado neste estabelecimento.");
                layoutNull.setVisibility(View.VISIBLE);
            }

            ProfessionalParser professionalParser = new ProfessionalParser(ProfessionalsActivity.this, jsonArray);
            mList = professionalParser.parseFullProfessionas();
            setRecyclerView();
            dialog.dismiss();
            //getListCategoryFromServer();

        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG, "Result request professionals FAIL: ");
            textViewNull.setText("Algo de errado aconteceu!\nVerifique sua conexão com a internet ou tente mais tarde.");
            layoutNull.setVisibility(View.VISIBLE);
            dialog.dismiss();
        }
    };

    private void getListCategoryFromServer(){
        restClient.getCategories(callbackCategory);
    }

    private Callback callbackCategory = new Callback<JsonArray>(){

        @Override
        public void success(JsonArray jsonArray, Response response) {
           HeaderParser parser = new HeaderParser(jsonArray);
            mListHeader = parser.parseFullCategory();
            setRecyclerView();
            dialog.dismiss();
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG,"Failure getCategory: "+ error);
        }
    };

    private List<Header> generateProfessionals(List<Header> headers, List<User> users) {

        List<Header> recipe = new ArrayList<>();
        for (Header header : headers) {

            List<User> childItemList = new ArrayList<>();

            for (User user : users){
                Log.d(LogUtils.TAG, "Varrendo user");
                if(header.getId() == user.getProfessional().getCategory()) {
                    childItemList.add(user);
                    Log.d(LogUtils.TAG, "Profissional Add");
                }
            }

            if(childItemList.size() > 0){
                header.setItens(childItemList);
                recipe.add(header);
                Log.d(LogUtils.TAG, "Recipe: "+ recipe.size());
            }
        }
        return recipe;

//        List<ParentListItem> parentListItems = new ArrayList<>();
//        for (Header header : headers) {
//
//            List<User> childItemList = new ArrayList<>();
//
//            for (User user : users){
//                if(header.getId() == user.getProfessional().getCategory()) {
//                    childItemList.add(user);
//                    Log.d(LogUtils.TAG, "Profissional Add");
//                }
//            }
//
//            Log.d(LogUtils.TAG, "Header: " + header.getItens().size());
//
//            if(header.getItens() != null){
//                parentListItems.add(header);
//                header.setItens(childItemList);
//            }
//
//
//        }
//        return parentListItems;
    }

    @Override
    protected void onResume() {
        super.onResume();
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
