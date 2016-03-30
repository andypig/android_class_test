package com.example.diva.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailActivity extends AppCompatActivity {

    TextView note;
    TextView storeInfo;
    TextView menu;
    ImageView photo;
    ImageView staticMapImageView;
    WebView webView;
    Switch open;

    // 怕url在運行時會消失，所以用在class下得變數去接
    //private String url;
    //private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView) findViewById(R.id.noteView);
        storeInfo = (TextView) findViewById(R.id.storeInfoView);
        menu = (TextView) findViewById(R.id.menuView);
        photo = (ImageView) findViewById(R.id.photoView);
        staticMapImageView = (ImageView) findViewById(R.id.staticMapImageView);
        webView = (WebView) findViewById(R.id.webView);
        open = (Switch)findViewById(R.id.open);

        note.setText(getIntent().getStringExtra("note"));
        final String storeInformation= getIntent().getStringExtra("storeInfo");
        storeInfo.setText(getIntent().getStringExtra("storeInfo"));
        String menuResult = getIntent().getStringExtra("menu");

        try {
            JSONArray array = new JSONArray(menuResult);
            String text = "";
            for (int i = 0; i < array.length(); i++) {
                JSONObject order = array.getJSONObject(i);

                String name = order.getString("name");
                String lNumber = String.valueOf(order.getInt("lNumber"));
                String mNumber = String.valueOf(order.getInt("mNumber"));

                text = text + name + "l:" + lNumber + "m:" + mNumber + "\n";
            }
            menu.setText(text);// 把最後的text結果設定回去畫面上的textView
        }
        // 用來抓例外錯誤狀況
        catch (JSONException e) {
            e.printStackTrace();
        }


        String address = storeInformation.split(",")[1];

        new GeoCodingTask().execute(address);

        String url = getIntent().getStringExtra("photoURL");

        // 1. 原本寫法
        //上方的改寫，上面是簡單版，下面是抓url後去做拿圖片
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
////                String url = Utils.getGeoCodingURL(address);
////                byte[] bytes = Utils.urlToBytes(url);
////                String result = new String(bytes);
////
//                double[] locations = Utils.getLatLngFromJsonString(address);
//                String debugLog = "lat" + String.valueOf(locations[0]) + "lng" + String.valueOf(locations[1]);
//                Log.d("debug", "debugLog :" + debugLog);
//
//                // 用thread跑load string不好
//                //Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                //photo.setImageBitmap(bmp);
//            }
//        });
//        thread.start();
        //url = this.getIntent().getStringExtra("photoURL");
        if(url==null){
            return;
        }

        //url = getIntent().getStringExtra("photoURL");

        // 避免如果該筆紀錄沒有上傳圖片的話會當掉的狀況
        // 如果url沒有東西(表示沒有照片)，就直接return，不跑下面的顯示照片
        new ImageLoadingTask(photo).execute(url);


//        // 0. 偷工減料的方法，使用Picasso的資源來抓url並顯示照片
//        if(url != null)
//        Picasso.with(this).load(url).into(photo);
//



        // 2.改寫更好的方法:
        //用syncronyze thread 跑比較好，用好幾個thread包起來，並且用物件去拿url，跟一般的thread就差別在他有好幾個thread
//        new AsyncTask<String, Void, byte[]>() {
//            @Override
//            protected byte[] doInBackground(String... params) {
//                String url = params[0];
//                return Utils.urlToBytes(url);
//            }
//
//            @Override
//            protected void onPostExecute(byte[] bytes) {
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                photo.setImageBitmap(bmp);
//                super.onPostExecute(bytes);
//            }
//        }.execute(url);


//        作業2

        open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            //String address = storeInformation.split(",")[1];
            String url = getIntent().getStringExtra("photoURL");

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    staticMapImageView.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    //new GeoCodingTask().execute(address);
                } else {
                    staticMapImageView.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.INVISIBLE);
                    new ImageLoadingTask(photo).execute(url);
                }
            }
        });

    }

    class GeoCodingTask extends AsyncTask<String, Void, byte[]> {
        private double[] latlng;
        private String url;

        @Override
        protected byte[] doInBackground(String... params){
            String address = params[0];
            latlng = Utils.addressToLatLng(address);
            url = Utils.getStaticMapUrl(latlng, 17);
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes){
            webView.loadUrl(url);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImageView.setImageBitmap(bmp);
            super.onPostExecute(bytes);
        }
    }



    class ImageLoadingTask extends AsyncTask<String, Void, byte[]> {
        ImageView imageView;

        public ImageLoadingTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected byte[] doInBackground(String... params){
            String url = params[0];
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes){
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bmp);
            super.onPostExecute(bytes);
        }
    }
}

