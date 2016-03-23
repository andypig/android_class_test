package com.example.diva.simpleui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DrinkMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
    }

    public void add(View view)
    {
        Button button = (Button) view;// 把整個view內容丟進來，然後強制轉型成Button，因為button的副類別就是View，所以可以這樣轉型。

        int number = Integer.parseInt(button.getText().toString());// 取得畫面上按鈕裡的值，然後存進區域變數number裡面，供下面計算
        // int number不是物件，Integer是中間值，把右邊是物件的東西，轉成不是物件去做++，最後在轉回去物件

        number ++;

        button.setText(String.valueOf(number));// 把遞增完的number轉成int(因為先轉成string去做操作)後再存回button去顯示其text內容
    }

    public void cancel(View view)
    {
        finish(); // 結束現在的Activity(即將他onPause->onStop->onDestory)，退回到原本被onStop的前一個Activity
    }

    public void done(View view)
    {
        // 當按下確認後，JSON物件回傳
        JSONArray array = getData();


        Intent data = new Intent();

        // 下面是KEY, VALUE的概念
        // 把result這個KEY放入一個陣列的資料，這個陣列叫做array，但是原本的array是JSON，所以說要把array轉成String，
        // 之後到MainActivity去就可以用在onActivityResult這個方法裡，
        // 用textView.setText(data.getStringExtra("result"));去取得String的格式資料。
        data.putExtra("result",array.toString()); // 似SharedPreferencese功能

        //data.putExtra("drinkNum",array.toString());


        // 按下done之後把資料抓出來，給予name, lNumber, mNumber這三種資料一個名為result的key值，
        // 這邊再把抓出來的資料加上一個result code後包再一起成為data
        // 然後用setResult把data跟result code打包再一起後回傳給onActivityResult
        // 所以如果有result code的使用就要用startActivityForResult丟進intent跟request code
        // 然後用setResult來設定回傳的result code與資料
        setResult(RESULT_OK, data);

        finish();
    }

    /*private int drinkCount()
    {
        int lCount;
        int mCount;

        lCount = 0;
        mCount = 0;



        JSONArray array = getData();

        for(int i =0; i < array.length(); i++) {
            try {
                //建立一個json格式的物件
                JSONObject object = new JSONObject();



                //把上面取得的資料，利用KEY, VALUE的JSON格式定義，分別塞進去name, lNumber, mNumber這三個key所代表的空間裡。
                object.put("name", drinkName);
                object.put("lNumber", lNumber);
                object.put("mNumber", mNumber);

                // 再把JSON格式的物件丟到一開始宣告的JSON格式的陣列裡面
                array.put(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }*/

    public JSONArray getData()
    {
        // 透過id先取得整個DrinkMenuActivity的Layout
        LinearLayout rootLinearLayout = (LinearLayout)findViewById(R.id.root);
        // 計算Layout上面的子物件數量，為了要抓出前三排裡面的資料，這邊指的是下一層的四行物件
        int count = rootLinearLayout.getChildCount();

        JSONArray array = new JSONArray();

        //抓出每一行裡面的子物件裡的資料，即飲料名稱跟飲料大杯中杯數量
        for(int i=0; i < count - 1; i++) // 只需三個，最後一排不用，所以不用小於等於
        {
            // i為第0排到第2排
            LinearLayout l1 = (LinearLayout)rootLinearLayout.getChildAt(i);
            //抓出每一排的飲料名稱，還有後面中杯跟大杯的數量
            //getChildAt(0)~getChildAt(2) 指的是每一行裡面的第1個到第3個子物件
            TextView drinkNameTextView = (TextView)l1.getChildAt(0);
            Button lButton = (Button)l1.getChildAt(1);
            Button mButton = (Button)l1.getChildAt(2);

            //分別抓出每一行裡的子物件的資料，即飲料名稱跟飲料數量
            String drinkName = drinkNameTextView.getText().toString();
            int lNumber = Integer.parseInt(lButton.getText().toString());
            int mNumber = Integer.parseInt(mButton.getText().toString());

            // 建立一個JSON物件把資料包起來
            try{
                //建立一個json格式的物件
                JSONObject object = new JSONObject();

                //把上面取得的資料，利用KEY, VALUE的JSON格式定義，分別塞進去name, lNumber, mNumber這三個key所代表的空間裡。
                object.put("name", drinkName);
                object.put("lNumber", lNumber);
                object.put("mNumber", mNumber);

                // 再把JSON格式的物件丟到一開始宣告的JSON格式的陣列裡面
                array.put(object);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //回傳整個JSON格式的陣列到MainActivity，接續上面的done方法
        //也就是當按下done後要去把資料打包起來回傳到MainActivity
        return array;
    }



    // Activity 生命週期 Check
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "Drink Menu onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "Drink Menu onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "Drink Menu onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "Drink Menu onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "Drink Menu onRestart");
    }
}
