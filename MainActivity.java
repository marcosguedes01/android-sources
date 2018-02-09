package com.programacaobrasil.testelocalizacaomaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity
        implements LocationListener, View.OnClickListener, OnMapReadyCallback {

    protected LocationManager locationManager;
    protected LocationListener locationListener;

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;
    private TextView txtLat;
    private Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLat = (TextView) findViewById(R.id.textview1);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.button1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                Geocoder geocoder1 = new Geocoder(MainActivity.this);

                                try {
                                    List<Address> lst = geocoder1.getFromLocation(location.getLatitude(),
                                            location.getLongitude(), 1);

                                    Address address = lst.get(0);
                                    String endereco =
                                            address.getThoroughfare() + " " +
                                                    address.getSubAdminArea() + " " +
                                                    address.getAdminArea() + " " +
                                                    address.getCountryName();

                                    Toast.makeText(getApplicationContext(),
                                            endereco, Toast.LENGTH_SHORT).show();

                                    //mMap.addMarker(new MarkerOptions().position(temp).title("Minha Localização: " + endereceo));
                                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
                                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 12.0f));
                                }
                                catch (Exception ex){}

                                /*Toast.makeText(getApplicationContext(),
                                        "Latitude: " + location.getLatitude() +
                                                " Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();*/
                            }
                        }
                    });
        }
    }

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
                                Geocoder geocoder1 = new Geocoder(MainActivity.this);

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