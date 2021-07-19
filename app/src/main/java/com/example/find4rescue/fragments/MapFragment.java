package com.example.find4rescue.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.example.find4rescue.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

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
        mapView = view.findViewById(R.id.mapView);

        ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(42.4473, -71.2272, 72000.0));
        Log.i("MapFragment", "" + mapView.getMap());
        ArcGISRuntimeEnvironment.setApiKey("AAPKa9ab0f4101a64687a9607a6ae1476ac8hpV0htXjE3VxcH9PqDX2h0SK83oRgb23gZnKJvI0hpWQwFnx7aUPZ8nQ2cXr8U7f");

//        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(view.getExternalFilesDir(null) + "/M155Misc_CY21_FY21.shp");
//        FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);
//        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
//        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
//
//        SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
//        featureLayer.setRenderer(renderer);
//        map.getOperationalLayers().add(featureLayer);

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