package br.com.dalecom.agendamobile.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.AlertAdapter;
import br.com.dalecom.agendamobile.adapters.EventsAdapter;
import br.com.dalecom.agendamobile.model.Alert;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;

public class AlertsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AlertAdapter adapter;
    private List<Alert> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setFindByIds();
        setRecyclerView();
    }

    private void setFindByIds(){

    }

    private void setRecyclerView(){

        mList = Alert.getAlerts();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_alerts);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new AlertAdapter(this,mList,mRecyclerView);
        mRecyclerView.setAdapter(adapter);

//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {

//                    }
//                })
//        );


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
