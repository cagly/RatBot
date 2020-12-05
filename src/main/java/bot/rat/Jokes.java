package bot.rat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Jokes {

    public List<String> jokeList = new ArrayList<>();
    BufferedReader in = new BufferedReader(new FileReader("resources/Jokes.txt"));
    Random rand = new Random();

    public Jokes() throws IOException {
        String str;
        while((str=in.readLine())!= null) {
            if (str.length() > 1) {
                jokeList.add(str);
            }
        }
    }

    public String getJoke() {
        return jokeList.get(rand.nextInt(jokeList.size()));
    }

}
