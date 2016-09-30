package com.thonnycleuton.meusonibus.inthegraAPI.AsyncTasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Veiculo;
import com.thonnycleuton.meusonibus.inthegraAPI.InthegraService;
import com.thonnycleuton.meusonibus.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask resposnável por carregar as informações de véiculos de uma dada linha.
 /**
 * Created by thonnycleuton on 19/09/16.
 */

public class InthegraVeiculosAsync extends AsyncTask<Linha, Void, List<Veiculo>> implements DialogInterface.OnCancelListener {
    private final String TAG = "FileHandler";
    public InthegraVeiculosAsyncResponse delegate = null;
    private ProgressDialog dialog;
    private AlertDialog alert;
    private Context mContext;
    private boolean wasUnsuccessful;

    public InthegraVeiculosAsync(Context context){
        Log.i(TAG, "Constructor Called");
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute Called");
        super.onPreExecute();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setMessage("Não foi possível recuperar os veículos da Linha informada");
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton("Certo",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertBuilder.create();

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Carregando veículos...");
//        dialog.show();
    }

    @Override
    protected List<Veiculo> doInBackground(Linha... params) {
        Log.i(TAG, "doInBackground Called");
        Linha linha = params[0];
        List<Veiculo> veiculos = new ArrayList<>();
        try {
            Log.d(TAG, "Recuperando veículos da linha... ");
            veiculos = InthegraService.getVeiculos(linha);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar os veículos da linha, motivo: " + e.getMessage());
            wasUnsuccessful = true;
        } finally {
            dialog.dismiss();
        }
        return veiculos;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.i(TAG, "onCancel Called");
        cancel(true);
    }

    @Override
    protected void onPostExecute(List<Veiculo> veiculos) {
        Log.i(TAG, "onPostExecute Called");
        if (wasUnsuccessful) {
            Toast.makeText(mContext, "Não foi possível encontrar os veículos...", Toast.LENGTH_SHORT).show();
        }
        delegate.processFinish(veiculos);
    }
}
