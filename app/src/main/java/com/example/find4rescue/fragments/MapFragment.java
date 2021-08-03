package com.example.find4rescue.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.networkanalysis.Facility;
import com.example.find4rescue.R;
import com.example.find4rescue.activities.MainActivity;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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
    MapView mapView;
    ArcGISMap map;
    ShapefileFeatureTable shapefileFeatureTable;


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
        loadShapefile(view);


        shapefileFeatureTable.addDoneLoadingListener(() -> {
            if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

                FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);
                SimpleLineSymbol pointLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
                SimpleFillSymbol pointFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0, pointLineSymbol);
                SimpleRenderer renderer = new SimpleRenderer(pointFillSymbol);
                featureLayer.setRenderer(renderer);

                SimpleMarkerSymbol polyMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, Color.BLUE, 14);
                SimpleFillSymbol polyFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, Color.BLUE, null);
                SimpleLineSymbol polyLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                GraphicsOverlay overlay = new GraphicsOverlay();

                mapView.getGraphicsOverlays().add(overlay);
                mapView.getMap().getOperationalLayers().add(featureLayer);
                mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());

                if (getArguments() != null) {
                    if (getArguments().get("address") != null) {
                        String address = getArguments().getString("address");
                        address = address.toUpperCase();
                        address = "110 GLENMORE RD";
                        Log.d(TAG, "Address: " + address);

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
                                   if(Objects.equals(feature.getAttributes().get("ADDRESS1"), finalAddress)) {
                                       wanted_feature_ = feature;
                                       break;
                                   }
                                }
                                Geometry converted_feature_ = GeometryEngine.project(wanted_feature_.getGeometry(), SpatialReferences.getWgs84());

                                String coords = converted_feature_.toJson().split(":")[1];
                                coords = coords.split("\"")[0];
                                coords = coords.substring(2, coords.length()- 3);
                                String[] coords2 = coords.split(",");
                                overlay.getGraphics().add(new Graphic(createPolyline(coords2), polyLineSymbol));
                                overlay.getGraphics().add(new Graphic(createPolygon(coords2), polyFillSymbol));
                                mapView.setViewpointGeometryAsync(converted_feature_);
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
                Log.d(TAG, shapefileFeatureTable.getPath() + "|" + shapefileFeatureTable.getLoadStatus()+"|"+shapefileFeatureTable.getLoadError());
            }
        });

    }

    private void checkPermissions() {
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

    private void loadShapefile(View view) {
        ArcGISRuntimeEnvironment.setApiKey("AAPKa9ab0f4101a64687a9607a6ae1476ac8hpV0htXjE3VxcH9PqDX2h0SK83oRgb23gZnKJvI0hpWQwFnx7aUPZ8nQ2cXr8U7f");

        // inflate MapView from layout
        mapView = view.findViewById(R.id.mapView);
        // create a map with the BasemapType topographic
        map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
        // set the map to be displayed in this view
        mapView.setMap(map);

        shapefileFeatureTable = new ShapefileFeatureTable(getContext().getExternalFilesDir(null) + "/parview.shp");
        shapefileFeatureTable.loadAsync();
        Log.d(TAG, shapefileFeatureTable.getPath() + "|" + shapefileFeatureTable.getLoadStatus()+"|"+shapefileFeatureTable.getLoadError());
    }

    private Polyline createPolyline(String[] coords2) {
        PointCollection ncCorners = convertToLatLongCollection(coords2);
        return new Polyline(ncCorners);
    }

    private Polygon createPolygon(String[] coords2) {
        PointCollection ncCorners = convertToLatLongCollection(coords2);
        return new Polygon(ncCorners);
    }

    private PointCollection convertToLatLongCollection(String[] coords2) {
        PointCollection ncCorners = new PointCollection(SpatialReferences.getWgs84());
        for (int i = 0; i < coords2.length; i = i + 2) {
            double lat = Double.parseDouble(coords2[i].split("\\[")[1]);
            double lon = Double.parseDouble(coords2[i + 1].split("\\]")[0]);
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