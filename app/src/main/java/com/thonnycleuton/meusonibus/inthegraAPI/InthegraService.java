package com.thonnycleuton.meusonibus.inthegraAPI;

import android.content.Context;
import android.util.Log;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.Veiculo;
import com.equalsp.stransthe.rotas.PontoDeInteresse;
import com.equalsp.stransthe.rotas.Rota;
import com.equalsp.stransthe.rotas.RotaService;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by thonnycleuton on 19/09/16.
 */
public class InthegraService {
    private final static String TAG = "ServiceSingleton";
    private static CachedInthegraService cachedService;
    private static RotaService rotaService;
    private static com.equalsp.stransthe.InthegraService service;

    public static void initInstance(Context context) throws IOException {
        Log.d(TAG, "initInstance Called");
        if (cachedService == null) {
            service = new com.equalsp.stransthe.InthegraService("123ece2c0e7642138f19bdcf937a2ff3", "thonnycleuton@gmail.com", "C0ncr3t0.");
        }
    }

    public static CachedInthegraService getInstance() {
        Log.d(TAG, "getInstance Called");
        return cachedService;
    }

    public static List<Parada> getParadas() throws IOException {
        return getParadas(null);
    }

    public static List<Parada> getParadas(Linha linha) throws IOException {
        Log.d(TAG, "getParadas Called");
        List<Parada> paradas;
        if (linha == null) {
            paradas = cachedService.getParadas();
        } else {
            paradas = cachedService.getParadas(linha);
        }

        if(!paradas.isEmpty()) {
            Collections.sort(paradas, new Comparator<Parada>() {
                @Override
                public int compare(Parada p1, Parada p2) {
                    return p1.getCodigoParada().compareTo(p2.getCodigoParada());
                }
            });
        }
        return paradas;
    }

    public static List<Linha> getLinhas() throws IOException {
        return getLinhas();
    }

    public static List<Linha> getLinhas(Parada parada) throws IOException {
        Log.d(TAG, "getLinhas Called");
        List<Linha> linhas;

        if(parada == null) {
            linhas = cachedService.getLinhas();
        } else {
            linhas = cachedService.getLinhas(parada);
        }

        if (!linhas.isEmpty()) {
            Collections.sort(linhas, new Comparator<Linha>() {
                @Override
                public int compare(Linha l1, Linha l2) {
                    return l1.getCodigoLinha().compareTo(l2.getCodigoLinha());
                }
            });
        }
        return linhas;
    }

    public static List<Linha> getLinhas(String s) throws IOException{
        List<Linha> linhas = cachedService.getLinhas(s);
        return linhas;
    }

    public static List<Veiculo> getVeiculos(Linha linha) throws IOException {
        Log.d(TAG, "getVeiculos Called");
        List<Veiculo> veiculos;
        if(linha == null) {
            veiculos = cachedService.getVeiculos();
        } else {
            veiculos = cachedService.getVeiculos(linha);
        }

        if (!veiculos.isEmpty()) {
            Collections.sort(veiculos, new Comparator<Veiculo>() {
                @Override
                public int compare(Veiculo v1, Veiculo v2) {
                    return v1.getCodigoVeiculo().compareTo(v2.getCodigoVeiculo());
                }
            });
        }
        return veiculos;
    }

    public static Set<Rota> getRotas(LatLng origem, LatLng destino, double distanciaMaxima) throws IOException {
        Log.d(TAG, "getRotas Called");
        assert origem != null;
        double origemLat = origem.latitude;
        double origemLng = origem.longitude;

        assert destino != null;
        double destinoLat = destino.latitude;
        double destinoLng = destino.longitude;

        PontoDeInteresse p1 = new PontoDeInteresse(origemLat, origemLng);
        PontoDeInteresse p2 = new PontoDeInteresse(destinoLat, destinoLng);

        Set<Rota> rotas = rotaService.getRotas(p1, p2, distanciaMaxima);
        return rotas;
    }

}
