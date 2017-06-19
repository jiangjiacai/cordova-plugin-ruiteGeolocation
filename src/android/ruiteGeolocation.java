package com.ruite.location;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ruite.location.bean.Coords;
import com.ruite.location.bean.Position;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class ruiteGeolocation extends CordovaPlugin {

    private static final int requestCode = 1;

    private CallbackContext callbackContext;

    private LocationClient mLocationClient = null;

    private Context context;

    private JSONObject options;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        context = cordova.getActivity().getApplicationContext();
        this.callbackContext = callbackContext;
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if (action.equals("getCurrentPosition")) {
            if (args != null && args.length() != 0) {
                options = args.getJSONObject(0);
            }
            getCurrentLocation();
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void getCurrentLocation() throws JSONException {
        if (!cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            cordova.requestPermissions(this, requestCode, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            if (!isGpsOPen(context)) {
                // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
                if (options != null) {
                    if (options.getBoolean("showGpsPage")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity()).setTitle("通知").setMessage("请打开GPS用以获取精准位置信息,点击'确定'进行设置").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //此时没有打开位置开关可能定位失败
                                startLocationClient();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 转到手机设置界面，用户设置GPS
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                cordova.startActivityForResult(ruiteGeolocation.this, intent, 0); // 设置完成后返回到原来的界面
                            }
                        });
                        builder.create().show();
                    } else {
                        startLocationClient();
                    }
                }
            } else {
                startLocationClient();
            }
        }
    }

    private void startLocationClient() {
        mLocationClient = new LocationClient(context);
        initLocation(mLocationClient);
        BDLocationListener mBdLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mBdLocationListener);
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        switch (requestCode) {
            case ruiteGeolocation.requestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (!isGpsOPen(context)) {
                        // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
                        if (options != null) {
                            if (options.getBoolean("showGpsPage")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity()).setTitle("通知").setMessage("请打开GPS用以获取精准位置信息,点击'确定'进行设置").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //此时没有打开位置开关可能定位失败
                                        startLocationClient();
                                    }
                                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 转到手机设置界面，用户设置GPS
                                        Intent intent = new Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        cordova.startActivityForResult(ruiteGeolocation.this, intent, 0); // 设置完成后返回到原来的界面
                                    }
                                });
                                builder.create().show();
                            } else {
                                startLocationClient();
                            }
                        }
                    } else {
                        startLocationClient();
                    }
                } else {
                    callbackContext.error("获取权限失败");
                }
                break;
            default:
                break;
        }
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Coords coords = new Coords();
            coords.setLatitude(bdLocation.getLatitude());
            coords.setLongitude(bdLocation.getLongitude());
            coords.setAccuracy(bdLocation.getRadius());
            coords.setHeading(bdLocation.getDirection());
            coords.setSpeed(bdLocation.getSpeed());
            coords.setAltitude(bdLocation.getAltitude());
            Position position = new Position();
            position.setTimestamp(String.valueOf(System.currentTimeMillis()));
            position.setCoords(coords);
            position.setLocType(bdLocation.getLocType());
            callbackContext.success(position.toJSON());
            if (mLocationClient != null) {
                mLocationClient.stop();
                mLocationClient = null;
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private boolean isGpsOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case 0:
                startLocationClient();
                break;
            default:
                break;
        }
    }

    private void initLocation(LocationClient mLocationClient) {
        LocationClientOption option = new LocationClientOption();
        if (options == null) {
            try {
                boolean enableHighAccuracy = options.getBoolean("enableHighAccuracy");
                if (enableHighAccuracy) {
                    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                    //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
                } else {
                    option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
                }
                String bd09 = null;
                bd09 = options.getString("coorType");
                if (bd09 == null) {
                    option.setCoorType("bd09ll");
                    //可选，默认gcj02，设置返回的定位结果坐标系
                } else {
                    option.setCoorType(bd09);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
        }
        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

}
