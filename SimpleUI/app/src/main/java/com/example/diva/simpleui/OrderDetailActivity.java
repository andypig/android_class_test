package com.example.diva.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class OrderDetailActivity extends AppCompatActivity {

    TextView note;
    TextView storeInfo;
    TextView menu;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView) findViewById(R.id.noteView);
        storeInfo = (TextView) findViewById(R.id.storeInfoView);
        menu = (TextView) findViewById(R.id.menuView);
        photo = (ImageView) findViewById(R.id.photoView);

        note.setText(getIntent().getStringExtra("note"));
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

        String url = getIntent().getStringExtra("photoURL");
        if(url != null)
        Picasso.with(this).load(url).into(photo);
    }
}
