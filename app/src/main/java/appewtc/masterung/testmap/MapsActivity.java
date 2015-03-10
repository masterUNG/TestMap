package appewtc.masterung.testmap;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double douLatEWTC = 13.667646, doulngEWTC = 100.621740;
    private LatLng objLatLngBangna14, objLatLngBTS, objLatLngMobile;

    private double douLatMobile, douLngMobile;
    private LocationManager objLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Setup LatLng
        setupLatLng();

        setUpMapIfNeeded();

        objLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check Status
        checkStatus();

    }   // onCreate

    public Location requestProvider(final String strPrivider, String strError) {
        Location objLocation = null;
        if (objLocationManager.isProviderEnabled(strPrivider)) {
            objLocationManager.requestLocationUpdates(strPrivider, 1000, 10, objLocationListener);
            objLocation = objLocationManager.getLastKnownLocation(strPrivider);
        } else {
            Log.d("master", "Error ==> " + strError);
        }
        return objLocation;
    }

    public final LocationListener objLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            douLatMobile = location.getLatitude();
            douLngMobile = location.getLongitude();
            Log.d("master", "Lat == Lng" + Double.toString(douLngMobile));
        }   // onLocationChange

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void checkStatus() {
        if (objLocationManager == null) {
            Toast.makeText(MapsActivity.this, "Device not Support Location", Toast.LENGTH_SHORT).show();
            finish();
        }
    }   // checkStatus

    private void setupLatLng() {

        objLatLngBangna14 = new LatLng(13.669442, 100.623291);
        objLatLngBTS = new LatLng(13.668212, 100.605009);
        objLatLngMobile = new LatLng(douLatMobile, douLngMobile);


    }   // setupLatLng

    @Override
    protected void onStart() {
        super.onStart();
        boolean bolGPS = objLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean bolNetwork = objLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!bolGPS && !bolNetwork) {
            Intent objIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            Log.d("master", "Cannot Connected");
        } else {
            Log.d("master", "Connected");
        }

    }   // onStart

    @Override
    protected void onStop() {
        super.onStop();
        if (objLocationManager != null) {
            objLocationManager.removeUpdates(objLocationListener);
        }
    }   // onStop

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUp();
    }   // onResume

    public void setUp() {
        objLocationManager.removeUpdates(objLocationListener);
        Location networkLocation = requestProvider(LocationManager.NETWORK_PROVIDER, "Network Error");
        if (networkLocation != null) {
            douLatMobile = networkLocation.getLatitude();
            douLngMobile = networkLocation.getLongitude();
        }
        Location gpsLocation = requestProvider(LocationManager.GPS_PROVIDER, "GPS Error");
        if (gpsLocation != null) {
            douLatMobile = gpsLocation.getLatitude();
            douLngMobile = gpsLocation.getLongitude();
        }
    }   // setUp

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            //Move Camera to EWTC
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(douLatEWTC, doulngEWTC), 15));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(douLatMobile, douLngMobile), 15));

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }   // if2
        }   // if1
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(douLatEWTC, doulngEWTC)).title("สถาบัน EWTC").snippet("อบรมแอนดรอยด์ กับ มาสเตอร์ อึ่ง"));

        // for Bangna14
        mMap.addMarker(new MarkerOptions().position(objLatLngBangna14).title("ซอย บางนา-ตราด 14").snippet("จะเห็นป้ายหมู่บ้านถาวรนิเวศน์"));

        // for BTS
        mMap.addMarker(new MarkerOptions().position(objLatLngBTS).title("BTS").snippet("กรมอุตุ"));

        mMap.addMarker(new MarkerOptions().position(objLatLngMobile).title("Mobile"));

    }   // setUpMap

}   // Main Class
