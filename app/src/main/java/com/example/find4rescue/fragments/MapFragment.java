package com.example.find4rescue.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.AreaUnit;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.networkanalysis.Facility;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.example.find4rescue.R;
import com.example.find4rescue.activities.MainActivity;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    public static final String TAG = "MapFragment";
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 15;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 20;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 18;
    public static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 19;

    MapView mapView;
    TextView mapPIN;
    TextView mapSubdivision;
    TextView mapAddress;
    TextView mapDistance;
    ArcGISMap map;
    Switch stMap;
    ShapefileFeatureTable shapefileFeatureTable;
    GraphicsOverlay overlay;
    SimpleFillSymbol polyFillSymbol;
    SimpleLineSymbol polyLineSymbol;
    Geometry polygon;

    TextToSpeech textToSpeech;
    boolean isTextToSpeechInitialized;

    double currentLatitude;
    double currentLongitude;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkPermissions();
        ArcGISRuntimeEnvironment.setApiKey("AAPKa9ab0f4101a64687a9607a6ae1476ac8hpV0htXjE3VxcH9PqDX2h0SK83oRgb23gZnKJvI0hpWQwFnx7aUPZ8nQ2cXr8U7f");

        mapPIN = view.findViewById(R.id.mapPIN);
        mapSubdivision = view.findViewById(R.id.mapSubdivision);
        mapAddress = view.findViewById(R.id.mapAddress);
        mapView = view.findViewById(R.id.mapView);
        mapDistance = view.findViewById(R.id.mapDistance);
        map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

        mapView.setMap(map);
        mapPIN.setText("");
        mapSubdivision.setText("");
        mapAddress.setText("");
        mapDistance.setText("");

        mapPIN.setVisibility(View.INVISIBLE);
        mapSubdivision.setVisibility(View.INVISIBLE);
        mapAddress.setVisibility(View.INVISIBLE);
        mapDistance.setVisibility(View.INVISIBLE);

        loadParcels();

    }

    private List<Stop> getStops() {
        List<Stop> stops = new ArrayList<>(2);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        }

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Point currentLocation = new Point(locationGPS.getLatitude(), locationGPS.getLongitude(), SpatialReferences.getWgs84());
        stops.add(new Stop(currentLocation));

        String coordinates = getArguments().getString("coordinates");
        double givenLatitude = Double.parseDouble(coordinates.split(",")[0]);
        double givenLongitude = Double.parseDouble(coordinates.split(",")[1]);
        Point givenLocation = new Point(givenLatitude, givenLongitude, SpatialReferences.getWgs84());
        stops.add(new Stop(givenLocation));

        return stops;
    }

    private void loadParcels() {
        shapefileFeatureTable = new ShapefileFeatureTable(getContext().getExternalFilesDir(null) + "/parview.shp");
        shapefileFeatureTable.loadAsync();

        shapefileFeatureTable.addDoneLoadingListener(() -> {
            if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

                FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);
                SimpleLineSymbol pointLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
                SimpleFillSymbol pointFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0, pointLineSymbol);
                SimpleRenderer renderer = new SimpleRenderer(pointFillSymbol);
                featureLayer.setRenderer(renderer);

                SimpleMarkerSymbol polyMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, Color.BLUE, 14);
                polyFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, Color.BLUE, null);
                polyLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                overlay = new GraphicsOverlay();

                mapView.getGraphicsOverlays().add(overlay);
                mapView.getMap().getOperationalLayers().add(featureLayer);
                mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());

                if (getArguments() != null) {
                    if (getArguments().get("address") != null) {
                        String address = getArguments().getString("address");
                        address = address.toUpperCase();

                        featureLayer.clearSelection();
                        QueryParameters queryParameters = new QueryParameters();
                        queryParameters.setWhereClause("1=1");
                        ListenableFuture<FeatureQueryResult> result = shapefileFeatureTable.queryFeaturesAsync(queryParameters);
                        String finalAddress = address;
                        result.addDoneListener(() -> {
                            try {
                                // call get on the future to get the result
                                FeatureQueryResult queryResult = result.get();
                                Feature wanted_feature_ = queryResult.iterator().next();
                                for (Feature feature : queryResult) {
                                    if (Objects.equals(feature.getAttributes().get("ADDRESS1"), finalAddress)) {
                                        wanted_feature_ = feature;
                                        break;
                                    }
                                }
                                loadParcelInformation(wanted_feature_);
                                calculateCurrentDistance();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                String error = "Error retrieving list of features: " + e.getMessage();
                                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                                Log.e(TAG, error);
                            }

                        });


                    }
                }

            } else {
                String error = "Shapefile feature table failed to load: " + shapefileFeatureTable.getLoadError().toString();
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                Log.d(TAG, error);
                Log.d(TAG, shapefileFeatureTable.getPath() + "|" + shapefileFeatureTable.getLoadStatus() + "|" + shapefileFeatureTable.getLoadError());
            }
        });
    }

    private void calculateCurrentDistance() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Point currentLocation = new Point(locationGPS.getLatitude(), locationGPS.getLongitude(), SpatialReferences.getWgs84());

        String coordinates = getArguments().getString("coordinates");
        double givenLatitude = Double.parseDouble(coordinates.split(",")[0]);
        double givenLongitude = Double.parseDouble(coordinates.split(",")[1]);
        Point givenLocation = new Point(givenLatitude, givenLongitude, SpatialReferences.getWgs84());

        GeodeticDistanceResult distance = GeometryEngine.distanceGeodetic(currentLocation, givenLocation, new LinearUnit(LinearUnitId.MILES), new AngularUnit(AngularUnitId.DEGREES), GeodeticCurveType.GEODESIC);
        mapDistance.setText("Current Distance: " + distance.getDistance() + " miles");
    }

    private void loadParcelInformation(Feature feature) {

        polygon = GeometryEngine.project(feature.getGeometry(), SpatialReferences.getWgs84());

        String PIN = (String) feature.getAttributes().get("PIN");
        String subdivision = (String) feature.getAttributes().get("SUBDIVISIO");
        String address1 = (String) feature.getAttributes().get("ADDRESS1");
        String city = (String) feature.getAttributes().get("CITY");
        String state = (String) feature.getAttributes().get("STATE");
        String zipcode = (String) feature.getAttributes().get("ZIPCODE");

        mapPIN.setVisibility(View.VISIBLE);
        mapSubdivision.setVisibility(View.VISIBLE);
        mapAddress.setVisibility(View.VISIBLE);
        mapDistance.setVisibility(View.VISIBLE);

        mapSubdivision.setText("Subdivision: " + subdivision);
        mapPIN.setText("PIN: " + PIN);
        mapAddress.setText("Address: " + address1 + ", " + city + ", " + state + " " + zipcode);

        String[] coordinates = getCoordinates(polygon);
        overlay.getGraphics().add(new Graphic(createPolyline(coordinates), polyLineSymbol));
        overlay.getGraphics().add(new Graphic(createPolygon(coordinates), polyFillSymbol));
        mapView.setViewpointGeometryAsync(polygon);
    }

    private String[] getCoordinates(Geometry polygon) {
        String coords = polygon.toJson().split(":")[1];
        coords = coords.split("\"")[0];
        coords = coords.substring(2, coords.length()- 3);
        String[] coords2 = coords.split(",");
        return coords2;
    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(getContext(),
                ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "COARSE LOCATION: No permission at first");

            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_COARSE_LOCATION)) {
                Log.d(TAG, "COARSE LOCATION: Requesting permission");

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
        } else {
            // Permission has already been granted
            Log.d(TAG, "COARSE LOCATION: Permission already granted");
        }

        if (ContextCompat.checkSelfPermission(getContext(),
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "FINE LOCATION: No permission at first");

            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "FINE LOCATION: Requesting permission");

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }
        } else {
            // Permission has already been granted
            Log.d(TAG, "FINE LOCATION: Permission already granted");
        }

        if (ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "WRITE: No permission at first");

            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "WRITE: Requesting permission");

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            // Permission has already been granted
            Log.d(TAG, "WRITE: Permission already granted");
        }

        if (ContextCompat.checkSelfPermission(getContext(),
                READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "READ: No permission at first");

            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    READ_EXTERNAL_STORAGE)) {
                Log.d(TAG, "READ: Requesting permission");

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
            }
        } else {
            // Permission has already been granted
            Log.d(TAG, "READ: Permission already granted");
        }
    }

    private Polyline createPolyline(String[] coordinates) {
        PointCollection ncCorners = retrievePoints(coordinates);
        return new Polyline(ncCorners);
    }

    private Polygon createPolygon(String[] coordinates) {
        PointCollection ncCorners = retrievePoints(coordinates);
        return new Polygon(ncCorners);
    }

    private PointCollection retrievePoints(String[] coordinates) {
        PointCollection ncCorners = new PointCollection(SpatialReferences.getWgs84());
        for (int i = 0; i < coordinates.length; i = i + 2) {
            double lat = Double.parseDouble(coordinates[i].split("\\[")[1]);
            double lon = Double.parseDouble(coordinates[i + 1].split("\\]")[0]);
            Point mapPoint = new Point(lat, lon);
            ncCorners.add(mapPoint);
        }
        return ncCorners;
    }

    @Override
    public void onPause() {
        mapView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    public void onDestroy() {
        mapView.dispose();
        super.onDestroy();
    }
}