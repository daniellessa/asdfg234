package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.Calendar;

import javax.inject.Inject;
import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.fragments.CustomDialogFragment;
import br.com.dalecom.agendamobile.fragments.DialogFragmentLogin;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.gcm.RegistrationIntentService;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.S3;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private EditText editTextLogin;
    private EditText editTextPassword;
    private TextView btnLogin, textProfessional,textUser;
    private ProgressDialog dialog;
    private FloatingActionButton fab, fab_user, fab_professional;


    @Inject
    public SharedPreference sharedPreference;

    @Inject
    public RestClient restClient;

    @Inject
    public S3 s3;

    @Inject
    public FileUtils fileUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();


        if (sharedPreference.hasUserToken()) {
            startHomeActivity();
            finish();
        }

        getViewsImpl();

        if (checkPlayServices()) {
            if(!sharedPreference.hasUserRegistrationId()){
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

    }


    private void getViewsImpl() {

        editTextLogin = (EditText) findViewById(R.id.edit_text_login);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        btnLogin = (Button) findViewById(R.id.button_login);
        dialog = new ProgressDialog(LoginActivity.this);
        textProfessional = (TextView) findViewById(R.id.text_professional);
        textUser = (TextView) findViewById(R.id.text_user);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialog();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {
                    dialog.setIndeterminate(true);
                    dialog.setMessage(getResources().getString(R.string.wait));
                    dialog.setCancelable(false);
                    dialog.show();


                    restClient.login(
                            editTextLogin.getText().toString(),
                            editTextPassword.getText().toString(),
                            loginCallback);

                } else {
                    showFailDialog();
                }

            }
        });

        isReturnNewUser();
    }

    private void isReturnNewUser(){
        try {
            User user = (User) getIntent().getSerializableExtra("user");
            if(user != null){
                editTextLogin.setText(user.getEmail());
                editTextPassword.setText(user.getPassword());

                Toast.makeText(this,"Usuário criado com sucesso!",Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){};
    }

    private void startHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private Callback loginCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject o, Response response) {
            dialog.dismiss();

            final User userFromServer = userMarshalling(o.getAsJsonObject("user"));
            final User userFromDb = User.getUserByServerId(userFromServer.getIdServer());


            if ( o.get(S.KEY_TOKEN) == null || o.get(S.KEY_TOKEN).getAsString().equals("") )
            {
                showFailDialog();
                return;
            }

            sharedPreference.setUserToken(o.get(S.KEY_TOKEN).getAsString());

            if(userFromServer.getBucketPath() != null && userFromServer.getPhotoPath() != null){

                String namePath = fileUtils.getUniqueName();
                final File pictureFile = fileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_IMAGE, namePath);

                s3.downloadProfileFile(pictureFile, userFromServer.getBucketPath()+userFromServer.getPhotoPath()).setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {

                        if (state == TransferState.COMPLETED) {
                            userFromDb.setLocalImageLocationAndDeletePreviousIfExist(Uri.fromFile(pictureFile).toString());
                            userFromServer.setLocalImageLocationAndDeletePreviousIfExist(Uri.fromFile(pictureFile).toString());

                            if ( userFromDb == null )
                            {
                                Log.d(LogUtils.TAG, "CREATING USER");
                                userFromServer.save();
                                sharedPreference.setCurrentUser(userFromServer);

                            }
                            else
                            {
                                userFromDb.setName(userFromServer.getName());
                                userFromDb.setBucketPath(userFromServer.getBucketPath());
                                userFromDb.setPhotoPath(userFromServer.getPhotoPath());

                                userFromDb.save();
                                sharedPreference.setCurrentUser(userFromDb);
                            }

                            startHomeActivity();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        Log.d(LogUtils.TAG, "Progress: "+ (int) (bytesCurrent * 100 / bytesTotal) + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.d(LogUtils.TAG, "Erro s3 Login: "+ ex);
                    }
                });
            }else{

                if ( userFromDb == null )
                {
                    Log.d(LogUtils.TAG, "CREATING USER");
                    userFromServer.save();
                    sharedPreference.setCurrentUser(userFromServer);

                }
                else
                {
                    userFromDb.setName(userFromServer.getName());
                    userFromDb.setBucketPath(userFromServer.getBucketPath());
                    userFromDb.setPhotoPath(userFromServer.getPhotoPath());

                    userFromDb.save();
                    sharedPreference.setCurrentUser(userFromDb);
                }


                startHomeActivity();
            }


        }

        @Override
        public void failure(RetrofitError error) {
            showFailDialog();
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields() {
        if (editTextLogin.getText() == null || editTextLogin.getText().toString().equals(""))
            return false;
        if (editTextPassword.getText() == null || editTextPassword.getText().toString().equals(""))
            return false;
        return true;
    }

    private void showFailDialog() {
        dialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(R.string.error)
                .setMessage(R.string.invalid_login_fields)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private User userMarshalling(JsonObject user) {
        final User userObj = new User();

        if(!user.get("bucket_name").isJsonNull())
            userObj.setBucketPath(user.get("bucket_name").getAsString());

        if(!user.get("photo_path").isJsonNull())
            userObj.setPhotoPath(user.get("photo_path").getAsString());

        userObj.setEmail(user.get("email").getAsString());
        userObj.setName(user.get("name").getAsString());
        userObj.setIdServer(user.get("id").getAsInt());


        return userObj;
    }

    private void initDialog(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DialogFragmentLogin cdf = new DialogFragmentLogin();
        cdf.show(ft, "dialogLogin");
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LogUtils.TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
