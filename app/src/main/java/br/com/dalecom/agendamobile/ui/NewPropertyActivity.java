package br.com.dalecom.agendamobile.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
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

    private CircleImageView circleImageView;
    private TextView nameProperty;
    private EditText textPin;
    private Button btnBuscar;
    private RelativeLayout layoutProgress;
    private RelativeLayout layoutFinalize;
    private Property property;

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

        carregarFindsByIds();


    }


    public void buscarProperty(View view){
        layoutProgress.setVisibility(View.VISIBLE);
        int PIN = Integer.valueOf(textPin.getText().toString());

        restClient.getProperties(PIN, callbackProperty);

    }

    private Callback callbackProperty = new Callback<JsonObject>() {

        @Override
        public void success(JsonObject o, Response response) {

            if(!o.isJsonNull()) {
                property = new Property();
                property.setIdServer(o.get("id").getAsInt());
                property.setPin(o.get("pin").getAsString());
                property.setName(o.get("name").getAsString());
                property.setPhoto_path(o.get("photo_path").getAsString());
                property.setBucketPath(o.get("bucket_name").getAsString());
                property.setInfo(o.get("info").getAsString());

            }else{
                layoutProgress.setVisibility(View.INVISIBLE);
                nameProperty.setText("Ops! Nenhum estabelecimento encontrado.");
            }

            if(property.getPhoto_path() != null && property.getBucketPath() != null){

                final String pictureName = fileUtils.getUniqueName();
                final File pictureFile = fileUtils.getOutputMediaFile(fileUtils.MEDIA_TYPE_IMAGE, pictureName);

                s3.downloadProfileFile(pictureFile,property.getBucketPath()+"/"+property.getPhoto_path()).setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if(state == TransferState.COMPLETED) {
                            nameProperty.setText(property.getName());
                            property.setLocalImageLocationAndDeletePreviousIfExist(Uri.fromFile(pictureFile).toString());
                            circleImageView.setImageURI(Uri.fromFile(pictureFile));
                            btnBuscar.setVisibility(View.GONE);
                            layoutFinalize.setVisibility(View.VISIBLE);
                            layoutProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        Log.d(LogUtils.TAG, "Progress: "+ (int) (bytesCurrent * 100 / bytesTotal) + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.d(LogUtils.TAG, "Erro: "+ ex);
                        layoutProgress.setVisibility(View.INVISIBLE);
                        nameProperty.setText("Algo de errado aconteceu, tente novamente por favor");
                    }
                });


            }else{
                nameProperty.setText(property.getName());
                btnBuscar.setVisibility(View.GONE);
                layoutFinalize.setVisibility(View.VISIBLE);
                layoutProgress.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG, "FAILURE");
            layoutProgress.setVisibility(View.INVISIBLE);
            nameProperty.setText("Ops! Nenhum estabelecimento encontrado. Verifique sua conexão com a internet");
        }

    };

    public void saveProperty(View view){
        if(property != null){

            Property oldProperty = Property.findOne(property.getIdServer());

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

        }else{
            messageSnackbar("Nenhum estabelecimento selecionado", view);
        }

    }

    public void clearProperty(View view){
        nameProperty.setText(null);
        circleImageView.setImageResource(R.drawable.property_default);
        property = null;
        textPin.setText(null);
        layoutFinalize.setVisibility(View.GONE);
        btnBuscar.setVisibility(View.VISIBLE);
        messageSnackbar("Verifique junto ao estabelecimento se o PIN está correto",view);
    }

    private void notificationAdminProperty(){
        //criar logica
    }



    private void carregarFindsByIds(){
        circleImageView = (CircleImageView) findViewById(R.id.icon_property);
        nameProperty = (TextView) findViewById(R.id.title_property);
        textPin = (EditText) findViewById(R.id.text_pin);
        btnBuscar = (Button) findViewById(R.id.btn_buscar_property);
        layoutProgress = (RelativeLayout) findViewById(R.id.layout_progress);
        layoutFinalize = (RelativeLayout) findViewById(R.id.layout_finalize_or_clear);
    }

    private void messageSnackbar(String text, View view){
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
