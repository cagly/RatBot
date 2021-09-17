package bot.rat.services;

import bot.rat.entities.Word;
import bot.rat.entities.embeddables.WordUserId;
import bot.rat.repositories.WordRepository;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WordService {

    @Autowired
    WordRepository wordRepository;
    private final Comparator<Word> comparator = new WordCountComparator();

    public void recordWords(GuildMessageReceivedEvent event, String message) {
        try {
            Map<String, Word> wordCountMap = new HashMap<>();
            String userId = event.getAuthor().getId();
            String[] words = message.split(" ");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].toLowerCase();
                words[i] = words[i].replaceAll("[^a-z^\\d]", "");
            }
            for (String word : words) {
                if (word.length() > 2 && word.length() < 20) {
                    WordUserId uniqueId = new WordUserId(word, userId);
                    if (wordCountMap.containsKey(word)) {
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
        } catch (Exception e) {

        }
    }

    public List<Word> getTopWords(String userId) {
        List<Word> wordList = wordRepository.findAll().stream().filter(word -> word.id.userId.equals(userId)).collect(Collectors.toList());
        wordList.sort(comparator);
        if (wordList.size() >= 10) {
            wordList = wordList.subList(0, 9);
        }
        return wordList;
    }

    public static class WordCountComparator implements Comparator<Word> {

        @Override
        public int compare(Word one, Word two) {
            return Integer.compare(two.count, one.count);
        }

    }
}
