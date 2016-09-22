package com.thonnycleuton.meusonibus;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.Veiculo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.thonnycleuton.meusonibus.inthegraAPI.AsyncTasks.InthegraVeiculosAsync;
import com.thonnycleuton.meusonibus.inthegraAPI.AsyncTasks.InthegraVeiculosAsyncResponse;
import com.thonnycleuton.meusonibus.inthegraAPI.InthegraService;
import com.thonnycleuton.meusonibus.util.GoogleMaps.ItemParadaClusterizavel;
import com.thonnycleuton.meusonibus.util.GoogleMaps.ParadaClusterRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, InthegraVeiculosAsyncResponse {

    private final String TAG = "DetailVeiculos";
    private Linha linha;
    private List<Parada> paradas;
    private List<Veiculo> veiculos;
    private List<Linha> linhas;
    private List<Marker> veiculosMarkers;
    private List<Marker> paradasMarkers;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            InthegraService.initInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Carrega os veículos */
        // carregarVeiculos();
    }

    /**
     * Carrega os veículos de maneira assíncrona
     */
    private void carregarVeiculos() {
        InthegraVeiculosAsync asyncTask =  new InthegraVeiculosAsync(MapsActivity.this);
        asyncTask.delegate = this;
        asyncTask.execute(linha);
    }

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();

        if (location != null || !location.equals("")) {
            try {
                linhas = InthegraService.getLinhas(location);
                linha = linhas.get(0);
                veiculos = InthegraService.getVeiculos(linha);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Cria novos marcadores de veículos */
            for (Veiculo v : veiculos) {
                LatLng pos = new LatLng(v.getLat(), v.getLong());
                MarkerOptions m = new MarkerOptions()
                        .position(pos)
                        .title(v.getHora())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_bus));
                veiculosMarkers.add(map.addMarker(m));
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady Called");
        map = googleMap;

        LatLng teresina = new LatLng(-5.0851617,-42.8037127);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(teresina, 12));

        map.addMarker(new MarkerOptions()
                .title("Teresina")
                .snippet("The most populous city in Australia.")
                .position(teresina));

    }

    /**
     * Atualiza o mapa com a posição atual dos veículos
     */
    private void updateMapa() {
        Log.d(TAG, "updateMapa Called");
        /* Exibe a mensagem na tela */
        Toast.makeText(MapsActivity.this, this.getString(R.string.atualizando_mapa), Toast.LENGTH_SHORT).show();
        /* Remove os marcadores de veículos existentes */
        for (Marker m : veiculosMarkers) {
            m.remove();
        }
        /* Limpa a lista de marcadores de veículos*/
        veiculosMarkers.clear();

        /* Cria novos marcadores de veículos */
        for (Veiculo v : veiculos) {
            LatLng pos = new LatLng(v.getLat(), v.getLong());
            MarkerOptions m = new MarkerOptions()
                    .position(pos)
                    .title(v.getHora())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_bus));
            veiculosMarkers.add(map.addMarker(m));
        }
    }

    @Override
    public void processFinish(List<Veiculo> result) {
        Log.d(TAG, "ProcessFinish Called");
        veiculos = result;
        /* Recupera e preenche o TextView de quantidade de veículos */
        TextView qtdVeiculosTxt = (TextView) findViewById(R.id.qtdVeiculosTxt);
        qtdVeiculosTxt.setText(String.valueOf(veiculos.size()));
        /* Atualiza o mapa */
        updateMapa();
    }
}
