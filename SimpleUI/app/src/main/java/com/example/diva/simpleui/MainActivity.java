package com.example.diva.simpleui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 0;
    private static final int REQUEST_CODE_CAMERA = 1;

    private boolean hasPhoto ;

    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    ListView listView;
    Spinner spinner;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String menuResult="";
    List<ParseObject> queryResults;
    ImageView photoView;
    ProgressDialog progressDialog;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debuguydiva", "hello onCreate");

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
        progressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

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
        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDetailOrder(position);
            }
        });

        hidecheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckbox", hidecheckBox.isChecked());
                editor.apply();

                if(isChecked) {
                    photoView.setVisibility(View.GONE);
                } else {
                    photoView.setVisibility(View.VISIBLE);
                }
            }
        });

        //使用 自定義的function setListView()
        setListView();
        //setHistory();
        setSpinner();
    }

    private void setSpinner()
    {
        Log.d("debuguydiva", "hello setSpinner");
        //String[] data = {"1", "2", "3","4","5"};
        //String[] data = Utils.readFile(this, "history.txt").split("\n");
        /*String[] data = getResources().getStringArray(R.array.storyInfo);

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        spinner.setAdapter(adaptor);*/

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e != null) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                String[] stores = new String[list.size()];
                for (int i =0; i<list.size(); i++) {
                    ParseObject object = list.get(i);
                    stores[i] = object.getString("name")+", "+object.getString("address");
                }
                ArrayAdapter<String> storeAddapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, stores);
                spinner.setAdapter(storeAddapter);
            }
        });
    }

        /*
        private void setListView()
    {
        //String[] data = {"1", "2", "3","4","5"};
        String[] data = Utils.readFile(this, "history.txt").split("\n");

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

        listView.setAdapter(adaptor);
    }*/

    private void setListView() {
        Log.d("debuguydiva", "hello setListView");
        // 參考:https://parse.com/docs/android/api/com/parse/ParseQuery.html
        // 先用Parse的連線設定去取得Parse裡面key值為Order的所有資料
        // 意即query為在Parse資料庫中找class為Order的資料
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order");
        //接著在這些資料中，找所有資料存進一個剛new出來的物件
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e != null){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                queryResults = list;

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();

               for (int i =0; i < queryResults.size();i++) {
                   ParseObject object = queryResults.get(i);
                   String note = object.getString("note");
                   String storeInfo = object.getString("storeInfo");
                   //String menu = object.getString("menu");// 作業會用到
                   String menu = object.getString("menu");

                   // 計算杯數
                   int totalCount = getDrinkCount(menu);

                   Map<String, String> item = new HashMap<String, String>();

                   item.put("note", note);
                   item.put("storeInfo", storeInfo);
//                   item.put("drinkNum",  menu);// 作業:把裡面所有數量拿出來相加後，顯示出來
                   item.put("drinkNum",  String.valueOf(totalCount));

                   data.add(item);
               }
                String[] from = {"note", "storeInfo", "drinkNum"};
                int[] to = {R.id.note, R.id.storeInfoView, R.id.drinkNum};

                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.listview_item, from, to);

                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public int getDrinkCount(String menu)
    {
        int totalCount = 0;
        //當key為lNumber與mNumber時
            try{
                JSONArray objectCount = new JSONArray(menu);
                for(int i = 0; i < objectCount.length(); i++)
                {
                    JSONObject jsonObject = objectCount.getJSONObject(i);
                    totalCount = totalCount + jsonObject.getInt("mNumber") + jsonObject.getInt("lNumber");
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        //當key為l與m時
        if(totalCount == 0) {
            try {
                JSONArray objectCount = new JSONArray(menu);
                for (int i = 0; i < objectCount.
                        length(); i++) {
                    JSONObject jsonObject = objectCount.getJSONObject(i);
                    totalCount = totalCount + jsonObject.getInt("m") + jsonObject.getInt("l");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.d("debug", "Can not count the drink number!!");
        }
        return totalCount;
    }

    public void submit(View view) {
        Log.d("debuguydiva", "hello submit");
        //Toast.makeText(this, "Hello DUDE!!!", Toast.LENGTH_LONG).show();// 第一個參數把自己傳入，第二個是要顯示的東西，第三個是顯示的時間

        //textView.setText("Test Test Test!!!");

        String text = editText.getText().toString();

        // 設定按下Submit後會去上傳選單跟menu的資料到Parse Server
        // 在Parse上面有訂一個Class叫做Order，所以我們要New一個Order的物件
        // 然後把從畫面上的editText所輸入的備註資料塞進Order這個Class裡面的一個Key值稱note
        // 接著把spinner所選到的資料，塞進Order這個Class裡面的一個Key值稱為storeInfo
        // 最後把原本的DrinkMenuActivity得到的結果(稱為text)塞進Order這個Class裡面的一個值稱menu
        ParseObject orderObject = new ParseObject("Order");
        Log.d("debuguydiva", "get Parse Object");

        orderObject.put("note", text);
        Log.d("debuguydiva", "put note into Parse Object OK");

        orderObject.put("storeInfo", spinner.getSelectedItem());
        Log.d("debuguydiva", "put storeInfo into Parse Object OK");

        Log.d("debuguydiva", "menuResult:" + menuResult);
        orderObject.put("menu", menuResult);
        Log.d("debuguydiva", "put menu into Parse Object OK");

        Log.d("debuguydiva", "has photo:" + hasPhoto);

        if(hasPhoto)
        {
            Uri uri = Utils.getPhotoUri();
            ParseFile file = new ParseFile("photo.png", Utils.uriToBytes(this, uri));
            orderObject.put("photo", file);
        }


        progressDialog.setTitle("Loading...");
        progressDialog.show();

        Log.d("debuguydiva", "before saveInBackground");
        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("debuguydiva", " saveInBackground start");
                progressDialog.dismiss();
                if (e == null) {
                    Log.d("debuguydiva", "saveInBackground e=null");
                    Toast.makeText(MainActivity.this, "Submit OK", Toast.LENGTH_LONG).show();
                    Log.d("debuguydiva", "saveInBackground after e=null");
                    hasPhoto = false;

                    Log.d("debuguydiva", "saveInBackground after hasPhoto = false");
                    // 下面一行要註解掉才會正常
                    // photoView.setImageResource(0);
                    photoView.setImageDrawable(null);

                    Log.d("debuguydiva", "saveInBackground 123123");
                    editText.setText("");
                    textView.setText("");

                    Log.d("debuguydiva", "saveInBackground 456456");
                    setListView();
                } else {
                    Log.d("debuguydiva", "before saveInBackground e!=null");
                    Toast.makeText(MainActivity.this, "Submit Fail", Toast.LENGTH_LONG).show();
                }
                Log.d("debuguydiva", " saveInBackground end");
            }
        });

//        orderObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                progressDialog.dismiss();
//                if (e == null) {
//                    Toast.makeText(MainActivity.this, "Submit OK", Toast.LENGTH_LONG).show();
//                    hasPhoto = false;
//                    photoView.setImageResource(0);
//
//                    editText.setText("");
//                    textView.setText("");
//                    setHistory();
//                } else {
//                    Toast.makeText(MainActivity.this, "Submit Fail", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        // End of Parse Setting

        //Utils.writeFile(this, "history.txt", text + '\n');

        /*if(hidecheckBox.isChecked()) {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();

            textView.setText("你到底輸入了甚麼??");
            editText.setText("不要問，很恐怖");

            //間接寫法，如此就不會再繼續往下執行，所以下面的else也不用加了!!!
            return;
        }*/
        //else {
        //    textView.setText(text);
        //    editText.setText("");
        //}

        //textView.setText(text);

        //editText.setText(""); //清除原本的輸入，也就是設為空字串

        // 把輸入的值隱藏起來，全部用*取代
        //textView.setText(text);
        //editText.setText("");

//        textView.setText(text);
//        editText.setText("");
//        setListView();
        //setHistory();
    }

    // 從Main Activity 切到 Drink Menu Activity
    public void goToMenu(View view) {
        Log.d("debuguydiva", "hello goToMenu");
        Intent intent = new Intent(); //建立一個Intent的物件
        intent.setClass(this, DrinkMenuActivity.class);//當這個物件被使用到時，會將intent自己的class設定為DrinkMenuActivity的Class

        //startActivity(intent);// 設定完intent的class後要去執行，這時當按下menu的按鈕後，就會跳到另一個Activity去。
        //下面為上面一行呼叫另一個Activity的改寫，因為我們現在要串進去一個result code，所以寫法如下
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);
        // 若要傳資料進去另一個Activity就用 intent.putExtra(String str, String Key)
    }


    // 從Drink Menu Activity切回Main Activity
    // 從SimpleUIApplication切回Main Activity
    // onActivityResult為切回mainActivity的主要接口
    // 這個整數requestCode提供給onActivityResult，是以便確認返回的數據是從哪個Activity返回的。
    // requestCode和startActivityForResult中的requestCode相對應。
    // resultCode是由子Activity通過其setResult()方法返回。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("debuguydiva", "hello onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("debuguydiva", "[看我看我!]REQUEST_CODE_CAMERA : " + String.valueOf(REQUEST_CODE_CAMERA));
        Log.d("debuguydiva", "[看我看我!]REQUEST_CODE_MENU_ACTIVITY : " + String.valueOf(REQUEST_CODE_MENU_ACTIVITY));

        if(requestCode == REQUEST_CODE_MENU_ACTIVITY) {
            Log.d("debuguydiva", "hello after REQUEST_CODE_MENU_ACTIVITY 123"+RESULT_OK);
            if (resultCode == RESULT_OK) {
                Log.d("debuguydiva", "hello after REQUEST_CODE_MENU_ACTIVITY 456"+RESULT_OK);
                //textView.setText(data.getStringExtra("result"));

                // 從前一個DrinkMenuActivity傳回一個名為data的物件與一個result code
                // 而data裡面存放一個名為result的key值，其對應到一個JSONArray的array資料
                // 這個array裡面又存放三個key值分別為name, lNumber, mNumber，且分別對應到三種資料
                // 所以現在把data這個資料
                menuResult = data.getStringExtra("result");
                // P.S getStringExtra說明:
                // 在當前Activity1使用startActivity(intent)或者startActivityForResult(intent, code)方法跳轉到另一個Activity2之前(如上面goToMenu裡面所用)，
                // 如果要傳遞某些String類型數據給Activity2，則會執行intent.putExtra(String str, String Key),
                // 將String數據打包到Intent中，並給它一個Key標識。
                // 在Activity2當中，getIntent()方法獲得這個intent，然後再getStringExtra(Key)，
                // 就可以獲得你之前打包的那個數據了。


                // 接下來分別把裡面資料的值取出來，因為資料唯一筆一筆的，所以可以計算長度當作資料筆數。
                // 把原本的資料用JSONArray的格式來表示
                // 所以先把於原本的menuResult用array這個JSINArray格式來存，再去做調用
                try{
                    JSONArray array = new JSONArray(menuResult);
                    String text = "";
                    for(int i=0; i<array.length(); i++) {
                        JSONObject order = array.getJSONObject(i);

                        String name = order.getString("name");
                        String lNumber = String.valueOf(order.getInt("lNumber"));
                        String mNumber = String.valueOf(order.getInt("mNumber"));

                        text = text + name + "l:" + lNumber + "m:" + mNumber + "\n";
                    }
                    textView.setText(text);// 把最後的text結果設定回去畫面上的textView
                }
                // 用來抓例外錯誤狀況
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == REQUEST_CODE_CAMERA) {
            Log.d("debuguydiva", "hello get REQUEST_CODE_CAMERA  "+REQUEST_CODE_CAMERA);
            Log.d("debuguydiva", "hello get RESULT_OK  "+RESULT_OK);
            Log.d("debuguydiva", "hello get resultCode  "+resultCode);
            if(resultCode == RESULT_OK) {
                Log.d("debuguydiva", "hello RESULT_OK start  "+RESULT_OK);
                hasPhoto = true;

                photoView.setImageURI(Utils.getPhotoUri());

                Log.d("debuguydiva", "hello RESULT_OK end" );
            }
            Log.d("debuguydiva", "hello REQUEST_CODE_CAMERA end" );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("debuguydiva", "hello onCreateOptionsMenu");
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("debuguydiva", "hello onOptionItemSelected");
        //return super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if(id == R.id.action_take_photo) {
            Toast.makeText(this, "take photo", Toast.LENGTH_LONG).show();
            goToCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToCamera() {
        Log.d("debuguydiva", "hello goToCamera");
        if(Build.VERSION.SDK_INT >= 23)
        {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return;
            }
        }

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoUri());
//        this.setResult(RESULT_OK);
//        this.startActivity(intent);

        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    public void goToDetailOrder(int position) {
        Log.d("debuguydiva", "hello goToDetailOrder");
        ParseObject object = queryResults.get(position);
        Intent intent = new Intent();
        intent.setClass(this, OrderDetailActivity.class);

        intent.putExtra("note", object.getString("note"));
        intent.putExtra("storeInfo", object.getString("storeInfo"));
        intent.putExtra("menu", object.getString("menu"));

        if(object.getParseFile("photo") != null)
        {
            intent.putExtra("photoURL", object.getParseFile("photo").getUrl());
        }

        startActivity(intent);

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
