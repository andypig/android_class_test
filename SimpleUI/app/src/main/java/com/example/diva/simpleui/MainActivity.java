package com.example.diva.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 0;
    private static final int REQUEST_CODE_CAMERA = 1;

    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    ListView listView;
    Spinner spinner;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String menuResult;
    List<ParseObject> queryResults;
    ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Parse連線設定
        // 要import Namespace之前要在gradle的dependency加入兩個compile檔案的source來源路徑
        // 之後右上角會出現sync now，要先sync完之後才能import Namespace
        // Parse Init:
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this);

        ParseObject testObject = new ParseObject("TestObject");
        //ParseObject testObject = new ParseObject("HomeworkParse");

        // 下面的key value改成自己的資訊
        testObject.put("hi", "Andypig");
        //testObject.put("sid", "And26311");
        //testObject.put("email", "a760405@gmail.com");

        // testObject.saveInBackground();
        // 將上面Parse的執行結果的error log拋出來
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("debug", e.toString());
                }
            }
        });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        hidecheckBox = (CheckBox)findViewById(R.id.checkBox);
        listView = (ListView)findViewById(R.id.listView);
        spinner = (Spinner)findViewById(R.id.spinner);
        photoView = (ImageView)findViewById(R.id.photoView);

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
        //setHistory();
        setSpinner();
    }

    /*private void setListView()
    {
        //String[] data = {"1", "2", "3","4","5"};
        String[] data = Utils.readFile(this, "history.txt").split("\n");

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

        listView.setAdapter(adaptor);
    }*/

    private void setSpinner()
    {
        //String[] data = {"1", "2", "3","4","5"};
        //String[] data = Utils.readFile(this, "history.txt").split("\n");
        /*String[] data = getResources().getStringArray(R.array.storyInfo);

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        spinner.setAdapter(adaptor);*/

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e != null)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                String[] stores = new String[list.size()];
                for (int i =0; i<list.size(); i++)
                {
                    ParseObject object = list.get(i);
                    stores[i] = object.getString("name")+", "+object.getString("address");
                }

                ArrayAdapter<String> storeAddapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, stores);
                spinner.setAdapter(storeAddapter);
            }
        });
    }

    private void setListView()
    {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e != null){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                    return;
                }

                queryResults = list;

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();

               for (int i =0; i < queryResults.size();i++)
               {
                   ParseObject object = queryResults.get(i);
                   String note = object.getString("note");
                   String storeInfo = object.getString("storeInfo");
                   String menu = object.getString("menu");// 做又會用到

                   String largeNum = object.getString("lNumber");

                   Map<String, String> item = new HashMap<String, String>();

                   item.put("note", note);
                   item.put("storeInfo", storeInfo);
                   item.put("drinkNum", menu); // 作業:把裡面所有數量拿出來相加後，顯示出來

                   data.add(item);
               }

                String[] from = {"note", "storeInfo", "drinkNum"};
                int[] to = {R.id.note, R.id.storeInfo, R.id.drinkNum};

                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.listview_item, from, to);

                listView.setAdapter(adapter);

            }
        });
    }


    public void submit(View view)
    {
        //Toast.makeText(this, "Hello DUDE!!!", Toast.LENGTH_LONG).show();// 第一個參數把自己傳入，第二個是要顯示的東西，第三個是顯示的時間

        //textView.setText("Test Test Test!!!");

        String text = editText.getText().toString();

        // 設定按下Submit後會去上傳選單跟menu的資料到Parse Server
        ParseObject orderObject = new ParseObject("Order");
        orderObject.put("note", text);
        orderObject.put("storeInfo", spinner.getSelectedItem());
        orderObject.put("menu", menuResult);

        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e ==null)
                {
                    Toast.makeText(MainActivity.this, "Submit OK", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Submit Fail", Toast.LENGTH_LONG).show();
                }
            }
        });
        // End of Parse Setting

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
        //setHistory();
    }

    // 從Main Activity 切到 Drink Menu Activity
    public void goToMenu(View view)
    {
        Intent intent = new Intent(); //建立一個Intent的物件
        intent.setClass(this, DrinkMenuActivity.class);//當這個物件被使用到時，會將intent自己的class設定為DrinkMenuActivity的Class

        //startActivity(intent);// 設定完intent的class後要去執行，這時當按下menu的按鈕後，就會跳到另一個Activity去。
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);
    }


    // 從Drink Menu Activity切回Main Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_MENU_ACTIVITY)
        {
            if (resultCode == RESULT_OK)
            {
                //textView.setText(data.getStringExtra("result"));

                menuResult = data.getStringExtra("result");

                try{
                    JSONArray array = new JSONArray(menuResult);
                    String text = "";
                    for(int i=0; i<array.length(); i++)
                    {
                        JSONObject order = array.getJSONObject(i);

                        String name = order.getString("name");
                        String lNumber = String.valueOf(order.getInt("lNumber"));
                        String mNumber = String.valueOf(order.getInt("mNumber"));

                        text = text + name + "l:" + lNumber + "m:" + mNumber + "\n";

                    }
                    textView.setText(text);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == REQUEST_CODE_CAMERA)
        {
            if(requestCode == RESULT_OK)
            {
                photoView.setImageURI(Utils.getPhotoUri());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if(id == R.id.action_take_photo)
        {
            Toast.makeText(this, "take photo", Toast.LENGTH_LONG).show();
            goToCamera();
        }

        return super.onOptionsItemSelected(item);

    }

    private void goToCamera()
    {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoUri());
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }


    //Activity生命週期Check
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "Main onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "Main onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "Main onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "Main onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "Main onRestart");
    }

}
