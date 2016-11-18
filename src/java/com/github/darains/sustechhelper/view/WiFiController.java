package com.github.darains.sustechhelper.view;

import com.github.darains.sustechhelper.function.WifiFunction;
import com.github.darains.sustechhelper.util.Config;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.github.darains.sustechhelper.util.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WiFiController{
    
    @FXML
    private Button logInBtn;
    @FXML
    private TextField idField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea logArea;
    
    class TextLog implements Logger{
        TextArea t;
        TextLog(TextArea textArea){
            this.t=textArea;
        }
        public void log(String s){
            if (t.getText()==null){
                t.setText("");
            }
    
            t.setText(s+"\n"+t.getText());
//            try{
//                TimeUnit.MILLISECONDS.sleep(15);
//            } catch (InterruptedException e){
//                e.printStackTrace();
//            }
//            t.setScrollTop(t.getScrollTop()+1000);
    
        }
    }
    
    @FXML
    private void initialize(){
//        logInBtn.setText("WifiFunction");
    
        String id= Config.INFO.getProperty("userid");
        String password=Config.INFO.getProperty("password");
        idField.setText(id);
        passwordField.setText(password);
    
    }
    
    private ExecutorService service= Executors.newSingleThreadExecutor();
    
    
    @FXML
    private void logIn(){
        logInBtn.setText("...");
        logArea.setText("");
        
        String userName=idField.getText();
        String password=passwordField.getText();
    
        ExecutorService s= Executors.newSingleThreadExecutor();
        s.execute(new Runnable(){
            @Override
            public void run(){
                Config.INFO.setProperty("userid",userName);
                Config.INFO.setProperty("password",password);
            }
        });
        Config.INFO.setProperty("userid",userName);
        Config.INFO.setProperty("password",password);
        WifiFunction l=new WifiFunction();
        l.setLogger(new TextLog(logArea));
        l.setUserName(userName);
        l.setPassword(password);
    
        service.execute(l);
        logInBtn.setText("重新连接");
        
    }
    
    
    
}
