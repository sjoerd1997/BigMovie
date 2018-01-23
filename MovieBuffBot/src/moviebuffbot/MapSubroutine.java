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
        String sql = "SELECT country from countries where id IN (select country_id from movie_country where movie_id IN (select id from movies where title = " + "\"" + cmd + "\"" + " ))";
        String result = "";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        result += "https://maps.googleapis.com/maps/api/staticmap?autoscale=1&size=600x300&maptype=roadmap&format=png&visual_refresh=true";
        
        try {
            connection=(Connection) DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useSSL=false",
                    username, password); 
            statement=(Statement) connection.createStatement();
            resultSet=statement.executeQuery(sql);
            while(resultSet.next()) {
                //result += "\n" + "https://www.google.com/maps/search/" + resultSet.getString("country") + "\n";
                result += "&markers=size:mid%7Ccolor:0xff0000%7Clabel:" + resultSet.getString("country") + "%7C" + resultSet.getString("country") ;
            }
            result += "";
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

        return result;
    }
}