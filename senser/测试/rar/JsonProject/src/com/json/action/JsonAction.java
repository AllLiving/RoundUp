package com.json.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.json.service.JsonService;
import com.json.tools.JsonTools;

/**
 * @author xukunhui
 * Servlet类,处理根浏览器客户端通过不同的参数请求返回JSON数据。
 */
public class JsonAction extends HttpServlet {

    private JsonService service;
    
    /**
     * Constructor of the object.
     */
    public JsonAction() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; Charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();          
        /*   
         * 如果是以下这种方式直接在浏览器地址栏中用  http://192.168.0.112:8080/JsonProject/servlet/JsonAction 请求即可
         * String jsonString = JsonTools.createJsonString("person", service.getListPerson());
         * out.print(jsonString); // 从服务端提取数据,并且输出的浏览器客户端。
        */        
        // 根据不同的参数输出不同的JSON数据
        String jsonString = "";
        String action_flag = request.getParameter("action_flag");
        if(action_flag.equals("person")) {
            jsonString = JsonTools.createJsonString("person", service.getPerson());
        } else if(action_flag.equals("persons")){
            jsonString = JsonTools.createJsonString("persons", service.getListPerson());
        } else if(action_flag.equals("listString")) {
            jsonString = JsonTools.createJsonString("listString", service.getListString());
        } else if(action_flag.equals("listMap")){
            jsonString = JsonTools.createJsonString("listMap", service.getListMaps());
        }
        out.print(jsonString);
        out.flush();
        out.close();
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
        service = new JsonService();
    }
}



































