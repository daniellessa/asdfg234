package br.com.dalecom.agendamobile.service.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.wrappers.MixPanel;
import br.com.dalecom.agendamobile.wrappers.S3;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by viniciuslima on 11/10/15.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

    }

//    @Inject
//    public S3 s3Helper;
//    @Inject
//    public RestClient restClient;
//    @Inject
//    public SharedPreference sharedPreference;
//    @Inject
//    public FileUtils fileUtils;
//
//    static public List<Long> idsSyncing;
//    static public List<Long> imageIdsSyncing;
//
//    static public List<TransferObserver> observers;
//
//    ContentResolver mContentResolver;
//    Context mContext;
//
//    public SyncAdapter(Context context, boolean autoInitialize) {
//        super(context, autoInitialize);
//
//        if ( idsSyncing == null )
//            idsSyncing = new ArrayList<Long>();
//
//        if ( imageIdsSyncing == null )
//            imageIdsSyncing = new ArrayList<Long>();
//
//        if ( observers == null )
//            observers = new ArrayList<TransferObserver>();
//
//
//        Log.d(LogUtils.TAG, "SyncAdapter 1");
//        mContext = context;
//        mContentResolver = context.getContentResolver();
//        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
//    }
//
//    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
//        super(context, autoInitialize, allowParallelSyncs);
//
//        if ( idsSyncing == null )
//            idsSyncing = new ArrayList<Long>();
//
//        if ( imageIdsSyncing == null )
//            imageIdsSyncing = new ArrayList<Long>();
//
//        if ( observers == null )
//            observers = new ArrayList<TransferObserver>();
//
//        Log.d(LogUtils.TAG, "SyncAdapter 2");
//        mContext = context;
//        mContentResolver = context.getContentResolver();
//        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
//    }
//
//    /**
//     *
//     * @param account
//     * @param extras
//     * @param authority
//     * @param provider
//     * @param syncResult
//     *
//     * Note: The sync adapter framework runs onPerformSync() on a background thread
//     */
//    @Override
//    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
//        Log.d(LogUtils.TAG, "onPerformSync");
//
//        if ( !shouldSync() )
//            return;
//
//        syncHistoryStatus();
//
//        Event<Event> exams = Event.getAllExams();
//        MixPanel.trackEvent(mContext, "SyncStart");
//
//        for (int i = 0; i < exams.size(); i++) {
//            Exam exam = exams.get(i);
//            syncExam(exam);
//            syncLesionImagesFromExam(exam);
//            syncPatientProfilePhotoFromExam(exam);
//        }
//    }
//
//    private void syncHistoryStatus() {
//        restClient.getHistoryStatus(getExamIds(), new HistoryStatusCallback());
//    }
//
//    private ArrayList getExamIds() {
//        ArrayList<Exam> exams = (ArrayList) Exam.getLocalExamsByCurrentUser(getCurrentUserId());
//        ArrayList examIds = new ArrayList();
//        for (Exam exam : exams) {
//            examIds.add(exam.getServerId());
//        }
//        return examIds;
//    }
//
//    private Long getCurrentUserId() {
//        ( (DermatoApplication) mContext.getApplicationContext()).getAppComponent().inject(SyncAdapter.this);
//        User user = sharedPreference.getCurrentUser();
//        Long currentUserId = user.id;
//        return currentUserId;
//    }
//
//    private void syncLesionImagesFromExam(Exam exam) {
//        List<Lesion> lesions = exam.getLesions();
//        int lesionsSize = lesions.size();
//        for (int j = 0; j < lesionsSize; j++) {
//            List<Image> images = lesions.get(j).getImageList();
//            for (int k = 0; k < images.size(); k++) {
//                if ( !images.get(k).isSynced() ) {
//                    syncLesionImages(images.get(k),exam );
//                }
//            }
//        }
//    }
//
//    private void syncLesionImages(Image image,Exam exam) {
//        String uriString = image.getLocalImageLocation();
//        Uri myUri = Uri.parse(uriString);
//        File tFile = new File( myUri.getPath() );
//
//
//        String filePath = image.getPath();
//        String [] filePathArr = filePath.split("/");
//        String fileName = filePathArr[ filePathArr.length-1 ];
//
//        int position = observers.size();
//        observers.add(position,s3Helper.sendFile( tFile, fileName ));
//
//        MixPanel.trackEvent(mContext, "SyncLesionImages");
//
//        if (  !imageIdsSyncing.contains( image.getId() ) )
//        {
//            imageIdsSyncing.add(image.getId());
//            observers.get(position).setTransferListener(new S3TransferListener(image, exam ));
//        }
//    }
//
//    private void syncPatientProfilePhotoFromExam(Exam exam) {
//        Patient patient = exam.getPatient();
//
//        if ( patient.getLocalImageLocation() == null )
//            return;
//
//        if ( patient.isImageSynced() )
//            return;
//
//        String patientPhotoUriString = patient.getLocalImageLocation();
//        Uri patientPhotoUri = Uri.parse(patientPhotoUriString);
//        File patientPhotoFile = new File( patientPhotoUri.getPath() );
//
//        String patientFilePath = patient.getPhotoPath();
//        String [] patientFilePathArr = patientFilePath.split("/");
//        String patientFileName = patientFilePathArr[ patientFilePathArr.length-1 ];
//
//        TransferObserver patientS3Observer = s3Helper.sendFile( patientPhotoFile, patientFileName, fileUtils.getCurrentProfileBucketName() );
//
//        patient.setAsImageSynced();
//        patient.save();
//
//        patientS3Observer.setTransferListener(new S3TransferPatientListener( patient ));
//    }
//
//    private void syncExam(Exam exam) {
//        if ( !exam.hasServerIdInDb() )
//        {
//            JSONObject props = new JSONObject();
//            try
//            {
//                props.put("patientNameFromExam",exam.getPatient().getName());
//            }
//            catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//
//            MixPanel.trackEvent(mContext, "SyncExam", props);
//
//            if ( !idsSyncing.contains( exam.getId() ) )
//            {
//                idsSyncing.add(exam.getId());
//                restClient.postExam(exam, new PostExamCallback(exam));
//            }
//        }
//    }
//
//    private boolean shouldSync() {
//        if ( Connectivity.isConnected(mContext) )
//            return true;
//        return false;
//    }
//
//
//    class PostExamCallback implements Callback<JsonObject> {
//        private Exam mExam;
//        public PostExamCallback(Exam exam) {
//            mExam = exam;
//        }
//        @Override
//        public void success(JsonObject json, Response response) {
//            Log.d(LogUtils.TAG, "PostExamCallback SUCCESS");
//            mExam.setServerId(json.get("id").getAsLong());
//            mExam.setAsSynced();
//            mExam.save();
//
//            idsSyncing.remove(mExam.getId());
//
//            JSONObject props = new JSONObject();
//            try {
//                props.put("examServerId",json.get("id").getAsLong());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            MixPanel.trackEvent(mContext, "examSyncedSuccessfully", props);
//
////            deleteAllLesionPhotosFromExam(this.mExam);
//        }
//        @Override
//        public void failure(RetrofitError error) {
//
//            MixPanel.trackEvent(mContext, "examFailedToSync");
//            idsSyncing.remove(mExam.getId());
//            mExam.setAsNotSynced();
//            mExam.save();
//            Log.d(LogUtils.TAG, "PostExamCallback failure");
//        }
//
////        private void deleteAllLesionPhotosFromExam(Exam exam) {
////            for (Lesion lesion : exam.getLesions()) {
////                lesion.deleteImages();
////            }
////        }
//    }
//
//    class HistoryStatusCallback implements Callback<JsonArray> {
//
//        public HistoryStatusCallback() {}
//
//        @Override
//        public void success(JsonArray json, Response response) {
//            Log.d(LogUtils.TAG, "HistoryStatusCallback SUCCESS");
//
//            ExamParser parser = new ExamParser(json);
//            ArrayList<Exam> serverExams = (ArrayList) parser.parseHistoryExam();
//
//            ArrayList<Exam> localExams = (ArrayList) Exam.getLocalExamsByCurrentUser(getCurrentUserId());
//            for (Exam serverExam : serverExams) {
//                for (Exam localExam : localExams) {
//                    if (serverExam.getServerId() == localExam.getServerId()) {
//                        localExam.setStatus(serverExam.getStatus());
//                        localExam.save();
//                    }
//                }
//            }
//        }
//        @Override
//        public void failure(RetrofitError error) {
//            Log.d(LogUtils.TAG, "HistoryStatusCallback failure");
//        }
//    }
//
//
//    class S3TransferListener implements TransferListener {
//        private Image image;
//        private Exam exam;
//
//        public S3TransferListener(Image image, Exam exam) {
//            this.image = image;
//            this.exam = exam;
//        }
//
//        @Override
//        public void onStateChanged(int id, TransferState state) {
//            Log.d(LogUtils.TAG, "IMAGE LESION - onStateChanged: id = " + id + "  TransferState: " + state.toString());
//
//            JSONObject props = new JSONObject();
//            try {
//                props.put("lesionImageBucketAndPath",image.getBucketName() + '/'+image.getPath());
//                props.put("ImageId",image.getId());
//                props.put("Status",state.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            MixPanel.trackEvent(mContext, "S3OnStatusChanged", props);
//
//            if (state.toString().equals("COMPLETED")) {
//                image.setAsSynced();
//                image.save();
//                imageIdsSyncing.remove(image.getId());
//                MixPanel.trackEvent(mContext, "lesionImageSyncedComplete", props);
//                Log.e(LogUtils.TAG, "IMAGE LESION - COMPLETED " + id);
//            }
//
//            if (state.toString().equals("FAILED")) {
//                image.setAsNotSynced();
//                image.save();
//                imageIdsSyncing.remove(image.getId());
//            }
//        }
//
//        @Override
//        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            if ( bytesCurrent == bytesTotal )
//            {
//                Log.e(LogUtils.TAG, "bytesCurrent === bytesTotal , id = " + id);
//                image.setAsSynced();
//                image.save();
//                imageIdsSyncing.remove(image.getId());
//            }
//        }
//
//        @Override
//        public void onError(int id, Exception ex) {
//            image.setAsNotSynced();
//            image.save();
//            Log.e(LogUtils.TAG, "S3 ERROR IN" + id);
//        }
//    }
//
//    class S3TransferPatientListener implements TransferListener {
//        private Patient mPatient;
//        public S3TransferPatientListener(Patient patient) {
//            this.mPatient = patient;
//        }
//        @Override
//        public void onStateChanged(int id, TransferState state) {
//            if (state.toString().equals("COMPLETED")) {
//                Log.e(LogUtils.TAG, "PATIENT PROFILE -  COMPLETED!!!!");
//                this.mPatient.setAsImageSynced();
//                this.mPatient.save();
//            }
//
//            if ( state.toString().equals("FAILED") ) {
//                this.mPatient.setAsNotSynced();
//                this.mPatient.save();
//            }
//
//            Log.d(LogUtils.TAG, "PATIENT PROFILE - onStateChanged: id = " + id + "  TransferState: " + state.toString());
//        }
//        @Override
//        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            Log.d(LogUtils.TAG, "PATIENT PROFILE - onProgressChanged");
//        }
//        @Override
//        public void onError(int id, Exception ex) {
//            this.mPatient.setAsNotSynced();
//            this.mPatient.save();
//        }
//    }

}
