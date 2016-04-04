package br.com.dalecom.agendamobile.wrappers;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;

/**
 * Created by viniciuslima on 10/13/15.
 */
public class S3 {

    private AmazonS3 s3Client = null;
    private Context mContext;
    @Inject public FileUtils fileUtils;

    @Inject
    public S3(Context context) {

        ((AgendaMobileApplication) context).getAppComponent().inject(this);

        Log.d(LogUtils.TAG,"Constructing S3 Object");
        mContext = context;

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                S.COGNITO_POOL_ID, // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        s3Client = new AmazonS3Client(credentialsProvider);
        s3Client.setRegion(Region.getRegion(Regions.SA_EAST_1));
    }

    public AmazonS3 getS3Client(Context context) {
        return s3Client;
    }

     public TransferUtility getS3TransferUtility() {
        TransferUtility transferUtility = new TransferUtility(s3Client, mContext);
        return transferUtility;
    }

    public TransferObserver sendFile(File file,String fileName) {
        TransferUtility transferUtility = this.getS3TransferUtility();
        String bucketFullAddressPath = fileUtils.getFullBucketPath();

        return transferUtility.upload(
                bucketFullAddressPath,
                fileName,
                file
        );
    }

    public TransferObserver sendFile(File file,String fileName, String bucketName) {
        TransferUtility transferUtility = this.getS3TransferUtility();
        String bucketFullAddressPath = bucketName + "/" + fileUtils.getCurrentUserBucketPath();
        return transferUtility.upload(
                bucketFullAddressPath,
                fileName,
                file
        );
    }

    public TransferObserver downloadProfileFile(File file, String nameWithPath) {
        TransferUtility transferUtility = this.getS3TransferUtility();
        String currentBucketName = fileUtils.getCurrentProfileBucketName();

        return transferUtility.download(
                currentBucketName,     /* The bucket to download from */
                nameWithPath,//nameWithPath,    /* The key for the object to download */ //Ex: pio-xii/dermato_20151130_182136.jpg
                file        /* The file to download the object to */
        );
    }

    public TransferObserver downloadResizedProfileFile(File file, String nameWithPath) {
        TransferUtility transferUtility = this.getS3TransferUtility();
        String currentBucketName = fileUtils.getCurrentResizedProfileBucketName();

        return transferUtility.download(
                currentBucketName,     /* The bucket to download from */
                nameWithPath,//nameWithPath,    /* The key for the object to download */ //Ex: pio-xii/dermato_20151130_182136.jpg
                file        /* The file to download the object to */
        );
    }

}
