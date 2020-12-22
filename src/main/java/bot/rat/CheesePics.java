package bot.rat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheesePics {

    public List<String> cheeseList = new ArrayList<>();
    BufferedReader in = new BufferedReader(new FileReader("resources/cheeseList.txt"));
    Random rand = new Random();

    public CheesePics() throws IOException {
        String str;
        while((str=in.readLine())!= null) {
            if (str.length() > 1) {
                cheeseList.add(str);
            }
        }
    }

    public String getJoke() {
        return cheeseList.get(rand.nextInt(cheeseList.size()));
    }
}
