package com.example.diva.simpleui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
        Intent data = new Intent();

        data.putExtra("result","order done"); // 似SharedPreferencese功能

        setResult(RESULT_OK, data);

        finish();
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
