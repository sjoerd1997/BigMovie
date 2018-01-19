/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviebuffbot;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.rivescript.macro.Subroutine;
import com.rivescript.util.StringUtils;
import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class MapSubroutine implements Subroutine {
    DefaultAbsSender das;
    
    public MapSubroutine(DefaultAbsSender sender) {
        das = sender;
    }
    
    @Override
    public String call(com.rivescript.RiveScript rs, String[] args) {
        //String type = args[0];
        String cmd = StringUtils.join(args, " ");
        System.out.println(cmd);
        
        String host = "localhost";
        String port = "3306";
        String db = "bigmovie";
        String username = "root";
        String password = "1234";
        String sql = "SELECT location FROM locations WHERE title like " + "\"%" + cmd + "%\"";
        String result = "";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection=(Connection) DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useSSL=false",
                    username, password); 
            statement=(Statement) connection.createStatement();
            resultSet=statement.executeQuery(sql);
            while(resultSet.next()) {
                result += "\n" + resultSet.getString("location");
            }
           
            result += "\n...";
            
            return result;
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
        
        
        /*if ("photo".equals(type)) {
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
        }*/
        //return "";
        return result;
    }
}