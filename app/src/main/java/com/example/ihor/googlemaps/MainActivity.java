package com.example.ihor.googlemaps;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.ihor.googlemaps.placesJson.PlacesJson;
import com.example.ihor.googlemaps.placesJson.Prediction;
import com.example.ihor.googlemaps.placesJsonСoordinates.PlacesJsonСoordinates;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,DirectionCallback {

    @BindView(R.id.ibStartSeach)
    ImageButton ibStartSeach;
    @BindView(R.id.ibStopSeach)
    ImageButton ibStopSeach;
    @BindView(R.id.tilError)
    TextInputLayout tilError;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nvNavigation)
    NavigationView navigationView;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.actvPlaces)
    AutoCompleteTextView actvPlaces;
    @BindView(R.id.fbaddMarker)
    FloatingActionButton fbaddMarker;
    @BindView(R.id.fbNormal)
    FloatingActionButton fbNormal;
    @BindView(R.id.fbHybird)
    FloatingActionButton fbHybird;
    @BindView(R.id.fbMyMarker)
    FloatingActionButton fbMyMarker;
    private APIGoogle apiGoogle;
    private Retrofit retrofit;
    private PlacesJson placesJsons;
    private LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    private PlacesAdapter placesAdapter;
    private PlacesJsonСoordinates placesJsonСoordinates;
    GoogleMap googleMap;
    private DB db;
    private LatLng camera ;
    private LatLng origin ;
    private LatLng destination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        placesJsons = new PlacesJson();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL_PLACES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiGoogle = retrofit.create(APIGoogle.class);
        initGPS();
        initNavigationView();
        initMapView();
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

        }
    }

    private void initGPS() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }


    private void initNavigationView() {
        View headerView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuMyPlace) {
                    Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });
    }

    private void initMapView() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        fbNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        fbaddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(placesJsonСoordinates == null)) {
                    writeDB(placesJsonСoordinates.getResult().getName(), placesJsonСoordinates.getResult().getGeometry().getLocation().getLat(), placesJsonСoordinates.getResult().getGeometry().getLocation().getLng());
                }

            }
        });
        fbHybird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                requestDirection();
            }
        });
        fbMyMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarkerMy(googleMap);

            }
        });

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



    private void addMarkerPlaces(final String nameMarker) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(placesJsonСoordinates.getResult().getGeometry().getLocation().getLat(), placesJsonСoordinates.getResult().getGeometry().getLocation().getLng()))
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);

        if (null != googleMap) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(placesJsonСoordinates.getResult().getGeometry().getLocation().getLat(), placesJsonСoordinates.getResult().getGeometry().getLocation().getLng()))
                    .title(nameMarker)
                    .draggable(false)

            );
        }

    }



    private void initTab() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.taxi));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.subway));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.run));
    }

    private void initSearch() {
        ibStopSeach.setVisibility(View.GONE);
        actvPlaces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {

                if ((editable.length() > 3) && (editable.length() % 2 == 0)) {
                    apiGoogle.getPlaces(editable.toString(), "geocode", Constant.API_KEY).enqueue(new Callback<PlacesJson>() {
                        @Override
                        public void onResponse(final Call<PlacesJson> call, Response<PlacesJson> response) {

                            if (!(response == null)) {
                                placesJsons = response.body();
                                if (placesJsons.getStatus().equals("OK")) {
                                    Log.wtf("test", "OK");
                                    if (!(placesJsons == null)) {
                                        updatePlaceAdapter(placesJsons);
                                        Log.wtf("test", "updatePlaceAdapter");
                                    }
                                } else if (placesJsons.getStatus().equals("ZERO_RESULTS")) {
                                    Log.wtf("test", "Не знайдено");
                                    ibStartSeach.setVisibility(View.GONE);
                                } else if (placesJsons.getStatus().equals("OVER_QUERY_LIMIT")) {
                                    Log.wtf("test", "Ліміт");
                                    ibStartSeach.setVisibility(View.GONE);
                                    actvPlaces.setError("Ліміт!!!");


                                }
                            }
                            ibStopSeach.setVisibility(View.VISIBLE);
                            actvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                    actvPlaces.setText(placesJsons.getPredictions().get(i).getTerms().get(0).getValue());
                                    actvPlaces.setError(null);
                                    ibStartSeach.setVisibility(View.GONE);
                                    ibStopSeach.setVisibility(View.VISIBLE);
                                    apiGoogle.getPlacesСoordinates(placesJsons.getPredictions().get(i).getPlaceId(), Constant.API_KEY).enqueue(new Callback<PlacesJsonСoordinates>() {
                                        @Override
                                        public void onResponse(Call<PlacesJsonСoordinates> call, Response<PlacesJsonСoordinates> response) {
                                            if (!(response == null)) {
                                                placesJsonСoordinates = response.body();
                                                if (placesJsonСoordinates.getStatus().equals("OK")) {

                                                    if (!(placesJsonСoordinates == null)) {

                                                        addMarkerPlaces("test");
                                                        Log.wtf("test", "updatePlaceCoordinates");

                                                    }
                                                } else if (placesJsonСoordinates.getStatus().equals("ZERO_RESULTS")) {
                                                    Log.wtf("test", "Не знайдено");

                                                } else if (placesJsonСoordinates.getStatus().equals("OVER_QUERY_LIMIT")) {
                                                    Log.wtf("test", "Ліміт");

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<PlacesJsonСoordinates> call, Throwable t) {

                                            Log.wtf("test", "retrofit Error");
                                        }
                                    });


                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<PlacesJson> call, Throwable t) {
                            Log.wtf("test", "retrofit Error");
                        }
                    });
                }
            }
        });


        ibStartSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibStartSeach.setVisibility(View.GONE);
                ibStopSeach.setVisibility(View.VISIBLE);
                actvPlaces.setFocusableInTouchMode(true);
            }
        });
        ibStopSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvPlaces.setText("");
                actvPlaces.setError(null);
                actvPlaces.setFocusableInTouchMode(true);

            }
        });

        actvPlaces.setFocusable(false);
    }

    private void updatePlaceAdapter(PlacesJson placesJsons) {
        placesAdapter = new PlacesAdapter(MainActivity.this, placesJsons.getPredictions());
        actvPlaces.setAdapter(placesAdapter);
        placesAdapter.notifyDataSetChanged();

    }


    private void writeDB(String name, double latitude, double longitude) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase dbConnect = db.getInstance(this).getWritableDatabase();
        cv.put("name", name);
        cv.put("latitude", latitude);
        cv.put("longitude", longitude);
        long rowID = dbConnect.insert("mytable", null, cv);
        Log.wtf("test", "row inserted, ID = " + rowID);




    }
    public void requestDirection() {
        origin=new LatLng(latitude,longitude);
        destination=new LatLng(placesJsonСoordinates.getResult().getGeometry().getLocation().getLat(), placesJsonСoordinates.getResult().getGeometry().getLocation().getLng());

        GoogleDirection.withServerKey(Constant.API_KEY)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.TRANSIT)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            ArrayList<LatLng> sectionPositionList = direction.getRouteList().get(0).getLegList().get(0).getSectionPoint();
            for (LatLng position : sectionPositionList) {
                googleMap.addMarker(new MarkerOptions().position(position));
            }

            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(this, stepList, 5, Color.RED, 3, Color.BLUE);
            for (PolylineOptions polylineOption : polylineOptionList) {
                googleMap.addPolyline(polylineOption);
            }


        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }


}


