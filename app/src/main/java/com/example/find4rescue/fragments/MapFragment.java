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
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
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
import com.example.find4rescue.R;
import com.example.find4rescue.activities.MainActivity;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        ArcGISRuntimeEnvironment.setApiKey("AAPKa9ab0f4101a64687a9607a6ae1476ac8hpV0htXjE3VxcH9PqDX2h0SK83oRgb23gZnKJvI0hpWQwFnx7aUPZ8nQ2cXr8U7f");


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

//        String path = getContext().getExternalFilesDir(null) + "/Parcels.shp";
//        File file = new File(path);
//
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found exception: " + e);
//            e.printStackTrace();
//        }
//
//        if (file.exists()) {
//            Log.d(TAG, "File exists: " + file.getAbsolutePath() + " " + file.getName());
//            Log.d(TAG, "File length: " + file.length());
//            Log.d(TAG, "File is directory?: " + file.isDirectory());
//            Log.d(TAG, "File isFile: " + file.isFile());
//            Log.d(TAG, "File readable?: " + file.canRead());
//            Log.d(TAG, "File extension: " + file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")));
//            Log.d(TAG, "File Free Space: " + file.getFreeSpace());
//        } else {
//            Log.d(TAG, "Path doesn't exist!");
//        }


        // inflate MapView from layout
        mapView = view.findViewById(R.id.mapView);
        // create a map with the BasemapType topographic
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
        // set the map to be displayed in this view
        mapView.setMap(map);


        String filepath = getContext().getExternalFilesDir(null) + "/V700_Wisconsin_Parcels_OZAUKEE.shp";
        Log.d(TAG, getContext().getExternalFilesDir(null) + "/V700_Wisconsin_Parcels_OZAUKEE.shp");

        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(getContext().getExternalFilesDir(null) + "/V700_Wisconsin_Parcels_OZAUKEE.shp");
        shapefileFeatureTable.loadAsync();

        Log.d(TAG, "" + shapefileFeatureTable.getFields().get(0).getName() + " " + shapefileFeatureTable.getFields().get(1).getName() + " " + shapefileFeatureTable.getTotalFeatureCount());
        Log.d(TAG, shapefileFeatureTable.getPath() + "|" + shapefileFeatureTable.getLoadStatus()+"|"+shapefileFeatureTable.getLoadError());


        shapefileFeatureTable.addDoneLoadingListener(() -> {
            Log.d(TAG, "listener hello!");
            if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                // zoom the map to the extent of the shapefile
                FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);
                Log.d(TAG, "Viewpoint: " + featureLayer.getFullExtent());
                // create the Symbol
                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
                SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0, lineSymbol);

                // create the Renderer
                SimpleRenderer renderer = new SimpleRenderer(fillSymbol);

                // set the Renderer on the Layer
                featureLayer.setRenderer(renderer);
                mapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()));
                Log.d(TAG, "Viewpoint zoomed in!");
                // use the shapefile feature table to create a feature layer
                mapView.getMap().getOperationalLayers().add(featureLayer);
                Log.d(TAG, "ShapefileFeatureTable: " + shapefileFeatureTable.getField("LONGITUDE").getFieldType());
                SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, Color.BLUE, 14);
                GraphicsOverlay overlay = new GraphicsOverlay();
                mapView.getGraphicsOverlays().add(overlay);
                overlay.getGraphics().add(new Graphic(createPoint(), markerSymbol));
                //mapView.setViewpointGeometryAsync(createEnvelope(), getResources().getDimension(R.dimen.viewpoint_padding));


            } else {
                String error = "Shapefile feature table failed to load: " + shapefileFeatureTable.getLoadError().toString();
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                Log.d(TAG, error);
                Log.d(TAG, shapefileFeatureTable.getPath() + "|" + shapefileFeatureTable.getLoadStatus()+"|"+shapefileFeatureTable.getLoadError());
            }
        });

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getContext()));
        }
        Python py = Python.getInstance();
        final PyObject pyobj = py.getModule("ShapefileParser");

        if (getArguments() != null) {
            if (getArguments().get("address") != null) {
                String address = getArguments().getString("address");
                Log.d(TAG, "Address: " + address);
                PyObject obj = pyobj.callAttr("findAddress", filepath, address);
                Log.d(TAG, "Successfull run: " + obj.toString() + " " + address);
            }
        }

    }

    private Envelope createEnvelope() {
        Envelope envelope = new Envelope(692477.7350706644, 326871.9862984028, 692542.7400737517, 326937.90910153463, SpatialReferences.getWgs84());
        return envelope;
    }

    private Point createPoint() {
        //[DocRef: Name=Create Point, Category=Fundamentals, Topic=Geometries]
        // create a Point using x,y coordinates and a SpatialReference
        Point pt = new Point(-87.87, 43.3953, SpatialReferences.getWgs84());
        //[DocRef: END]

        return pt;
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