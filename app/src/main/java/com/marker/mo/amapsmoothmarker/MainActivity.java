package com.marker.mo.amapsmoothmarker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * author: Ivan Zhang
 * �ߵµ�ͼ��ͼ��ƽ���ƶ�����Demo
 */
public class MainActivity extends AppCompatActivity {

    MapView mMapView = null;
    AMap aMap;

    private double[] coords;//·�����������,������ո���
    private List<LatLng> carsLatLng;//��̬����������
    private List<LatLng> goLatLng;
    private List<Marker> showMarks;//��̬����ͼ��
    private List<SmoothMoveMarker> smoothMarkers;//ƽ���ƶ�ͼ�꼯��
    //����
    private double lng = 0.0;
    //γ��
    private double lat = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //��ȡ��ͼ�ؼ�����
        mMapView = (MapView) findViewById(R.id.map);
        //��activityִ��onCreateʱִ��mMapView.onCreate(savedInstanceState)��������ͼ
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        initView();
        initData();
    }

    private void initView() {
        Button put = (Button) findViewById(R.id.put);
        Button run = (Button) findViewById(R.id.run);

        //���뾲̬����
        put.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //��յ�ͼ������
                if (smoothMarkers != null) {//��ն�̬marker
                    for (int i = 0; i < smoothMarkers.size(); i++) {
                        smoothMarkers.get(i).destroy();
                    }
                }
                //����ɼ���
                if (showMarks == null) {
                    showMarks = new ArrayList<Marker>();
                }
                for (int j = 0; j < showMarks.size(); j++) {
                    showMarks.get(j).remove();
                }
                //���η��뾲̬ͼ��
                for (int i = 0; i < carsLatLng.size(); i++) {
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car_up);
                    lng = Double.valueOf(carsLatLng.get(i).longitude);
                    lat = Double.valueOf(carsLatLng.get(i).latitude);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(lat, lng))
                            .icon(icon);
                    showMarks.add(aMap.addMarker(markerOptions));
                    Animation startAnimation = new AlphaAnimation(0, 1);
                    startAnimation.setDuration(600);
                    //�������о�ֹ���ĽǶ�
//                            showMarks.get(i).setRotateAngle(Float.valueOf(listBaseBean.datas.get(i).angle));
                    showMarks.get(i).setAnimation(startAnimation);
                    showMarks.get(i).setRotateAngle(new Random().nextInt(359));
                    showMarks.get(i).startAnimation();
                }
            }
        });

        /**
         * չʾƽ���ƶ�����
         */
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smoothMarkers != null) {//��ն�̬marker
                    for (int i = 0; i < smoothMarkers.size(); i++) {
                        smoothMarkers.get(i).destroy();
                    }
                }
                //����ɼ���
                if (showMarks == null) {
                    showMarks = new ArrayList<Marker>();
                }
                //�����̬marker
                for (int j = 0; j < showMarks.size(); j++) {
                    showMarks.get(j).remove();
                }
                smoothMarkers = null;//��վ�����
                smoothMarkers = new ArrayList<SmoothMoveMarker>();
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car_up);
                //ѭ��
                for (int i = 0; i < carsLatLng.size(); i++) {
                    //����·��
                    double[] newoords = {Double.valueOf(carsLatLng.get(i).longitude), Double.valueOf(carsLatLng.get(i).latitude),
                            Double.valueOf(goLatLng.get(i).longitude), Double.valueOf(goLatLng.get(i).latitude)};
                    coords = newoords;
                    //�ƶ�����
                    movePoint(icon);
                }
            }
        });
    }

    private void initData() {
        //������
        LatLng car1 = new LatLng(39.902138, 116.391415);
        LatLng car2 = new LatLng(39.935184, 116.328587);
        LatLng car3 = new LatLng(39.987814, 116.488232);
        //���������꼯��
        carsLatLng = new ArrayList<>();
        carsLatLng.add(car1);
        carsLatLng.add(car2);
        carsLatLng.add(car3);

        //Ŀ�ĵ�
        LatLng go1 = new LatLng(39.96782, 116.403775);
        LatLng go2 = new LatLng(39.891225, 116.322235);
        LatLng go3 = new LatLng(39.883322, 116.415619);
        //Ŀ�ĵ����꼯��
        goLatLng = new ArrayList<>();
        goLatLng.add(go1);
        goLatLng.add(go2);
        goLatLng.add(go3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()�����ٵ�ͼ
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //��activityִ��onResumeʱִ��mMapView.onResume ()�����»��Ƽ��ص�ͼ
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //��activityִ��onPauseʱִ��mMapView.onPause ()����ͣ��ͼ�Ļ���
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //��activityִ��onSaveInstanceStateʱִ��mMapView.onSaveInstanceState (outState)�������ͼ��ǰ��״̬
        mMapView.onSaveInstanceState(outState);
    }

    //ƽ���ƶ�
    public void movePoint(BitmapDescriptor bitmap) {
        // ��ȡ�켣�����
        List<LatLng> points = readLatLngs();
//        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
//        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

//        SparseArrayCompat sparseArrayCompat = new SparseArrayCompat();//�ȸ��¼��ϣ����hashmap
        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
        smoothMarkers.add(smoothMarker);
        int num = smoothMarkers.size() - 1;
        // ���û�����ͼ��
        smoothMarkers.get(num).setDescriptor(bitmap);
        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // ���û����Ĺ켣��ߵ�
        smoothMarkers.get(num).setPoints(subList);
        // ���û�������ʱ��
        smoothMarkers.get(num).setTotalDuration(10);
        // ��ʼ����
        smoothMarkers.get(num).startSmoothMove();
    }

    //��ȡ·��
    private List<LatLng> readLatLngs() {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < coords.length; i += 2) {
            points.add(new LatLng(coords[i + 1], coords[i]));
        }
        return points;
    }
}
