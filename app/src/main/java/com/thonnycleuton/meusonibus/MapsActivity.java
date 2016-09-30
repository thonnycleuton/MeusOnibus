package com.thonnycleuton.meusonibus;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.Veiculo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thonnycleuton.meusonibus.inthegraAPI.AsyncTasks.InthegraVeiculosAsync;
import com.thonnycleuton.meusonibus.inthegraAPI.AsyncTasks.InthegraVeiculosAsyncResponse;
import com.thonnycleuton.meusonibus.inthegraAPI.InthegraService;
import com.thonnycleuton.meusonibus.inthegraAPI.Util;

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


    private Handler UI_HANDLER = new Handler();
    private Runnable UI_UPDTAE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            carregarVeiculos();
            UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, Util.VEICULOS_REFRESH_TIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        veiculosMarkers = new ArrayList<>();

        try {
            InthegraService.initInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, Util.VEICULOS_REFRESH_TIME);
    }

    /**
     * Carrega os veículos de maneira assíncrona
     */
    private void carregarVeiculos() {
        InthegraVeiculosAsync asyncTask = new InthegraVeiculosAsync(MapsActivity.this);
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
                carregarVeiculos();
            } catch (IOException e) {
                e.printStackTrace();
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

        LatLng teresina = Util.TERESINA;
        //move o foco e a altitude para o ponto do mapa desejado
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(teresina, 12));

        //permite mostrar botao localizador acima a direita na tela
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
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
                    .title(v.getCodigoVeiculo())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_bus));
            veiculosMarkers.add(map.addMarker(m));
        }
        if (veiculos.size() > 0){
            //TODO: calcular media entre as posicoes dos veiculos a fim de centralizar equidistantemente
            //reposicionando a camera apos update
            LatLng carPosition = new LatLng(veiculos.get(0).getLat(), veiculos.get(0).getLong());
            //se mais de um  veiculo é encontrado, o zoom é mais amplo, se nao é mais centralizado
            float zoom = veiculos.size() == 1 ? 15 : 12;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(carPosition, zoom));
        }
    }

    @Override
    public void processFinish(List<Veiculo> result) {
        Log.d(TAG, "ProcessFinish Called");
        veiculos = result;
        updateMapa();
    }
}