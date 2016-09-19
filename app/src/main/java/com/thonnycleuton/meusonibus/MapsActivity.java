package com.thonnycleuton.meusonibus;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.equalsp.stransthe.InthegraService;
import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Veiculo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    InthegraService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        service = new InthegraService("aa91935448534d519da1cda34d0b1ee4", "c2387331@trbvn.com", "c2387331@trbvn.com");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap = googleMap;

        try {
            List<Veiculo> veiculos = service.getVeiculos();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add a marker in Teresina and move the camera
        LatLng teresina = new LatLng(-5.101155, -42.755557);
        mMap.addMarker(new MarkerOptions().position(teresina).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(teresina));
    }
}
