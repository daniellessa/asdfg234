package br.com.dalecom.agendamobile.di.components;

import android.content.Context;

import javax.inject.Singleton;


import br.com.dalecom.agendamobile.adapters.AlertAdapter;
import br.com.dalecom.agendamobile.adapters.EventsAdapter;
import br.com.dalecom.agendamobile.adapters.ProfessionalsAdapter;
import br.com.dalecom.agendamobile.adapters.TimesAdapter;
import br.com.dalecom.agendamobile.fragments.DialogFragmentConfirmEvent;
import br.com.dalecom.agendamobile.fragments.DialogFragmentCreateUser;
import br.com.dalecom.agendamobile.service.gcm.RegistrationIntentService;
import br.com.dalecom.agendamobile.ui.CreateUserActivity;
import br.com.dalecom.agendamobile.ui.HomeActivity;
import br.com.dalecom.agendamobile.ui.LoginActivity;
import br.com.dalecom.agendamobile.ui.MyAppointmentsActivity;
import br.com.dalecom.agendamobile.ui.NewPropertyActivity;
import br.com.dalecom.agendamobile.ui.ProfessionalsActivity;;
import br.com.dalecom.agendamobile.ui.ProperiesActivity;
import br.com.dalecom.agendamobile.ui.PropertyActivity;
import br.com.dalecom.agendamobile.ui.ServicesActivity;
import br.com.dalecom.agendamobile.ui.TimesActivity;
import br.com.dalecom.agendamobile.di.modules.AwsModule;
import br.com.dalecom.agendamobile.di.modules.FileModule;
import br.com.dalecom.agendamobile.di.modules.ManagerModule;
import br.com.dalecom.agendamobile.di.modules.RestClientModule;
import br.com.dalecom.agendamobile.di.modules.SharedPreferenceModule;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.service.sync.SyncAdapter;
import br.com.dalecom.agendamobile.ui.UpDateImageActivity;
import br.com.dalecom.agendamobile.utils.CalendarTimes;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.ProfessionalParser;
import br.com.dalecom.agendamobile.wrappers.MixPanel;
import br.com.dalecom.agendamobile.wrappers.S3;
import dagger.Component;


@Singleton
@Component(modules = {AwsModule.class, ManagerModule.class, SharedPreferenceModule.class, RestClientModule.class, FileModule.class})
public interface AppComponent {
    //Where do you want dagger to provide this object?
    void inject(SyncAdapter syncAdapter);
    void inject(ProfessionalsActivity professionalsActivity);
    void inject(TimesActivity timesActivity);
    void inject(ServicesActivity servicesActivity);
    void inject(LoginActivity loginActivity);
    void inject(EventManager eventManager);
    void inject(FileUtils fileUtils);
    void inject(RestClient restClient);
    void inject(HomeActivity homeActivity);
    void inject(S3 s3);
    void inject(MixPanel mixPanel);
    void inject(NewPropertyActivity newPropertyActivity);
    void inject(ProperiesActivity properiesActivity);
    void inject(CreateUserActivity createUserActivity);
    void inject(DialogFragmentCreateUser dialogFragmentCreateUser);
    void inject(RegistrationIntentService registrationIntentService);
    void inject(CalendarTimes calendarTimes);
    void inject(DialogFragmentConfirmEvent dialogFragmentConfirmEvent);
    void inject(TimesAdapter timesAdapter);
    void inject(EventsAdapter eventsAdapter);
    void inject(ProfessionalsAdapter professionalsAdapter);
    void inject(ProfessionalParser professionalParser);
    void inject(UpDateImageActivity upDateImageActivity);
    void inject(PropertyActivity propertyActivity);
    void inject(PropertyActivity.PlaceholderFragment placeholderFragment);
    void inject(MyAppointmentsActivity.PlaceholderFragment placeholderFragment);
    void inject(AlertAdapter alertAdapter);

}
