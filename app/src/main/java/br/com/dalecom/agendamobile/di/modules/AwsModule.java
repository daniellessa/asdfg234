package br.com.dalecom.agendamobile.di.modules;

import android.content.Context;

import br.com.dalecom.agendamobile.wrappers.S3;
import dagger.Module;
import dagger.Provides;

/**
 * Created by viniciuslima on 10/15/15.
 */
@Module
public class AwsModule {

    Context mContext;

    public AwsModule(Context mContextParam) {
        mContext = mContextParam;
    }

    // Which objects do you want dagger to provide?

    @Provides
    S3 provideS3() {
        return new S3(mContext);
    }
}
