package com.github.darains.sustechhelper.util;

import com.github.darains.sustechhelper.MainApp;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

public enum Config{
    
    INFO("config/info.properties"),ABOUT("config/about.properties");
    
    Properties prop = new Properties();
    
    String path;
    
    Config(String path){
        this.path=path;
        try{
            prop.load(MainApp.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public String getProperty(String key){
        return prop.getProperty(key);
    }
    
    public Object setProperty(String key,String value){
        Object ob= prop.setProperty(key,value);
        try{
            OutputStream o=new FileOutputStream(new File(MainApp.class.getClassLoader().getResource(path).toURI()));
            prop.store(o,"");
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (URISyntaxException e){
            e.printStackTrace();
        }
        return ob;
    }
    
}
