package bot.rat.services;

import bot.rat.entities.UserEntity;
import bot.rat.entities.Word;
import bot.rat.entities.embeddables.WordUserId;
import bot.rat.repositories.WordRepository;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordService {

    @Autowired
    WordRepository wordRepository;
    private final Comparator<Word> comparator = new WordCountComparator();

    private final List<String> rightResponses = List.of("Absolutely, boss", "I don't know, man, I don't think so",
            "UHM", "Depends", "Do you really need to ask?",
            "Somehow, it would seem so", "I don't like you", "Don't get it twisted", "No way, bro", "You can't be serious",
            "...", "Bruh", "Yep!", "Left", "What are you even talking about");

    public void recordWords(MessageReceivedEvent event, String message) {
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

    public TreeMap<String, Integer> getXdCountsById(UserService userService) {
        TreeMap<String, Integer> xdMap = new TreeMap<>();
        List<Word> wordList = new ArrayList<>();
        for (UserEntity u : userService.getAll()) {
            Word w = getWordByUser(u.getId(), "xd");
            wordList.add(w);
        }
        wordList.sort(comparator);
        for (Word w : wordList) {
            xdMap.put(w.id.userId, w.count);
        }
        return xdMap;
    }

    public List<Word> getTopWords(String userId) {
        List<Word> wordList = wordRepository.findAll().stream().filter(word -> word.id.userId.equals(userId)).collect(Collectors.toList());
        wordList.sort(comparator);
        if (wordList.size() >= 10) {
            wordList = wordList.subList(0, 9);
        }
        return wordList;
    }

    public String getRightResponse() {
        Random rand = new Random();
        return rightResponses.get(rand.nextInt(rightResponses.size()));
    }

    public Word getWordByUser(String userId, String word) {
        return wordRepository.findById(new WordUserId(word, userId))
                .orElse(new Word(new WordUserId(word, userId), 0));
    }

    public static class WordCountComparator implements Comparator<Word> {

        @Override
        public int compare(Word one, Word two) {
            return Integer.compare(two.count, one.count);
        }

    }
}
