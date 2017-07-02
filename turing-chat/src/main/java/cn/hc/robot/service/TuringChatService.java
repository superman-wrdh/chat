package cn.hc.robot.service;

import cn.hc.robot.http.Request;
import cn.hc.robot.http.Response;
import net.sf.json.JSONObject;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * Created by hc on 17/7/2.
 */
@Service
public class TuringChatService {
    private static final String url="http://www.tuling123.com/openapi/api"; //图灵聊天请求地址
    private static final String key="xxxxxxxxxxx7584a1654xxxxxxx"; //图灵官网申请 key 妥善保管
    private JSONObject paramaters=null;
    public Response send(String info)throws Exception{
        System.out.println(info);
        paramaters=new JSONObject();
        paramaters.put("key",key);
        paramaters.put("info",info);
        Request r=new Request(url);
        r.contentType(ContentType.APPLICATION_JSON);
        r.body(paramaters.toString(),"UTF-8");
        return r.execute();
    }

    public JSONObject getJSONMsg(Response response){
        response.setCharset(Charset.forName("utf-8"));
        String string = response.string();
        JSONObject jsonObject= JSONObject.fromObject(string);
        return jsonObject;
    }

    public static void main(String[] args) throws Exception {
        TuringChatService turingChatService = new TuringChatService();
        Response response = turingChatService.send("hello");
        System.out.println(turingChatService.getJSONMsg(response));
    }
}
