package br.com.dalecom.agendamobile.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import br.com.dalecom.agendamobile.utils.FileUtils;
import dagger.Module;
import dagger.Provides;

/**
 * Created by guilhermeduartemattos on 12/15/15.
 */
@Module
public class FileModule {
    Context mContext;

    public FileModule(Context mContext) {
        this.mContext = mContext;
    }

    // Which objects do you want dagger to provide?

    @Provides
    @Singleton
    public FileUtils provideFileUtils() {
        return new FileUtils(mContext);
    }
}
