import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class RunMeRead {
    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new GsonBuilder().create();

        List<Map<String, String>> messageList = gson.fromJson(new FileReader(new File("messages.bak")), List.class);

        List<String> words = messageList.stream()
                .filter(target -> target.get("out").equals("1"))
                .map(target -> target.get("body"))
                .map(String::toLowerCase)
                .map(target -> target.replaceAll("[^А-Яа-я ]", ""))
                .map(target -> target.replaceAll("[ ]{2,}", ""))
                .map(target -> target.replaceAll("^ ", ""))
                .filter(target -> !target.isEmpty())
                .flatMap(target -> Arrays.asList(target.split(" ")).stream())
                .collect(Collectors.toList());

        print(analyzeWord(words, 5));
        print(analyzeSentence(words, 2, 5));
        print(analyzeSentence(words, 3, 5));
        print(analyzeSentence(words, 4, 5));
    }

    private static void print(List<String> lineList) {
        for (String line : lineList) {
            System.out.println(line);
        }
    }

    private static List<String> analyzeWord(List<String> words, int minimum) {
        return words.stream().filter(target -> target.length() > 2)
                .distinct()
                .collect(groupingBy(Function.identity(),
                        collectingAndThen(counting(), Long::intValue)))
                .entrySet()
                .stream()
                .filter(target -> target.getValue() >= minimum)
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(target -> generateResultString(target))
                .collect(Collectors.toList());
    }

    private static List<String> analyzeSentence(List<String> words, int count, int minimum) {
        Map<String, Integer> sentenceCounter = new HashMap<>();

        for (int i = count - 1; i < words.size(); i++) {
            String sentence = generateSentence(words, i, count);

            Integer integer = sentenceCounter.get(sentence);

            if (integer == null) {
                sentenceCounter.put(sentence, 1);
            } else {
                sentenceCounter.put(sentence, integer + 1);
            }
        }

        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(sentenceCounter.entrySet());

        Collections.sort(entries, Comparator.comparing(Map.Entry::getValue));

        List<String> resultList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : entries) {
            if (entry.getValue() >= minimum) {
                resultList.add(generateResultString(entry));
            }
        }

        return resultList;
    }

    private static String generateResultString(Map.Entry<String, Integer> entry) {
        return entry.getKey() + " [" + entry.getValue() + "]";
    }

    private static String generateSentence(List<String> words, int currentIndex, int count) {
        String result = "";

        for (int i = count - 1; i >= 0; i--) {
            result = result + words.get(currentIndex - i) + " ";
        }

        return result.trim();
    }
}
