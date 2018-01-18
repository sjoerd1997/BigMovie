/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Nick van Urk
 */
public class Parser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dataSrc = "data/src/";
        String dataDest = "data/dest/";
        
        String[] dataSrcFileNames = {
            "countries.list",
            "genres.list",
            "locations.list",
            "movies.list",
            "ratings.list",
            "running-times.list"
        };
        
        String[] dataDestFileNames = {
            "countries.csv",
            "genres.csv",
            "locations.csv",
            "movies.csv",
            "ratings.csv",
            "running-times.csv"
        };
        
        for(int i = 0; i < dataSrcFileNames.length; i++) {
            String fileNameSrc = dataSrcFileNames[i];
            String fileNameDest = dataDestFileNames[i];
            
            String selector = fileNameSrc == "ratings.list"
                    ? "\\s+([0-9\\.*]{10})((?!\\\"\\s\\().)*$"
                    : "(^[^\\s+\"].*)\\((\\d{4}|\\?{4})+\\/?(\\w+)?\\)";
        
            try (Stream<String> stream = Files.lines(Paths.get(dataSrc + fileNameSrc), StandardCharsets.ISO_8859_1)) {
                Pattern pattern = Pattern.compile(selector);

                String result = stream.filter(pattern.asPredicate())
                        .map(s -> LineMatcher(s, fileNameSrc))
                        .collect(Collectors.joining("\n"));

                Files.write(Paths.get(dataDest + fileNameDest), result.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
    }
    
    public static String LineMatcher(String line, String fileName) {
        String regex = "";
        
        if (fileName == "ratings.list") {
            regex = "\\s+([0-9\\.*]+)\\s+(\\d+)\\s+([0-9\\.]+)\\s+(.+)\\s+\\((\\d{4}|\\?{4})+\\/?(\\w+)?\\)";
        }
        else if (fileName == "running-times.list") {
            regex = "(.+)\\s+\\((\\d{4}|\\?{4})+\\/?(\\w+)?\\)\\s+(\\((\\w+)\\)\\s+)?((\\{\\{\\w+\\}\\})\\s+)?((.+)[:])?((\\s*|\\.|\\+\\/\\-)\\d+)";
        } else {
            regex = "(.+)\\s+\\((\\d{4}|\\?{4})+\\/?(\\w+)?\\)\\s+(\\((\\w+)\\)\\s+)?((\\{\\{\\w+\\}\\})\\s+)?(.+)";
        }
        
        Matcher matcher = Pattern.compile(regex).matcher(line);
        
        if (matcher.find()) {
            String title = matcher.group(1);
            String year = matcher.group(2);
            String occurance = matcher.group(3);
                
            if (fileName == "ratings.list") {
                String votes = matcher.group(2);
                String rating = matcher.group(3);
                title = matcher.group(4);
                year = matcher.group(5);
                occurance = matcher.group(6);
                return title + "|" + year +"|" + occurance + "|" +
                        votes + "|" + rating;
            }
            else if (fileName == "running-times.list") {
                String country = matcher.group(9);
                String time = matcher.group(10);
                
                if (country != null) country = country.trim();
                
                return title + "|" + year +"|" + occurance + "|" + country + "|"
                        + time;
            }
            else {
                String country = matcher.group(8).trim();
                return title + "|" + year +"|" + occurance +
                        ((fileName != "movies.list") ? "|" + country : "");
            }
        }
        System.out.println(line);
        return "";
    }
}
