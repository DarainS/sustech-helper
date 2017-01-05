package com.github.darains.sustechhelper.function;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;




public class JsoupLogIn{
    
    
    JsoupLogIn(){
        
    }
    
    public static void main(String[] args){
        Connection.Response r1=null;
        Connection.Response r2=null;
        Connection.Response r3=null;
    
        try{
            r1 = Jsoup.connect("http://www.baidu.com").followRedirects(false).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36").execute();
            System.out.println("r1 headers:"+r1.headers());
            
            r2 = Jsoup.connect(r1.header("Location")).followRedirects(false).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36").execute();
            System.out.println("r2 headers:"+r2.headers());
    
            r3= Jsoup.connect(r2.header("Location")).followRedirects(false).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36").execute();
            System.out.println("r3 headers:"+r3.headers());
            System.out.println("r3 cookies:"+r3.cookies());
            
            Connection.Response r4 = Jsoup.connect(r3.header("Location")).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
                .cookies(r3.cookies())
                .timeout(100000)
                .method(Connection.Method.POST)
                .execute();
            System.out.println("r4 headers:"+r4.headers());
            System.out.println("r4 cookies:"+r4.cookies()+"\n\n");
            
            
    
            String lt;
            lt = r4.parse().body().select("#fm1 > section.row.btn-row > input[type=\"hidden\"]:nth-child(1)").attr("value");
            String execution;
            execution = r4.parse().body().select("#fm1 > section.row.btn-row > input[type=\"hidden\"]:nth-child(2)").attr("value");
            String eventId="submit";
            String submit="LOGIN";
    
            String cookieId;
    
            System.out.println("\n\n\n");
            System.out.println(r4.parse().location());
            System.out.println(r4.cookies());
            System.out.println(lt+"\n");
            System.out.println(execution+'\n');
    
    
    
            HttpPost httpPost = new HttpPost(r4.parse().location());
            httpPost.setHeader("Cookie","JSESSIONID="+r4.cookie("JSESSIONID"));
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("Accept-Encoding", "gzip, deflate, br");
            httpPost.addHeader("Upgrade-Insecure-Requests", "1");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("Origin", "https://cas.sustc.edu.cn");
//            httpPost.addHeader("Cache-Control", "max-age=0");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
            httpPost.addHeader("Host", "cas.sustc.edu.cn");
            httpPost.addHeader("Referer", r4.parse().location());
//            httpPost.addHeader("DNT", "1");
            httpPost.addHeader("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
            httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,**;q=0.8");
            
            List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            list.add(new BasicNameValuePair("username","11310387"));
            list.add(new BasicNameValuePair("password", "panda2ivy"));
            list.add(new BasicNameValuePair("lt",lt));
            list.add(new BasicNameValuePair("execution",execution));
            list.add(new BasicNameValuePair("_eventId","submit"));
            list.add(new BasicNameValuePair("submit","LOGIN"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
            
            httpPost.setEntity(entity);
            
            
            
            CloseableHttpClient httpClient= HttpClients.createDefault();
            
//            CloseableHttpResponse response=httpClient.execute(httpPost);
    
            System.out.println("\n\n");
            
//            System.out.println(IOUtils.toString(response.getEntity().getContent()));
            
            
            
            
            //**
            Connection.Response r5=Jsoup.connect(r4.parse().location())
                .header("Connection", "keep-alive")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Origin", "https://cas.sustc.edu.cn")
//                .header("Cache-Control", "max-age=0")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
                .header("Host", "cas.sustc.edu.cn")
                .header("Referer", r4.parse().location())
                .header("DNT", "1")
                .header("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
                 .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,**;q=0.8")
                .cookie("JSESSIONID",r4.cookie("JSESSIONID"))
                .data("username","11310387")
                .data("password","panda2ivy")
                .data("lt",lt)
                .data("execution",execution)
                .data("_eventId",eventId)
                .data("submit",submit)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")
                .method(Connection.Method.POST)
                .followRedirects(false)
                .timeout(100000)
                .validateTLSCertificates(false)
                .execute();
            System.out.println("r5 cookies:"+r5.cookies());
            System.out.println("r5 headers:"+r5.headers());
//            System.out.println(r5.);
            System.out.println(r5.body());
            //    */
    
            final WebClient webClient = new WebClient();
    
            webClient.addCookie("TGC="+r5.cookie("TGC"),new URL("http://cas.sustc.edu.cn"),"");
            final HtmlPage page = webClient.getPage("http://baidu.com");
    
            System.out.println(page.asXml());
            System.out.println(page.asText());
        
        
        } catch(IOException e){
            e.printStackTrace();
        }

//        log.info(document.contentType());
    }
}
