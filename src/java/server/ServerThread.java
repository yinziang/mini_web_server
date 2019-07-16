package server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 核心代码 用于web文件的读取与解析等
 *
 * @author yin
 */
public class ServerThread implements Runnable {

    private static Map<String, String> context = new HashMap<>();

    /**
     * 参照Tomcat的web.xml配置文件
     */
    static {
        context.put("html", "text/html");
        context.put("htm", "text/html");
        context.put("jpg", "image/jpeg");
        context.put("jpeg", "image/jpeg");
        context.put("gif", "image/gif");
        context.put("js", "application/javascript");
        context.put("css", "text/css");
        context.put("json", "application/json");
        context.put("mp3", "audio/mpeg");
        context.put("mp4", "video/mp4");
    }

    private Socket client;
    private InputStream in;
    private OutputStream out;
    private PrintWriter pw;
    private BufferedReader br;

    /**
     * TODO 根目录
     */
    private static final String WEB_ROOT = "/Users/yin/Documents/Git/GitHub/Local/mini_web_server/src/resources";

    public ServerThread(Socket client) {
        this.client = client;
        init();
    }

    private void init() {
        // 获取输入输出流
        try {
            in = client.getInputStream();
            out = client.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            goRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goRun() throws Exception {
        //读取请求内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        // String line = reader.readLine().split(" ")[1].replace("/", "\\");  //请求的资源
        //请求的资源
        String line = reader.readLine().split(" ")[1];
        if(line.equals("\\")){
            line += "index.html";
        }
        System.out.println(line);
        //获取文件的后缀名
        String strType = line.substring(line.lastIndexOf(".")+1, line.length());
        // String strType = "html";
        System.out.println("strType = " + strType);

        //给用户响应
        PrintWriter pw = new PrintWriter(out);
        InputStream input = new FileInputStream(WEB_ROOT + line);

        //BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
        pw.println("HTTP/1.1 200 ok");
        pw.println("Content-Type: "+ context.get(strType)  +";charset=utf-8");
        pw.println("Content-Length: " + input.available());
        pw.println("Server: hello");
        pw.println("Date: " + new Date());
        pw.println();
        pw.flush();

        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = input.read(bytes)) != -1){
            out.write(bytes, 0, len);
        }
        pw.flush();

        input.close();
        pw.close();
        reader.close();
        out.close();

        client.close();
    }
}
