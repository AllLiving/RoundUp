package com.json.tools;

import net.sf.json.JSONObject;

/**
 * @author xukunhui
 * 工具类,这里专门处理Java对象转Json字符串的功能
 */
public class JsonTools {

    public JsonTools() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param key ： JSON 名值对中的的名字
     * @param value ：JSON 名值对中的值,值可以有多种类型
     * @return
     */
    // 接受对象转换为JSON数据格式并且作为字符串输出.
    public static String createJsonString(String key, Object value){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        return jsonObject.toString(); //就可以转换成Json数据格式
    }
    
}
