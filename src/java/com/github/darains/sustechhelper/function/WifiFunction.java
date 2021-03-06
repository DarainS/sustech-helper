package com.github.darains.sustechhelper.function;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.darains.sustechhelper.util.AppLogger;
import com.github.darains.sustechhelper.util.Config;
import com.github.darains.sustechhelper.util.TimeLog;
import lombok.Setter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;


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
                                try{
                                    logIn();
                                } catch(SAXException e){
                                    e.printStackTrace();
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
    
    private void logIn() throws IOException, SAXException{
        logIn(userName,password);
    }
    
    /*
    *
    * 被弃用的登录方法,现使用HtmlUnit模拟浏览器莱登陆
    *
    public void logIn(String username,char[] password){
        System.out.println("login()\nlocation:"+location);
        String newUrl="https://cas.sustc.edu.cn/cas/login?service="+location;
        HttpGet httpGet=newGetRequest(newUrl);
        String data;
        try {
//            log.info();
//            Connection.Response response=Jsoup.connect(location).execute();
            data=text(httpClient.execute(httpGet));
            String s1="action=\"(.*?)\"";
            System.out.println("data:\n"+data);
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
                System.out.println("execution:"+execution);
    
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
    */
    
    public void logIn(String username,String password) throws IOException, SAXException{
        final WebClient webClient = new WebClient();
        
        final HtmlPage page = webClient.getPage("http://baidu.com");
        
        HtmlElement usernameField = page.getFirstByXPath("//*[@id=\"username\"]");
        usernameField.click();
        usernameField.type(username);
        
        HtmlElement passwordField=page.getFirstByXPath("//*[@id=\"password\"]");
        passwordField.click();
        passwordField.type(password);
        
        HtmlElement elmt=page.getFirstByXPath("//*[@id=\"fm1\"]/section[3]/input[4]");
        elmt.click();
        
    }
    
    
    private   String time(){
        return TimeLog.timeInfo();
    }
    
    private volatile boolean shouldRun=true;
    
    public void run(){
        appLogger.log(mo+"SUSTC_WIFI  "+Config.ABOUT.getProperty("version")+" "+mo);
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
    
    
    
    
    public static void setAppLogger(AppLogger l){
        appLogger =l;
    }
    
    
}
