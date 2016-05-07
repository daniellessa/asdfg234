package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
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

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final int RC_SIGN_IN = 987;
    private GoogleApiClient mGoogleApiClient;
    private EditText editTextLogin;
    private EditText editTextPassword;
    private TextView btnLogin, textProfessional,textUser;
    private ProgressDialog dialog;
    private FloatingActionButton fab;
    private ImageView loginFacebook, loginGoogle;
    private CallbackManager callbackManager;


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

        setLoginFacebook();
        setLoginGoogle();

    }

    private void setLoginFacebook(){
        Log.d(LogUtils.TAG, "Entrou");

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(LogUtils.TAG, "LoginResult");

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                User userFacebook = new User();
                                Log.d(LogUtils.TAG, "Response: "+ response.toString());
                                try {

                                    userFacebook.setEmail(response.getJSONObject().get("email").toString());
                                    userFacebook.setName(response.getJSONObject().get("first_name").toString() + " " + response.getJSONObject().get("last_name").toString());
                                    userFacebook.setPassword(response.getJSONObject().get("id").toString());
                                    userFacebook.setPhotoPath(response.getJSONObject().getJSONObject("picture").getJSONObject("data").get("url").toString());
                                    userFacebook.setRole(4);
                                    userFacebook.setRegistrationId(sharedPreference.getUserRegistrationId());

                                    restClient.loginGoogleOrFacebbok(userFacebook, loginCallback);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.d(LogUtils.TAG, "email: "+userFacebook.getEmail());
                                Log.d(LogUtils.TAG, "name: "+userFacebook.getName());
                                Log.d(LogUtils.TAG, "password: "+userFacebook.getPassword());
                                Log.d(LogUtils.TAG, "picture: "+userFacebook.getPhotoPath());

                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "picture.type(large), id, first_name, last_name, email, gender, birthday, location");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        Log.d(LogUtils.TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LogUtils.TAG, "onError", error);
                    }
        });

        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });

    }


    private void setLoginGoogle(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LogUtils.TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            User userGoogle = new User();
            userGoogle.setEmail(acct.getEmail());
            userGoogle.setName(acct.getDisplayName());
            userGoogle.setPassword(acct.getId());
            userGoogle.setPhotoPath(acct.getPhotoUrl().toString());
            userGoogle.setRole(4);
            userGoogle.setRegistrationId(sharedPreference.getUserRegistrationId());

            restClient.loginGoogleOrFacebbok(userGoogle,loginCallback);

        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
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
        loginFacebook = (ImageView) findViewById(R.id.facebook_login);
        loginGoogle = (ImageView) findViewById(R.id.google_login);

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
        finish();
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

                            userFromServer.setLocalImageLocationAndDeletePreviousIfExist(Uri.fromFile(pictureFile).toString());

                            if ( userFromDb == null ) {
                                Log.d(LogUtils.TAG, "CREATING USER");
                                userFromServer.save();
                                sharedPreference.setUserRegistrationId(userFromServer.getRegistrationId());

                            }
                            else
                            {
                                userFromDb.setName(userFromServer.getName());
                                userFromDb.setBucketPath(userFromServer.getBucketPath());
                                userFromDb.setPhotoPath(userFromServer.getPhotoPath());
                                userFromDb.setLocalImageLocationAndDeletePreviousIfExist(Uri.fromFile(pictureFile).toString());
                                userFromDb.setRole(userFromServer.getRole());
                                userFromDb.setRegistrationId(sharedPreference.getUserRegistrationId());
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

                    if(userFromServer.getPhotoPath() != null)
                        userFromServer.setLocalImageLocationAndDeletePreviousIfExist(userFromServer.getPhotoPath());

                    userFromServer.save();
                    sharedPreference.setCurrentUser(userFromServer);

                }
                else {
                    userFromDb.setName(userFromServer.getName());
                    userFromDb.setBucketPath(userFromServer.getBucketPath());
                    userFromDb.setPhotoPath(userFromServer.getPhotoPath());

                    if(userFromServer.getPhotoPath() != null)
                        userFromDb.setLocalImageLocationAndDeletePreviousIfExist(userFromServer.getPhotoPath());

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

        if(!user.get("email").isJsonNull())
            userObj.setEmail(user.get("email").getAsString());

        if(!user.get("name").isJsonNull())
            userObj.setName(user.get("name").getAsString());

        if(!user.get("id").isJsonNull())
            userObj.setIdServer(user.get("id").getAsInt());

        if(!user.getAsJsonArray("roles").isJsonNull()){

            int role = 0;
            for (int i = 0; i < user.getAsJsonArray("roles").size(); i++){
                if(role == 0){
                    role = user.getAsJsonArray("roles").get(i).getAsJsonObject().get("roles").getAsInt();
                }else {
                    if(user.getAsJsonArray("roles").get(i).getAsJsonObject().get("roles").getAsInt() > role){
                        role = user.getAsJsonArray("roles").get(i).getAsJsonObject().get("roles").getAsInt();
                    }
                }
            }

            userObj.setRole(role);
            Log.d(LogUtils.TAG, "Role finded: "+ userObj.getRole());

        }



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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LogUtils.TAG, "onConnectionFailed");
    }
}
