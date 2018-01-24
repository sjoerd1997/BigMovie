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
        String cmd = StringUtils.join(args, " ");
        //System.out.println(cmd); for debugging
        
        //Connection information for database
        String host = "localhost";
        String port = "3306";
        String db = "bigmovie";
        String username = "root";
        String password = "1234";
        //Query for getting Countries where a movie
        String sql = "SELECT country from countries where id IN (select country_id from movie_country where movie_id IN (select id from movies where title = " + "\"" + cmd + "\"" + " ))";
        String like_query = "SELECT title from movies where title like" + "\"%" + cmd + "%\" order by title limit 20";
        String result = "";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        result += "This is the map: \n" + "https://maps.googleapis.com/maps/api/staticmap?autoscale=1&size=600x300&maptype=roadmap&format=png&visual_refresh=true";
        
        try {
            connection=(Connection) DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useSSL=false",
                    username, password); 
            statement=(Statement) connection.createStatement();
            resultSet=statement.executeQuery(sql);
            int label = 0;
            //Check if resultSet doesn't return any results, if it doesn't return anything check if there is anything like the title That was given.
            if(!resultSet.isBeforeFirst()){
                resultSet=statement.executeQuery(like_query);
                
                if(!resultSet.isBeforeFirst()){
                    result = "That Movie does not exist";
                } 
                else {
                    result = "That Movie does not exist, Dit you mean one fo these movies? \n";
                    while(resultSet.next()){
                        result += resultSet.getString("title") + "\n";
                    }
                }
            } 
            else {
                // Create link to map of countries
                while(resultSet.next()) {
                    result += "&markers=size:mid%7Ccolor:0xff0000%7Clabel:" + label + "%7C" + resultSet.getString("country") ;
                    label++;
                }
                result += "";
               
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

        return result;
    }
}