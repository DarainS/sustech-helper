package com.github.darains.sustechhelper.controller;

import com.github.darains.sustechhelper.function.WifiFunction;
import com.github.darains.sustechhelper.util.Config;
import com.github.darains.sustechhelper.util.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RootController{
    
    @FXML
    private Button logInBtn;
    @FXML
    private TextField idField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea logArea;
    @FXML
    private CheckBox isRememberCB;
    @FXML
    private CheckBox isAutoRunCB;
    
    
    class TextLog implements Logger{
        TextArea t;
        
        int len;
        
        TextLog(TextArea textArea){
            this.t=textArea;
        }
        public void log(String s){
            if (t.getText()==null){
                t.setText("");
            }
    
            t.setText(s+"\n"+t.getText());
            len=(s+"\n").length();
        }
        public void relog(String s){
            t.setText(s+"\n"+t.getText().substring(len));
            len=(s+"\n").length();
        }
    }
    
    @FXML
    private void initialize(){
        logArea.setText("");
        String id= Config.INFO.getProperty("userid");
        idField.setText(id);
        isRememberCB.setSelected(Boolean.valueOf(Config.INFO.getProperty("isRememberPassword")));
        if (isRememberCB.isSelected()) {
            String password = Config.INFO.getProperty("password");
            passwordField.setText(password);
        }
        isAutoRunCB.setSelected(Boolean.valueOf(Config.INFO.getProperty("isAutoRun")));
        if (isAutoRunCB.isSelected()){
            handleLogInBtn();
        }
    }
    
    private ExecutorService service = Executors.newSingleThreadExecutor();
    
    
    @FXML
    private void handleGithub(){
        try{
            Desktop.getDesktop().browse(new URI("https://github.com/DarainS/sustech-helper"));
        } catch (IOException e){
            e.printStackTrace();
        } catch (URISyntaxException e){
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogInBtn(){
    
//        if (!service.isShutdown()){
//            service.shutdown();
//        }
    
        logInBtn.setText("...");
        
        String userName=idField.getText();
        String password=passwordField.getText();
        
//        ExecutorService s= Executors.newSingleThreadExecutor();
        
        try{
            Config.INFO.setProperty("userid",userName);
    
            if (isRememberCB.isSelected()) {
                Config.INFO.setProperty("password", password);
            }
    
        } catch (Exception e){
            e.printStackTrace();
        }
        
        WifiFunction l=new WifiFunction();
        
        WifiFunction.setRememberPassword(isRememberCB.isSelected());
        
        WifiFunction.setAutoRun(isAutoRunCB.isSelected());
        
        l.setLogger(new TextLog(logArea));
        
        l.setUserName(userName);
        
        l.setPassword(password);
    
        
        service.execute(l);
        
        logInBtn.setText("重新连接");
        
    }
    
    @FXML
    private void handlePasswordCheckBox(){
        Config.INFO.setProperty("isRememberPassword",String.valueOf(isRememberCB.isSelected()));
    }
    
    @FXML
    private void handleAutoRunCheckBox(){
        Config.INFO.setProperty("isAutoRun",String.valueOf(isAutoRunCB.isSelected()));
    }
    
    
}
