package br.com.dalecom.agendamobile.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import dagger.Module;
import dagger.Provides;

/**
 * Created by viniciuslima on 11/23/15.
 */
@Module
public class SharedPreferenceModule {
    Context mContext;
    // Which objects do you want dagger to provide?


    public SharedPreferenceModule(Context mContext) {
        this.mContext = mContext;
    }

    @Provides
    @Singleton
    public SharedPreference provideExamManager() {
        return new SharedPreference(mContext);
    }
}
