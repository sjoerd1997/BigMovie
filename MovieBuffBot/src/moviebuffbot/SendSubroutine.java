/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviebuffbot;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.rivescript.macro.Subroutine;
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class SendSubroutine implements Subroutine {
    DefaultAbsSender das;
    
    public SendSubroutine(DefaultAbsSender sender) {
        das = sender;
    }
    
    @Override
    public String call(com.rivescript.RiveScript rs, String[] args) {
        String type = args[0];
        if (type.equals("file")) {
            String host = args[1];
            String port = args[2];
            String db = args[3];
            String username = args[4];
            String password = args[5];
            String sql = "";
            String result = "";
            
            for (int i = 6; i < args.length; i++) {
                sql = sql + " " + args[i];
            }
        
            sql = sql.trim();
            
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try {         
                connection=(Connection) DriverManager.getConnection(
                        "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=false&useSSL=false",
                        username, password); 
                statement=(Statement) connection.createStatement();
                resultSet=statement.executeQuery(sql);

                while(resultSet.next()) {
                    int i = resultSet.getMetaData().getColumnCount();
                    for (int j = 1; j <= i; j++) {
                        if (result.equals("")) {
                            result = resultSet.getString(j);
                        } else {
                            result += resultSet.getString(j) + " ";
                        }
                    }
                    if (!result.equals("")) 
                        result += "\n";
                }
                
                try {
                    File f = new File("result.txt");
                    FileUtils.writeStringToFile(f, result, "utf-8");
                    
                    System.out.println(result);
                    SendDocument msg = new SendDocument()
                            .setChatId(rs.currentUser())
                            .setNewDocument(f);
                    try {
                        das.sendDocument(msg); // Call method to send the photo with caption
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally{
                try {
                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        if ("photo".equals(type)) {
            String f_id = args[1];
            String caption = "";
            for (int i=2; i<args.length; i++) 
                caption = caption + " " + args[i];
            caption = caption.trim();
            SendPhoto msg = new SendPhoto()
                    .setChatId(rs.currentUser())
                    .setNewPhoto(new File(f_id))
                    .setCaption(caption);
            try {
                das.sendPhoto(msg); // Call method to send the photo with caption
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        
        return "";
    }
}
