package br.com.dalecom.agendamobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.Calendar;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.ui.HomeActivity;
import br.com.dalecom.agendamobile.ui.TimesActivity;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.LogUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by daniellessa on 26/02/16.
 */
public class DialogFragmentConfirmEvent extends DialogFragment {

    private TextView titleView;
    private TextView messageView;
    private String title;
    private String message;
    private Context mContext;
    private Callback callback;

    @Inject
    RestClient restClient;

    @Inject
    EventManager eventManager;

    public DialogFragmentConfirmEvent(Context context, String message, Callback callback){
        mContext = context;
        ((AgendaMobileApplication) context.getApplicationContext()).getAppComponent().inject(this);
        this.title = eventManager.getCurrentService().getTitle();
        this.message = message;
        this.callback = callback;


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Dialog);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.content_dialog_confirm, container);

        RelativeLayout btnConfirm = (RelativeLayout) view.findViewById(R.id.btn_ok_dialog_confirm);
        RelativeLayout btnCancel = (RelativeLayout) view.findViewById(R.id.btn_cancel_dialog);
        titleView = (TextView) view.findViewById(R.id.title_dialog);
        messageView = (TextView) view.findViewById(R.id.content_text_dialog);

        setTitle(title);
        setMessage(message);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        btnConfirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                restClient.postEvent(eventManager.getEvent(), callback);
            }
        });

        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }


    private void setTitle(String title){
        titleView.setText(title);
    }

    private void setMessage(String message){
        messageView.setText(message);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
