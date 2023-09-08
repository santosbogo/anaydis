package anaydis.search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class QuijoteInverter {
    public static void main(String[] args) {

        try{
            BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/books/quijote.txt"));
            String line = reader.readLine();
            Set<String> words = new HashSet<>();
            while(line != null){
                String[] lineWords = line.split(" ");
                for( String word: lineWords){
                    words.add(word);
                }
                line = reader.readLine();
            }

            String reverseText = "";
            for (String word: words){
                StringBuilder builder = new StringBuilder();
                builder.append(word);
                builder.reverse().append(" ");
                reverseText += builder.toString();
            }

            Files.write(Paths.get("src/test/resources/books/etojiuq.txt"), reverseText.getBytes(StandardCharsets.UTF_8));

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Line not found");
        }
    }
}
