import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class RunMeRead {
    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new GsonBuilder().create();

        List<Map<String, String>> messageList = gson.fromJson(new FileReader(new File("messages.bak")), List.class);

        messageList.stream()
                .filter(target -> target.get("out").equals("0"))
                .map(target -> target.get("body"))
                .map(String::toLowerCase)
                .map(target -> target.replaceAll("[^А-Яа-я ]", ""))
                .map(target -> target.replaceAll("[ ]{2,}", ""))
                .map(target -> target.replaceAll("^ ", ""))
                .filter(target -> !target.isEmpty())
                .flatMap(target -> Arrays.asList(target.split(" ")).stream())
                .filter(target -> target.length() > 2)
                .collect(groupingBy(Function.identity(),
                        collectingAndThen(counting(), Long::intValue)))
                .entrySet()
                .stream()
                .filter(target -> target.getValue() >= 5)
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach(System.out::println);
    }
}
