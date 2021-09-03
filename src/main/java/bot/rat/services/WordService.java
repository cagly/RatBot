package bot.rat.services;

import bot.rat.entities.Word;
import bot.rat.entities.embeddables.WordUserId;
import bot.rat.repositories.WordRepository;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WordService {

    @Autowired
    WordRepository wordRepository;

    public void recordWords(GuildMessageReceivedEvent event, String message) {
        Map<String, Word> wordCountMap = new HashMap<>();
        String userId = event.getAuthor().getId();
        String[] words = message.split(" ");
        for (String word : words) {
            if (word.length() > 2) {
                WordUserId uniqueId = new WordUserId(word, userId);
                if ( wordCountMap.containsKey(word) ) {
                    Word temp = wordCountMap.get(word);
                    temp.count++;
                    wordCountMap.put(word, temp);
                } else {
                    Word foundWord = wordRepository.findById(uniqueId).orElse(new Word(uniqueId, 0));
                    foundWord.count++;
                    wordCountMap.put(word, foundWord);
                }
            }
        }
        wordRepository.saveAll(wordCountMap.values());
    }
}
