package com.example.diva.simpleui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    ListView listView;
    Spinner spinner;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        hidecheckBox = (CheckBox)findViewById(R.id.checkBox);
        listView = (ListView)findViewById(R.id.listView);
        spinner = (Spinner)findViewById(R.id.spinner);

        sp = getSharedPreferences("setting", Context.MODE_PRIVATE); // 定義setting裡面的東西，供之後使用
        editor = sp.edit();

        editText.setText(sp.getString("editText", "")); // 取出sp裡面editText的內容，存回到editText

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                editor.putString("editText", editText.getText().toString());//把所key入的內容寫入到editText裡面，然後存起來
                editor.apply();

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) //前面定義按下去的按鈕為enter，後面定義時機
                {
                    submit(v);
                    return true;
                }
                return false;
            }

        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // 設定可以偵測虛擬鍵盤的輸入
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit(v);
                    return true;
                }

                return false;
            }
        });

        //抓取畫面上hidecheckbox物件的值，如果有被勾選就帶入true，如果沒有勾選就是預設為false，再存回sp裡面的hidecheckbox
        hidecheckBox.setChecked(sp.getBoolean("hideCheckbox", false));

        hidecheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckbox", hidecheckBox.isChecked());
                editor.apply();
            }
        });

        //使用 自定義的function setListView()
        setListView();
        setSpinner();
    }

    private void setListView()
    {
        //String[] data = {"1", "2", "3","4","5"};
        String[] data = Utils.readFile(this, "history.txt").split("\n");

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

        listView.setAdapter(adaptor);
    }

    private void setSpinner()
    {
        //String[] data = {"1", "2", "3","4","5"};
        //String[] data = Utils.readFile(this, "history.txt").split("\n");
        String[] data = getResources().getStringArray(R.array.storyInfo);

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);

        spinner.setAdapter(adaptor);
    }


    public void submit(View view)
    {
        //Toast.makeText(this, "Hello DUDE!!!", Toast.LENGTH_LONG).show();// 第一個參數把自己傳入，第二個是要顯示的東西，第三個是顯示的時間

        //textView.setText("Test Test Test!!!");

        String text = editText.getText().toString();

        Utils.writeFile(this, "history.txt", text + '\n');

        if(hidecheckBox.isChecked())
        {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();

            textView.setText("你到底輸入了甚麼??");
            editText.setText("不要問，很恐怖");

            //間接寫法，如此就不會再繼續往下執行，所以下面的else也不用加了!!!
            return;
        }
        //else {
        //    textView.setText(text);
        //    editText.setText("");
        //}

        //textView.setText(text);
        //editText.setText(""); //清除原本的輸入，也就是設為空字串

        // 把輸入的值隱藏起來，全部用*取代
        //textView.setText(text);
        //editText.setText("");

        textView.setText(text);
        editText.setText("");
        setListView();
    }

}
