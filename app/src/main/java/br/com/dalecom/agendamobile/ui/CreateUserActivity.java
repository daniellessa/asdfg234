package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.di.modules.AwsModule;
import br.com.dalecom.agendamobile.fragments.DialogFragmentCreateUser;
import br.com.dalecom.agendamobile.fragments.DialogFragmentLogin;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.S3;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateUserActivity extends AppCompatActivity {

    private final int CAMERA_ACTIVITY = 4563;
    private final int GALLERY_CODE = 3523;
    private DialogFragmentCreateUser fragment;
    private String photoUri;
    public ImageLoader imageLoader;
    private File file;
    private Uri outFileUri;
    private ProgressDialog dialog;
    private boolean hasProfileImage;
    private CircleImageView imageView;
    private EditText textName, textEmail, textPassword, textPasswordConfirm;
    private Spinner spinnerSex;
    private Button btnSave;
    private User user;
    private View view;

    @Inject
    RestClient restClient;

    @Inject
    FileUtils fileUtils;

    @Inject
    S3 s3;

    @Inject
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_create_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateFindByIds();

        Log.d(LogUtils.TAG, "RegistrationID do SharedPreferences: "+ sharedPreference.getUserRegistrationId());

    }

    private void populateFindByIds(){

        imageView = (CircleImageView) findViewById(R.id.icon_perfil);
        textName = (EditText) findViewById(R.id.edit_text_name);
        textEmail = (EditText) findViewById(R.id.edit_text_email);
        textPassword = (EditText) findViewById(R.id.edit_text_password);
        textPasswordConfirm = (EditText) findViewById(R.id.edit_text_password_confirm);
        spinnerSex = (Spinner) findViewById(R.id.spinner_sex);
        btnSave = (Button) findViewById(R.id.btn_save_user);

        ArrayAdapter<CharSequence> adapterSex = ArrayAdapter.createFromResource(this,R.array.sex_array,android.R.layout.simple_list_item_1);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(adapterSex);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view = v;
                validateForm(v);
            }
        });
    }

    private boolean validateForm(View view){

        if(textName.getText().toString().length() == 0){
            Snackbar.make(view, "Preencha o campo nome", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }

        if(textEmail.getText().toString().length() == 0){
            Snackbar.make(view, "Preencha o campo nome", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }


        if(textPassword.getText().toString().length() == 0){
            Snackbar.make(view, "Preencha o campo senha", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }

        if(textPasswordConfirm.getText().toString().length() == 0){
            Snackbar.make(view, "Confirme sua senha", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }

        if(spinnerSex.getSelectedItemPosition() == 0){
            Snackbar.make(view, "Selecione o sexo", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }

        if(!validateEmailAndPassword(view))
            return false;


        user = new User();
        user.setName(textName.getText().toString());
        user.setEmail(textEmail.getText().toString());
        user.setPassword(textPassword.getText().toString());
        user.setRole(4);

        if(sharedPreference.hasUserRegistrationId()){
            user.setRegistrationId(sharedPreference.getUserRegistrationId());
        }

        if(spinnerSex.getSelectedItemPosition() == 1)
            user.setSex("F");
        else
            user.setSex("M");

        if ( photoUri != null )
        {
            user.setLocalImageLocationAndDeletePreviousIfExist(photoUri);

            String fileName = getFileNameFromUriString( photoUri );
            String bucketPath = fileUtils.getCurrentUserBucketPath();
            user.setLocalImageLocationAndDeletePreviousIfExist(photoUri);
            user.setBucketPath(fileUtils.getCurrentProfileBucketName());
            user.setPhotoPath(bucketPath + "/" + fileName);

            dialog = ProgressDialog.show(CreateUserActivity.this,"Aguarde","Criando usuário 0%",false,true);

            s3.sendFile(file,fileName,S.BUCKET_PREFIX).setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d(LogUtils.TAG, "S3 Stage: "+ state);
                    if(state == TransferState.COMPLETED){
                        //user.save();
                        dialog.dismiss();
                        restClient.postUser(user, callbackPostUser);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    Log.d(LogUtils.TAG, "S3 Progress: " + bytesCurrent);
                    dialog.setMessage("Criando usuário " + (int) (bytesCurrent * 100 / bytesTotal) + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(LogUtils.TAG, "S3 Error: "+ ex);
                    dialog.setMessage("Ops! Falhou");
                    dialog.dismiss();
                }
            });

        }else{
            //user.save();
            restClient.postUser(user, callbackPostUser);
        }




        return true;
    }

    private String getFileNameFromUriString(String photoUri) {
        String[] parts = photoUri.split("/");
        return parts[ parts.length - 1 ];
    }

    private Callback callbackPostUser = new Callback<JsonObject>() {

        @Override
        public void success(JsonObject jsonObject, Response response) {
            Snackbar.make(view, "Selecione o sexo", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            Intent it = new Intent(CreateUserActivity.this,LoginActivity.class);
            it.putExtra("user", user);
            startActivity(it);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    };

    private boolean validateEmailAndPassword(View view){
        if(!textEmail.getText().toString().contains("@")) {
            Snackbar.make(view, "Email inválido", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }

        if(!textEmail.getText().toString().contains(".")){
            Snackbar.make(view, "Email inválido", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }

        if(textPassword.getText().toString().length() < 6){
            Snackbar.make(view, "Sua senha deve possuir no mínimo 6 caracteres", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        if(!textPassword.getText().toString().equals(textPasswordConfirm.getText().toString())){
            Snackbar.make(view, "As senhas devem ser iguais", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        return true;

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if(requestCode == CAMERA_ACTIVITY){

            if(resultCode == RESULT_OK) {
                outFileUri = fragment.getUri();
                Crop.of(outFileUri, outFileUri).withMaxSize(400, 400).asSquare().start(this);
            }else {
                outFileUri = fragment.getUri();
                fileUtils.deleteFileByUriString(outFileUri.toString());
            }
        }else if(requestCode == GALLERY_CODE){
            if(resultCode == RESULT_OK){

                File imageCaptured = imageFromGallery(result);
                File file = fileUtils.getOutputMediaFile(1, fileUtils.getUniqueName());

                try {
                    copy(imageCaptured,file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri uri = Uri.fromFile(file);
                outFileUri = uri;
                this.file = file;
                Crop.of(outFileUri, outFileUri).withMaxSize(400, 400).asSquare().start(this);
            }
        } else if (requestCode == Crop.REQUEST_CROP) {

            if(resultCode == RESULT_OK){
                photoUri = outFileUri.toString();
                displayProfilePhotoByUri(photoUri);
            }else {
                fileUtils.deleteFileByUriString(outFileUri.toString());
            }
        }
    }

    private void displayProfilePhotoByUri(String uriString) {
        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .build();

        imageLoader.displayImage(uriString, imageView, options);
        hasProfileImage = true;
    }

    private void initDialog(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new DialogFragmentCreateUser();
        fragment.show(ft, "dialogTakePicture");
    }

    private File imageFromGallery(Intent data) {

        Uri selectedImage = data.getData();
        String [] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        Log.d(LogUtils.TAG, "FILEPATH: "+ filePath);
        File file = new File(filePath);

        return file;

    }


    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_times, menu);
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
