package com.json.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.json.domain.Person;

public class JsonService {   
    public JsonService() {
        // TODO Auto-generated constructor stub
    }

    public Person getPerson(){
        Person person = new Person(23, "AHuier", "XIAMEN");
        return person;
    }
    
    public List<Person> getListPerson(){
        List<Person> list = new ArrayList<Person>();
        Person person1 = new Person(1001, "AHuier1", "Beijing");
        Person person2 = new Person(1002, "AHuier2", "shenzheng");
        list.add(person1);
        list.add(person2);
        return list;
    }
    
    public List<String> getListString(){
        List<String> list = new ArrayList<String>();
        list.add("Hello");
        list.add("World");
        list.add("AHuier");
        return list;
    }
    
    public List<Map<String, Object>> getListMaps(){
        List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("color", "red");
        map1.put("id", 01);
        map1.put("name", "Polu");
        listMap.add(map1);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("id", 07);
        map2.put("color", "green");
        map2.put("name", "Zark");
        listMap.add(map2);
        return listMap; 
    }
   
}
