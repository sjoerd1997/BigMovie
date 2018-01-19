package moviebuffbot;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.rivescript.macro.Subroutine;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class JdbcSubroutine implements Subroutine {
    @Override
    public String call(com.rivescript.RiveScript rs, String[] args) {
       String host = args[0];
        String port = args[1];
        String db = args[2];
        String username = args[3];
        String password = args[4];
        String sql = "";
        String result = "";
        
        for (int i=5; i<args.length; i++) 
        sql = sql + " " + args[i];
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
                result += "\n" + resultSet.getString("title");
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
        
        return result;
    }   
}
