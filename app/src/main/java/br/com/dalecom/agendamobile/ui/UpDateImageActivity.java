package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
import br.com.dalecom.agendamobile.fragments.DialogFragmentCreateUser;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.S3;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpDateImageActivity extends AppCompatActivity {

    private final int CAMERA_ACTIVITY = 4563;
    private final int GALLERY_CODE = 3523;
    private DialogFragmentCreateUser fragment;
    private String photoUri;
    private ImageLoader imageLoader;
    private File file;
    private Uri outFileUri;
    private ProgressDialog dialog;
    private boolean hasProfileImage;
    private CircleImageView imageView;
    private User user;
    private User currentUser;
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
        setContentView(R.layout.activity_update_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setSubtitle(getResources().getString(R.string.subtitle_update_image));

        populateFindByIds();
        currentUser = sharedPreference.getCurrentUser();
        if(currentUser.getLocalImageLocation() != null){
            imageView.setImageURI(Uri.parse(currentUser.getLocalImageLocation()));
        }else{
            imageView.setImageResource(R.drawable.user_default);
        }

    }

    private void populateFindByIds(){

        imageView = (CircleImageView) findViewById(R.id.icon_perfil);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });
    }

    public void saveImage(View view){



        if ( photoUri != null )
        {
            user = User.getUserByServerId(currentUser.getIdServer());
            user.setLocalImageLocationAndDeletePreviousIfExist(photoUri);

            String fileName = getFileNameFromUriString(photoUri);
            user.setLocalImageLocationAndDeletePreviousIfExist(photoUri);
            user.setBucketPath(fileUtils.getCurrentProfileBucketName());
            user.setPhotoPath("/" + fileName);

            dialog = ProgressDialog.show(UpDateImageActivity.this,"Aguarde","Salvando imagem 0%",false,true);

            s3.sendFile(file,fileName,S.BUCKET_PREFIX).setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d(LogUtils.TAG, "S3 Stage: "+ state);
                    if(state == TransferState.COMPLETED){
                        user.save();
                        sharedPreference.getCurrentUser().setLocalImageLocation(photoUri);
                        Log.d(LogUtils.TAG, "photoUri " + photoUri);
                        Log.d(LogUtils.TAG, "SharedPreferences " + sharedPreference.getCurrentUser().getLocalImageLocation());
                        restClient.postImage(user, callbackPostUser);
                        dialog.setMessage("Atualizando usuário ");
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    Log.d(LogUtils.TAG, "S3 Progress: " + bytesCurrent);
                    dialog.setMessage("Salvando imagem " + (int) (bytesCurrent * 100 / bytesTotal) + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(LogUtils.TAG, "S3 Error: "+ ex);
                    dialog.setMessage("Ops! Falha ao salvar a imagem");
                    dialog.dismiss();
                }
            });

        }else{
            Snackbar.make(view,"Nenhuma imagem selecionada",Snackbar.LENGTH_SHORT)
                    .setAction("action",null)
                    .show();
        }

    }

    private String getFileNameFromUriString(String photoUri) {
        String[] parts = photoUri.split("/");
        return parts[ parts.length - 1 ];
    }

    private Callback callbackPostUser = new Callback<JsonObject>() {

        @Override
        public void success(JsonObject jsonObject, Response response) {
            dialog.setMessage("Usuário atualizado");
            dialog.dismiss();
            Intent it = new Intent(UpDateImageActivity.this,HomeActivity.class);
            startActivity(it);
        }

        @Override
        public void failure(RetrofitError error) {
            dialog.dismiss();
        }
    };


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
