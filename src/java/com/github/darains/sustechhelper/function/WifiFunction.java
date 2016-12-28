package com.github.darains.sustechhelper.function;

import com.github.darains.sustechhelper.util.AppLogger;
import com.github.darains.sustechhelper.util.TimeLog;
import lombok.Setter;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WifiFunction implements Runnable{
    
    enum NetStatus {
        CONNECTED(0,"Connected"),NEWWORK_ERROR(403,"Network Error!"),NOT_IN_SCHOOL(300,"Not in Students' Network!"),NO_NETWORK(400,"No Networking!");
        public final int statusCode;
        public final String describe;
        NetStatus(int status,String describe){
            this.statusCode=status;
            this.describe=describe;
        }
    }
    
    private static AppLogger appLogger;
    
    @Setter
    private String userName;
    
    @Setter
    private String password;
    
    @Setter
    private static boolean isRememberPassword;
    
    @Setter
    private static boolean isAutoRun;
    
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    
    private String location=null;
    
    private static final String mo="~~~~~~~~~~";
    
    private static long delay = 1000;
    
    private volatile NetStatus netStatus;
    
    public synchronized static void setDefaultHeader(HttpRequestBase httpGet){
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.protocol.handle-redirects", false);
        httpGet.setParams(params);
    }
    
    public boolean isNetWorking() {
        try {
            HttpGet httpget = new HttpGet("http://www.baidu.com");
            setDefaultHeader(httpget);
            CloseableHttpResponse response=null;
            try {
                response= httpClient.execute(httpget);
                if (response.getStatusLine().getStatusCode()==200){
                    if (netStatus==NetStatus.CONNECTED){
                        appLogger.relog(time()+netStatus.describe);
                    }
                    else {
                        netStatus=NetStatus.CONNECTED;
                        appLogger.log(time()+netStatus.describe);
                    }
                }
                else{
                    if (response.getFirstHeader("Location")!=null){
                        location=response.getFirstHeader("Location").getValue();
                        if (location.contains("baidu.com")){
                            appLogger.log(time()+"Connected");
                        }
                        else
                            if (location.contains("http://enet.10000.gd.cn")){
                                appLogger.log(time()+"WifiFunction...");
                                logIn();
                            }
                            else {
                                if (netStatus==NetStatus.NOT_IN_SCHOOL){
                                    appLogger.relog(time()+"Not in Students' Network!");
                                } else {
                                    appLogger.log(time()+"Not in Students' Network!");
                                    netStatus=NetStatus.NOT_IN_SCHOOL;
    
                                }
                            }
                    }
                    else {
                        if (netStatus==NetStatus.NOT_IN_SCHOOL){
                            appLogger.relog(time()+"Not in Students' Network!");
                        } else {
                            appLogger.log(time()+"Not in Students' Network!");
                            netStatus=NetStatus.NOT_IN_SCHOOL;
                        }
                    }
                }
            } catch (UnknownHostException e) {
                if (netStatus==NetStatus.NO_NETWORK){
                    appLogger.relog(time()+"No Networking!");
                } else {
                    appLogger.log(time()+"No Networking!");
                    netStatus=NetStatus.NO_NETWORK;
                }
            }
            catch (SocketException se){
                if (netStatus==NetStatus.NEWWORK_ERROR){
                    appLogger.relog(time()+"Network Error!");
                } else {
                    appLogger.log(time()+"Network Error!");
                    netStatus=NetStatus.NEWWORK_ERROR;
                }
            }
            finally {
                if (response!=null)
                    response.close();
                }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public static HttpGet newGetRequest(String url){
        HttpGet httpGet=new HttpGet(url);
        return httpGet;
    }
    
    private void logIn(){
        logIn(userName,password.toCharArray());
    }
    
    public void logIn(String username,char[] password){
        System.out.println("location:"+location);
        HttpGet httpGet=newGetRequest(location);
        String data;
        try {
//            log.info();
//            Connection.Response response=Jsoup.connect(location).execute();
            data=text(httpClient.execute(httpGet));
            String s1="action=\"(.*?)\"";
            String s2="<input type=\"hidden\" name=\"lt\" .*?value=\"(.*?)\"";
            String s3="<input type=\"hidden\" name=\"execution\" .*?value=\"(.*?)\"";
            String s4="jsessionid=(.*?)type=";
            Pattern pattAction = Pattern.compile(s1);
            Pattern pattLt = Pattern.compile(s2);
            Pattern pattExec = Pattern.compile(s3);
            Pattern pattJsession=Pattern.compile(s4);
            Matcher matcherAction = pattAction.matcher(data);
            Matcher matcherLt = pattLt.matcher(data);
            Matcher matcherJsession=pattJsession.matcher(data);
            Matcher matcherExec = pattExec.matcher(data);
            if (matcherAction.find()&&matcherLt.find()&&matcherExec.find()&&matcherJsession.find()){
                String action=matcherAction.group(0);
                String lt= matcherLt.group(0);
                String execution= matcherExec.group(0);
                action=action.substring(8,action.length()-1);
                System.out.println("action:"+action);
                lt=lt.substring(38,lt.length()-1);
                execution=execution.substring(45,execution.length()-1);

                HttpPost httpPost = new HttpPost("https://cas.sustc.edu.cn"+action);
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair("username",username));
                list.add(new BasicNameValuePair("password", new String(password)));
                list.add(new BasicNameValuePair("lt",lt));
                list.add(new BasicNameValuePair("execution",execution));
                list.add(new BasicNameValuePair("_eventId","submit"));
                list.add(new BasicNameValuePair("submit","LOGIN"));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
                httpPost.setEntity(entity);
                
                System.out.println("~~~~~~~~~executing request~~~~~~~~~~" + httpPost.getURI());
    
                Executors.newSingleThreadExecutor().submit(new Runnable(){
                    @Override
                    public void run(){
                        try{
                            httpClient.execute(httpPost);
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                System.out.println("\nError");
                System.out.println(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void testLogIn(){
        String url="https://cas.sustc.edu.cn/cas/login?service="+location;
        Connection.Response response=Jsoup.connect(url).response();
        try{
            response.parse().body().select("").val();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    public static String text(CloseableHttpResponse response){
        StringBuffer sb=new StringBuffer();
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String d;
            while ((d=br.readLine())!=null){
                sb.append(d+"\n");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
    private   String time(){
        return TimeLog.timeInfo();
    }
    
    private volatile boolean shouldRun=true;
    
    public void run(){
        appLogger.log(mo+"SUSTC_WIFI v0.2.2_beta "+mo);
        appLogger.log(TimeLog.timeInfo() + "Starting...");
        long begin= System.currentTimeMillis();
        while (shouldRun) {
            try {
                isNetWorking();
                TimeUnit.MILLISECONDS.sleep(delay-(System.currentTimeMillis()-begin>0?System.currentTimeMillis()-begin:delay));
                begin=System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
//        if (args.length==2&&args[0].length()==8&&args[0].compareTo("11110000")>0&&args[1].length()>=6) {
//            userName=args[0];
//            password=args[1];
//            System.out.println(mo+"SUSTC_WIFI v0.1.0_beta "+mo);
//            System.out.println(TimeLog.timeInfo() + "Starting...");
//            while (true) {
//                try {
//                    isNetWorking();
//                    sleep(2000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        else {
//            System.out.println("请输入你的学号和密码,我们绝不会以任何方式记录你的密码!");
//            System.out.println("一个合法的输入示例为: java -jar C:\\\\sustc_wifi.jar 11310888 qwer1234");
//        }
    
    
    public static void setAppLogger(AppLogger l){
        appLogger =l;
    }
    
    
}
