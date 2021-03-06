  package com.shlompie.mimaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// classes needed to initialize map
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

// classes needed to add the location component
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;

// classes needed to add a marker
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

// classes needed to launch navigation UI
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


public class MapsFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {

    // Variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private Button searchBtn_map;
    private FloatingActionButton fab_favorite;

    private View view;

    private boolean useMetric; // Used to control the switch between metric and imperial system.

    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private LatLng landmarkLocation; // Location of the favourite landmark selected by the user in the landmarks page.

    public MapsFragment(LatLng landmarkLocation){
        this.landmarkLocation = landmarkLocation;
    } // Constructor for when the user selects a landmark as is returned to the map asset.

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));
        getUserPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            //finish(); // finish was giving an error
        }
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);
                initSearchFab();
                TrafficPlugin trafficPlugin = new TrafficPlugin(mapView, mapboxMap, style); // Initialising the traffic plugin.
                trafficPlugin.setVisibility(true); // This shows the traffic layer on the map.


                mapboxMap.addOnMapClickListener(MapsFragment.this::onMapClick); // not sure if this will work
                searchBtn_map = view.findViewById(R.id.startButton);
                fab_favorite = view.findViewById(R.id.fab_favorite);
                fab_favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(lastLoc != null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle("New Location");

                            final EditText input = new EditText(v.getContext());
                            input.setHint("Location Name");
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);

                            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // Handling Firestore
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("user_email", currentUser.getEmail());
                                    data.put("title", input.getText().toString());
                                    data.put("latitude", lastLoc.getLatitude());
                                    data.put("longitude", lastLoc.getLongitude());

                                    db.collection("saved_landmarks").document().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(view.getContext(), "Landmark Saved", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    }
                });

                // On click listener for the search button
                searchBtn_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean simulateRoute = true;
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute).shouldSimulateRoute(simulateRoute)
                                .build();

                        // Call this method with context from within an Activity
                        NavigationLauncher.startNavigation(getActivity(), options);
                    }
                });

                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        MapsFragment.this.getResources(), R.drawable.blue_marker_view));
                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);
                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);

                // This is handling when the user is sent to the map from the landmarks fragment after clicking to view one of their favourite landmarks.
                if(landmarkLocation != null){
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder()).target(landmarkLocation).zoom(14).build()));

                    // Generating the route to the users selected location.

                    // Building route to the searched location \\
                    Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                            locationComponent.getLastKnownLocation().getLatitude()); // Getting origin point.
                    // Getting landmark location LatLon
                    double lat = landmarkLocation.getLatitude();
                    double lon = landmarkLocation.getLongitude();
                    Point destinationPoint = Point.fromLngLat(lon,lat);
                    getRoute(originPoint, destinationPoint); // Generating the route to the users chosen landmark.

                    //addDestinationIconSymbolLayer(style); // adding the icon
                    // Enabling the user to start navigation to their selected favourite landmark.
                    searchBtn_map.setEnabled(true);
                    searchBtn_map.setBackgroundResource(R.color.mapboxBlue); // This color doesn't show, the theme overrides it.
                }
            }
        });
    }

    // Method to handle the search functionality.
    private void initSearchFab() {

        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude()); // Getting origin point. This is the user's current location.

        view.findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                        .placeOptions(PlaceOptions.builder()
                                .proximity(originPoint) // Bias results closer to user's location.
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10) // Limits search result to 10 locations.
                                .geocodingTypes("poi") // This is how you filter landmarks based on preference. MapBox deprecated methods so you can only search by POI now, filtering by landmark doesn't affect the data returned, its the same as filtering by poi.
                                .build(PlaceOptions.MODE_CARDS))
                        .build(getActivity());
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                 if(destinationSymbolLayer != null) mapboxMap.getStyle().removeLayer(destinationSymbolLayer);
            }
        });
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    // Result of searching a location using the search functionality.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);

                    // Building route to the searched location \\
                    Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                            locationComponent.getLastKnownLocation().getLatitude()); // Getting origin point.
                    // Getting carmen location LatLon
                    double lat = ((Point) selectedCarmenFeature.geometry()).latitude();
                    double lon = ((Point) selectedCarmenFeature.geometry()).longitude();
                    Point destinationPoint = Point.fromLngLat(lon,lat);
                    getRoute(originPoint, destinationPoint);
                }
            }
        }
    }


    SymbolLayer destinationSymbolLayer;
    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.blue_marker_view));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    LatLng lastLoc = null;

    @SuppressWarnings({"MissingPermission"})
    // If users haven't granted permissions - app requires fine location permissions granted
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);
        searchBtn_map.setEnabled(true);
        searchBtn_map.setBackgroundResource(R.color.mapboxBlue); // This color doesn't show, the theme overrides it.

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder()).target(point).zoom(14).build()));
        lastLoc = point;
        fab_favorite.setEnabled(true);

        return true;
    }

    // Get Route Method.
    private void getRoute(Point origin, Point destination) {
        //use the bool to alter the value of the string between metric and imperial
        String measurementSystem;
        if (useMetric)
        {
            measurementSystem = "metric";
        }
        else
        {
            measurementSystem = "imperial";
        }

        NavigationRoute.builder(getContext())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .voiceUnits(measurementSystem) // Allows for the switch between metric and imperial units.
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute(); // deprecated method might cause an issue for us removing the route.

                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(getContext(), loadedMapStyle); // Will this deprecation be an issue?
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING); // what is this for?
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    // Method to get user preferences from firebase.
    // User u = new User(); // Declaring a user object so we can get the users info from firebase.
    public void getUserPreferences(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user_preferences").whereEqualTo("user_email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();

                        useMetric = (Boolean) data.get("metric");
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        getUserPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        getUserPreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}