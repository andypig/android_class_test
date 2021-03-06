package com.example.diva.simpleui;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by DIVA on 2016/3/10.
 */
public class Utils {

    public static void writeFile(Context context, String fileName, String content) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(content.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            fis.read(buffer, 0, buffer.length);
            fis.close();
            return new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

//    public static Uri getPhotoUri() {
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        if (dir.exists() == false) {
//            dir.mkdir();
//        }
//        File file = new File(dir, "simple_photo.png");
//        return Uri.fromFile(file);
//    }

    public static Uri getPhotoUri() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (dir.exists() == false) {
            dir.mkdir();
        }

        File file = new File(dir, "simple_photo.png");
        return Uri.fromFile(file);
    }

    public static byte[] uriToBytes(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream base = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                base.write(buffer, 0, len);
            }
            return base.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] urlToBytes(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getGeoCodingURL(String address){
        try {
            address = URLEncoder.encode(address, "utf-8"); // 先把url轉成utf-8格式，在串上API
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + address;
        return url;
    }


    public static double[] getLatLngFromJsonString(String jsonString){
        try {
            JSONObject object = new JSONObject(jsonString);

//            if (!object.getString("status").equals("OK")){
//                return null;

                JSONObject location = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                return new double[]{lat, lng};

            } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    // 把前面的Thread thread = new Thread(new Runnable()裡面的run拉過來這邊寫成function去call他
    public static double[] addressToLatLng(String address) {
        String url = Utils.getGeoCodingURL(address);
        byte[] bytes = Utils.urlToBytes(url);
        String result = new String(bytes);

        double[] locations = Utils.getLatLngFromJsonString(result);
        //String debugLog = "lat" + String.valueOf(locations[0]) + "lng" + String.valueOf(locations[1]);
        //Log.d("debug", result); //亂碼，因把url轉成string
        //Log.d("debug", "debugLog :" + debugLog);

        return locations;
    }

    public static String getStaticMapUrl(double[] latlng, int zoom){
        String center = latlng[0] + "," + latlng[1];
        String url = "http://maps.googleapis.com/maps/api/staticmap?center=" + center + "&zoom=" + zoom + "&size=640x400";
        return url;
    }
}

