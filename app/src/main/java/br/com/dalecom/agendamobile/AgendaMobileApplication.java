package br.com.dalecom.agendamobile;

import android.content.Context;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import br.com.dalecom.agendamobile.di.components.AppComponent;
import br.com.dalecom.agendamobile.di.components.DaggerAppComponent;
import br.com.dalecom.agendamobile.di.modules.AwsModule;
import br.com.dalecom.agendamobile.di.modules.FileModule;
import br.com.dalecom.agendamobile.di.modules.ManagerModule;
import br.com.dalecom.agendamobile.di.modules.RestClientModule;
import br.com.dalecom.agendamobile.di.modules.SharedPreferenceModule;


public class AgendaMobileApplication extends com.activeandroid.app.Application {

    AppComponent appComponent;
    public static Context mGlobalContext;


    public void onCreate() {
        super.onCreate();
        mGlobalContext = getApplicationContext();

        appComponent =  DaggerAppComponent.builder().
                        awsModule(new AwsModule(this)).
                        restClientModule(new RestClientModule(this)).
                        managerModule(new ManagerModule(this)).
                        fileModule(new FileModule(this)).
                        sharedPreferenceModule(new SharedPreferenceModule(this))
                        .build();

        initImageLoader(getApplicationContext());
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);

        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
