
package com.android.jsonproject;

import java.util.List;
import java.util.Map;

import com.android.jsonproject.domain.Person;
import com.android.jsonproject.http.HttpUtils;
import com.android.jsonproject.json.JSONTools;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
    
    private static final String TAG = "MainActivity";
    private Button person, persons, liststring, listmap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void initComponent(){
        person = (Button)findViewById(R.id.person);
        persons = (Button)findViewById(R.id.persons);
        liststring = (Button)findViewById(R.id.liststring);
        listmap = (Button)findViewById(R.id.listmap);
        person.setOnClickListener(this);
        persons.setOnClickListener(this);
        liststring.setOnClickListener(this);
        listmap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.person:  
                String path = "http://192.168.0.112:8080/JsonProject/servlet/JsonAction?action_flag=person";
                String jsonString = HttpUtils.getJsonContent(path);
                Log.i(TAG, "The jsonString:" + jsonString);
                Person person = JSONTools.getPerson("person", jsonString);
                Log.i(TAG, "The person:" + person.toString());
                break;
            case R.id.persons:
                String path2 = "http://192.168.0.112:8080/JsonProject/servlet/JsonAction?action_flag=persons";
                String jsonString2 = HttpUtils.getJsonContent(path2);
                Log.i(TAG, "The jsonString:" + jsonString2);
                List<Person> list2 = JSONTools.getPersons("persons", jsonString2);
                Log.i(TAG, "The persons:" + list2.toString());
                break;
            case R.id.liststring:
                String path3 = "http://192.168.0.112:8080/JsonProject/servlet/JsonAction?action_flag=listString";
                String jsonString3 = HttpUtils.getJsonContent(path3);
                Log.i(TAG, "The jsonString:" + jsonString3);
                List<String> list3 = JSONTools.getListString("listString", jsonString3);
                Log.i(TAG, "The listString:" + list3.toString());
                break;
            case R.id.listmap:
                String path4 = "http://192.168.0.112:8080/JsonProject/servlet/JsonAction?action_flag=listMap";
                String jsonString4 = HttpUtils.getJsonContent(path4);
                Log.i(TAG, "The jsonString:" + jsonString4);
                List<Map<String, Object>> list4 = JSONTools.getListMaps("listMap", jsonString4);
                Log.i(TAG, "The listMap:" + list4.toString());
                break;
        }
    }
}
