package com.offapps.off.UI;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.offapps.off.Adapters.MallListAdapter;
import com.offapps.off.Data.Mall;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

/*
* Acquired from:
* https://parse.com/tutorials/anywall-android#1-overview
* &
* https://github.com/ParsePlatform/AnyWall/blob/master/AnyWall-android/Anywall/src/com/parse/anywall/MainActivity.java
*
* Created by:
* mrbm, caabernathy & mrkane27 (Unkown date)
*
* Edited by:
* carlosdiez on 8/26/15
*/

public class MapActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 10;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    /*
     * Constants for handling location results
     */

    // Maximum results returned from a Parse query
    private static final int MAX_MALL_SEARCH_RESULTS = 10;

    // Maximum post search radius for map in kilometers
    private static final int MAX_POST_SEARCH_DISTANCE = 100;

    /*
     * Other class member variables
     */
    // Map fragment
    private SupportMapFragment mapFragment;

    // Fields for the map radius in km
    private float radius;

    // Fields for helping process map and location changes
    private final Map<String, Marker> mapMarkers = new HashMap<>();
    private String mSelectedMallObjectId;
    private Location mLastLocation;
    private Location mCurrentLocation;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mLocationClient;

    private SearchView mSearchView;

    @InjectView(R.id.tool_bar) Toolbar mToolbar;
    @InjectView(R.id.mall_listview) ListView mMallListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        radius = 25;
        setContentView(R.layout.activity_map);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        // Enable the current location "blue dot"
        mapFragment.getMap().setMyLocationEnabled(true);
        // Set up the camera change handler
        mapFragment.getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                doMapQuery();
            }
        });

        //getMalls();

        isLocationEnabled();
    }

    public void getMalls() {
        final Location myLoc = (mCurrentLocation == null) ? mLastLocation : mCurrentLocation;
        ParseQuery<Mall> query = Mall.getQuery();
        query.whereWithinKilometers(ParseConstants.KEY_LOCATION, geoPointFromLocation(myLoc), radius);
        query.setLimit(MAX_MALL_SEARCH_RESULTS);
        query.findInBackground(new FindCallback<Mall>() {
            @Override
            public void done(final List<Mall> list, ParseException e) {
                if (e == null) {
                    MallListAdapter adapter = new MallListAdapter(MapActivity.this, list, myLoc);

                    mMallListView.setAdapter(adapter);

                    mMallListView.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final Mall item = list.get(position);
                            mSelectedMallObjectId = item.getObjectId();
                            Intent intent = new Intent(MapActivity.this, MallActivity.class);
                            intent.putExtra(ParseConstants.KEY_OBJECT_ID, mSelectedMallObjectId);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStop() {
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }
        mLocationClient.disconnect();

        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

        mLocationClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        doMapQuery();
        doListQuery();

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorPrimary));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);

        final MenuItem searchItem = menu.findItem(R.id.search_view);
        mSearchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = getLocation();
        startPeriodicUpdates();

        goToLocationOnMap();
    }

    private void goToLocationOnMap() {
        Location myLoc = (mCurrentLocation == null) ? mLastLocation : mCurrentLocation;
        if (myLoc != null) {
            //Go to Location
            mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(myLoc.getLatitude(), myLoc.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLoc.getLatitude(), myLoc.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mapFragment.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (mLastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(mLastLocation)) < 0.01) {
            // If the location hasn't changed by more than 10 meters, ignore it.
            return;
        }
        mLastLocation = location;

        doMapQuery();
        doListQuery();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
            }
        } else {
            //showErrorDialog(connectionResult.getErrorCode());
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Connection Failed!");
            builder.setPositiveButton("ACCEPT", null);
            builder.setMessage("Could not connect to Google Play services.");
            builder.show();
        }
    }

    private void doMapQuery() {
        Location myLoc = (mCurrentLocation == null) ? mLastLocation : mCurrentLocation;
        if (myLoc == null) {
            cleanUpMarkers(new HashSet<String>());
            return;
        }
        final ParseGeoPoint myPoint = geoPointFromLocation(myLoc);
        ParseQuery<Mall> mapQuery = Mall.getQuery();
        mapQuery.whereWithinKilometers(ParseConstants.KEY_LOCATION, myPoint, MAX_POST_SEARCH_DISTANCE);
        mapQuery.findInBackground(new FindCallback<Mall>() {
            @Override
            public void done(List<Mall> objects, ParseException e) {
                // Handle the results
                Set<String> toKeep = new HashSet<>();
                for (Mall mall : objects) {
                    toKeep.add(mall.getObjectId());
                    Marker oldMarker = mapMarkers.get(mall.getObjectId());
                    MarkerOptions markerOpts =
                            new MarkerOptions().position(new LatLng(mall.getLocation().getLatitude(), mall
                                    .getLocation().getLongitude()));
                    if (mall.getLocation().distanceInKilometersTo(myPoint) > radius) {
                        // Set up an out-of-range marker
                        if (oldMarker != null) {
                            if (oldMarker.getSnippet() == null) {
                                continue;
                            } else {
                                oldMarker.remove();
                            }
                        }
                        markerOpts =
                                markerOpts.title(mall.getName())
                                        .icon(BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED));
                    } else {
                        // Set up an in-range marker
                        if (oldMarker != null) {
                            if (oldMarker.getSnippet() != null) {
                                continue;
                            } else {
                                oldMarker.remove();
                            }
                        }
                        markerOpts =
                                markerOpts.title(mall.getName())
                                        .snippet(mall.getAddress())
                                        .icon(BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_GREEN));
                    }
                    Marker marker = mapFragment.getMap().addMarker(markerOpts);
                    mapMarkers.put(mall.getObjectId(), marker);
                    if (mall.getObjectId().equals(mSelectedMallObjectId)) {
                        marker.showInfoWindow();
                        mSelectedMallObjectId = null;
                    }
                }
                cleanUpMarkers(toKeep);
            }
        });
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            //Show error dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Connection Failed!");
            builder.setPositiveButton("ACCEPT", null);
            builder.setMessage("Could not connect to Google Play services.");
            builder.show();
            return false;
        }
    }

    /*
   * Helper method to clean up old markers
   */
    private void cleanUpMarkers(Set<String> markersToKeep) {
        for (String objId : new HashSet<>(mapMarkers.keySet())) {
            if (!markersToKeep.contains(objId)) {
                Marker marker = mapMarkers.get(objId);
                marker.remove();
                mapMarkers.get(objId).remove();
                mapMarkers.remove(objId);
            }
        }
    }

    private void doListQuery() {
        Location myLoc = (mCurrentLocation == null) ? mLastLocation : mCurrentLocation;
        if (myLoc != null) {
            getMalls();
        }
    }

    /*
   * Helper method to get the Parse GEO point representation of a location
   */
    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    private void startPeriodicUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
    }

    private void stopPeriodicUpdates() {
        mLocationClient.disconnect();
    }

    private Location getLocation() {
        if (servicesConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        } else {
            return null;
        }
    }

    private void isLocationEnabled() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Location services disabled!");
            builder.setPositiveButton("ACCEPT", null);
            builder.setMessage("Please enable your location services.");
            builder.show();
        }
    }
}
