package com.example.ooar.discountproject.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onur Kuru on 31.3.2016.
 */
public class GoogleMapsFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    LocationClient mLocationClient;
    private static final int REQUEST_CODE_LOCATION = 2;
    public static GoogleMap googleMap = null;
    private static boolean restartFragment = false;
    private static boolean locationEnabled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "onCreateView", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.google_maps_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        checkPermissionAndConnectGoogle();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (restartFragment) {
            restartFragment = false;
            checkPermissionAndConnectGoogle();
        }
    }

    @Override
    public void onDestroyView() {
        Toast.makeText(getActivity(), "onDestroyView", Toast.LENGTH_SHORT).show();
        super.onDestroyView();
        mLocationClient.disconnect();
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                mLocationClient = new LocationClient(this.getActivity(), this, this);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationEnabled = true;
                } else {
                    locationEnabled = false;
                }
                mLocationClient.connect();
                return;
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

        Location currentLocation;
        LatLng branchCoordinates;
        LatLngBounds.Builder lBuilder;
        LatLng myCoordinates;
        List<LatLng> latLngList;
        Marker currentMarker;
        String latlng = getArguments().getString("url");
        String branchName = getArguments().getString("branchName");

        if (latlng != null) {
            double lat = Double.parseDouble(latlng.split(",")[0]);
            double lng = Double.parseDouble(latlng.split(",")[1]);
            branchCoordinates = new LatLng(lat, lng);
            try {
                googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
                Marker destinationMarker = googleMap.addMarker(new MarkerOptions().position(branchCoordinates).title(branchName));
                destinationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker));

                if (locationEnabled) {
                    if (checkLocationEnabled()) {
                        currentLocation = mLocationClient.getLastLocation();
                        if (currentLocation != null) {
                            lBuilder = new LatLngBounds.Builder();
                            myCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            lBuilder.include(branchCoordinates);
                            lBuilder.include(myCoordinates);
                            latLngList = new ArrayList<>();
                            latLngList.add(branchCoordinates);
                            latLngList.add(myCoordinates);
                            currentMarker = googleMap.addMarker(new MarkerOptions().position(myCoordinates).title("Benim Konumum"));
                            currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.current_marker));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(lBuilder.build(), 300));
                            String url = Util.getDirectionsUrl(myCoordinates, branchCoordinates);
                            Util.DownloadTask downloadTask = new Util.DownloadTask();
                            downloadTask.execute(url);
                        } else {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(branchCoordinates, 15));
                        }
                    } else {
                        checkPermissionAndConnectGoogle();
                    }
                } else {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(branchCoordinates, 15));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean checkLocationEnabled() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            locationEnabled = gps_enabled;
        } catch (Exception ex) {
        }

        return gps_enabled;
    }

    public void checkPermissionAndConnectGoogle() {
        mLocationClient = new LocationClient(this.getActivity().getApplicationContext(), this, this);
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (checkLocationEnabled()) {
                mLocationClient.connect();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Konumunuz kapalı!");
                dialog.setMessage("Konumunuzu açarak daha fazla bilgi elde edebilirsiniz.");
                dialog.setPositiveButton("Konum Ayarlarını Aç", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        restartFragment = true;
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Konumumu kullanmadan devam et", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        locationEnabled = false;
                        mLocationClient.connect();
                    }
                });
                dialog.show();
            }

        } else {
            if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            } else {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            }
        }
    }

}