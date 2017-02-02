package com.example.ihor.googlemaps;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;

import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihor.googlemaps.placesJson.PlacesJson;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private LocationManager locationManager;
    private TextView widthGPS;
    private TextView lengthGPS;
    private double latitude = 0;
    private double longitude = 0;
    private FloatingActionButton fbSattelite;
    private FloatingActionButton fbNormal;
    private FloatingActionButton fbHybird;
    private FloatingActionButton fbTerrain;
    private Places places;
    private Retrofit retrofit;
    private PlacesJson placesJsons;
    private TabLayout tabLayout;
    private SearchView svPlaces;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fbNormal = (FloatingActionButton) findViewById(R.id.fbNormal);
        fbHybird = (FloatingActionButton) findViewById(R.id.fbHybird);
        fbTerrain = (FloatingActionButton) findViewById(R.id.fbMyMarker);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvPlaces);
        placesJsons = new PlacesJson();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        places = retrofit.create(Places.class);
        initToolbar();
        initGPS();
        initNavigationView();
        initMapView();
        Places();
        initTab();
        initSearch();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {

        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            widthGPS.setText(String.valueOf(location.getLatitude()));
            lengthGPS.setText(String.valueOf(location.getLongitude()));

        }
    }


    private void initGPS() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);


    }

    private void initNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nvNavigation);
        View headerView = navigationView.getHeaderView(0);
        widthGPS = (TextView) headerView.findViewById(R.id.widthGPS);
        lengthGPS = (TextView) headerView.findViewById(R.id.lengthGPS);


    }

    private void initMapView() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        fbNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);


            }

        });
        fbHybird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        fbTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarkerMy(googleMap);

            }
        });
        addMarker(googleMap);
       googleMap.getUiSettings().setZoomControlsEnabled(true);


    }

    private void addMarkerMy(GoogleMap map) {
        if (!(latitude == 0)) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(15)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate);

            if (null != map) {
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("My")
                        .draggable(false)

                );
            }
        } else {
            Toast.makeText(getApplicationContext(), "Search for a place", Toast.LENGTH_SHORT).show();
        }


    }

    private void addMarker(final GoogleMap map) {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Friends")
                        .draggable(false));
                addPolylineMaps(new LatLng(latitude, longitude), latLng, map);


            }
        });
    }

    private void addPolylineMaps(LatLng my, LatLng friends, GoogleMap map) {
        map.addPolyline(new PolylineOptions().geodesic(true)
                .add(my)
                .add(friends)
        );
    }

    private void Places() {
        places.getData("kyiv", "(cities)", "AIzaSyB0vy3-cJTyWIUiQ_4fW4FzGi3sPw5f9yE").enqueue(new Callback<PlacesJson>() {
            @Override
            public void onResponse(Call<PlacesJson> call, Response<PlacesJson> response) {
                placesJsons = response.body();
            }

            @Override
            public void onFailure(Call<PlacesJson> call, Throwable t) {

            }
        });

    }

    private void initTab() {
        tabLayout = new TabLayout(MainActivity.this);
        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.taxi));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.subway));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.run));

    }

    private void initSearch() {
        svPlaces = (SearchView) findViewById(R.id.svPlaces);
        svPlaces.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tabLayout.setVisibility(View.GONE);
                initRecyclerView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

    }

    private void initRecyclerView() {
        String[] names = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис"
                 };
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PlacesAdapter(names);
        mRecyclerView.setAdapter(mAdapter);

    }
}


