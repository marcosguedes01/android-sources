package com.programacaobrasil.testelocalizacaomaps;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> result1 = geocoder.getFromLocationName("Avenida Quatro, Vila Rica, Jaboatao dos Guararapes, PE", 10);
            List<Address> result2 = geocoder.getFromLocationName("Rua Via Principal, Jaboatao dos Guararapes, PE", 10);

            mMap = googleMap;
            PolylineOptions rectOptions = new PolylineOptions();
            rectOptions.color(Color.RED);

            for(Address address: result1) {
                LatLng temp = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(temp).title(address.getThoroughfare()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 12.0f));
                rectOptions.add(temp);
            }

            for(Address address: result2) {
                LatLng temp = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(temp).title(address.getThoroughfare()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 12.0f));
                rectOptions.add(temp);
            }

            Polyline polyline = mMap.addPolyline(rectOptions);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
                                Geocoder geocoder1 = new Geocoder(MapsActivity.this);

                                try {
                                    List<Address> lst = geocoder1.getFromLocation(location.getLatitude(),
                                            location.getLongitude(), 1);

                                    Address address = lst.get(0);
                                    String endereceo =
                                            address.getThoroughfare() + " " +
                                            address.getSubAdminArea() + " " +
                                            address.getAdminArea() + " " +
                                            address.getCountryName();

                                    //mMap.addMarker(new MarkerOptions().position(temp).title("Minha Localização: " + endereceo));
                                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
                                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 12.0f));
                                }
                                catch (Exception ex){}
                            }
                        }
                    });

            mMap.setMyLocationEnabled(true);

            // Add a marker in Sydney and move the camera
            //LatLng sydney = new LatLng(-34, 151);
            //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
        catch (Exception e){}
    }
}