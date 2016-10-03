package com.thonnycleuton.meusonibus;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Localizacao;
import com.equalsp.stransthe.Veiculo;
import com.equalsp.stransthe.rotas.PontoDeInteresse;
import com.thonnycleuton.meusonibus.inthegraAPI.InthegraService;
import com.thonnycleuton.meusonibus.inthegraAPI.Util;

import java.io.IOException;
import java.util.List;

/**
 * Created by thonnycleuton on 01/10/16.
 */
public class SeguirOnibus extends IntentService{

    private List<Veiculo> veiculos;
    private Veiculo veiculo;
    private Linha linha;
    private Localizacao meuLocal;

    private Handler UI_HANDLER_Notify = new Handler();
    private Runnable UI_UPDTAE_RUNNABLE_Notify = new Runnable() {
        @Override
        public void run() {
            notificar();
        }
    };

    public SeguirOnibus() {
        super("SeguirOnibus");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        linha = (Linha) extras.get("linha");
        meuLocal = new PontoDeInteresse(Util.TERESINA.latitude, Util.TERESINA.longitude);
        UI_HANDLER_Notify.postDelayed(UI_UPDTAE_RUNNABLE_Notify, Util.VEICULOS_REFRESH_TIME);
    }

    protected void notificar() {

        Intent newIntent = new Intent(SeguirOnibus.this, Onibus_detail.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(SeguirOnibus.this, 0, newIntent, 0);

        Notification notification = new NotificationCompat.Builder(SeguirOnibus.this)
                .setContentTitle(getString(R.string.new_notification))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.drawable.bus_notity)//TODO criar um icone de onibus de vista lateral
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
