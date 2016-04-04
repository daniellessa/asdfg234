package br.com.dalecom.agendamobile.di.modules;

import android.content.Context;
import javax.inject.Singleton;
import br.com.dalecom.agendamobile.utils.EventManager;
import dagger.Module;
import dagger.Provides;


@Module
public class ManagerModule {
    Context mContext;

    public ManagerModule(Context mContext) {
        this.mContext = mContext;
    }

    // Which objects do you want dagger to provide?

    @Provides @Singleton
    public EventManager provideEventManager() {
        return new EventManager(mContext);
    }
}
