package com.github.darains.sustechhelper.function;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.xml.sax.SAXException;

import java.io.IOException;

public class HtmlUnitLogIn{
    
    public void logIn() throws IOException, SAXException{
        final WebClient webClient = new WebClient();
    
        final HtmlPage page = webClient.getPage("http://baidu.com");
    
        HtmlElement username = page.getFirstByXPath("//*[@id=\"username\"]");
        username.click();
        username.type("11310387");
        
        HtmlElement password=page.getFirstByXPath("//*[@id=\"password\"]");
        password.click();
        password.type("panda2ivy");
        
        HtmlElement elmt=page.getFirstByXPath("//*[@id=\"fm1\"]/section[3]/input[4]");
        elmt.click();
        
    }
    
    public static void main(String[] args) throws IOException, SAXException{
        HtmlUnitLogIn h = new HtmlUnitLogIn();
        h.logIn();
    }
}
