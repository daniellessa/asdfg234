package br.com.dalecom.agendamobile.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import br.com.dalecom.agendamobile.service.rest.RestClient;
import dagger.Module;
import dagger.Provides;

/**
 * Created by viniciuslima on 11/24/15.
 */
@Module
public class RestClientModule {
    Context mContext;
    // Which objects do you want dagger to provide?

    public RestClientModule(Context mContext) {
        this.mContext = mContext;
    }

    @Provides
    @Singleton
    public RestClient provideRestClient() {
        return new RestClient(mContext);
    }
}
