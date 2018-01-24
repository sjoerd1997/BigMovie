package moviebuffbot;

import com.rivescript.macro.Subroutine;
import com.rivescript.util.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.commons.codec.binary.StringUtils.newString;


public class GraphSubroutine implements Subroutine {

    @Override
    public String call(com.rivescript.RiveScript rs, String[] args) {
        String cmd = StringUtils.join(args, " ");
        String result = "";
        
        int number = 0;
        for(String s : args){
            if(number <= 8) cmd += args[number];
            number++;
        }
        String path = "C:\\Users\\emiel\\OneDrive\\Documenten\\GitHub\\BigMovie\\MovieBuffBot\\resources\\R\\video-format.R";
        ReplaceString(path, "replacevalue", args[9]);
        
        java.util.Scanner s;
        try {
            s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
            result += s.hasNext() ? s.next() : "";
            ReplaceString(path,args[9], "replacevalue");
            return result;
        } catch (IOException ex) {
            Logger.getLogger(SystemSubroutine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public void ReplaceString(String path, String ToReplace, String ReplaceValue) {
                File fileToBeModified = new File(path);

        String oldContent = "";

        BufferedReader reader = null;

        FileWriter writer = null;

        try 
        {
            reader = new BufferedReader(new FileReader(fileToBeModified));

            String line = reader.readLine();

            while (line != null) 
            {
                    oldContent = oldContent + line + System.lineSeparator();
                    line = reader.readLine();
            }
            String newContent = oldContent.replaceAll(ToReplace, ReplaceValue);
            writer = new FileWriter(fileToBeModified);

            writer.write(newContent);
        }
        catch (IOException e)
        {
                e.printStackTrace();
        }
        finally
        {
                try 
                {
                        reader.close();
                        writer.close();
                } 
                catch (IOException e) 
                {
                        e.printStackTrace();
                }
        }
    }
    
}