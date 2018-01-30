package com.android.jsonproject.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.jsonproject.domain.Person;

/**
 * @author xukunhui
 * 完成对从服务端请求获得的JSON数据的解析成指定的对象.
 */
public class JSONTools {

    public JSONTools() {
        // TODO Auto-generated constructor stub
    }
    
    // 此时从服务端取下来的数据是：{"person":{"address":"XIAMEN","id":23,"name":"AHuier"}}
    public static Person getPerson(String key, String jsonString){
        Person person = new Person();
        try {
            // 在Android官方文档中，org.json 这是Android提供给我们的解析json数据格式的包，
            // 我们比较常用的是JSONArray 和 JSONObject这个两个类
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject personObject = jsonObject.getJSONObject(key);
            person.setId(personObject.getInt("id"));
            person.setAddress(personObject.getString("address"));
            person.setName(personObject.getString("name"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return person;
    }
    
    //{"persons":[{"address":"Beijing","id":1001,"name":"AHuier1"},{"address":"shenzheng","id":1002,"name":"AHuier2"}]}
    public static List<Person> getPersons(String key, String jsonString){
        List<Person> list = new ArrayList<Person>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            //返回json的数组
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                Person person = new Person();
                person.setId(jsonObject2.getInt("id"));
                person.setName(jsonObject2.getString("name"));
                person.setAddress(jsonObject2.getString("address"));
                list.add(person);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return list;
    }
    
    //{"listString":["Hello","World","AHuier"]}
    public static List<String> getListString(String key, String jsonString){
        List<String> listString = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            //返回JSON的数组
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for(int i = 0; i < jsonArray.length(); i++){
                String msg = jsonArray.getString(i);
                listString.add(msg);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return listString;
    }
    
    // 此时从服务端取下来的数据是：{"listMap":[{"id":1,"color":"red","name":"Polu"},{"id":7,"color":"green","name":"Zark"}]}
    public static List<Map<String, Object>> getListMaps(String key, String jsonString){
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<String, Object>();
                // 通过org.json中的迭代器来取Map中的值。
                Iterator<String> iterator = jsonObject2.keys();
                while(iterator.hasNext()) {
                    String jsonKey = iterator.next();
                    Object jsonValue = jsonObject2.get(jsonKey);
                    //JSON的值是可以为空的，所以我们也需要对JSON的空值可能性进行判断。
                    if(jsonValue == null){
                        jsonValue = "";
                    }
                    map.put(jsonKey, jsonValue);
                }
                listMap.add(map);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return listMap;
    }    
}

