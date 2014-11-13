package cc.kusudart;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        // メソッド参照を変数に代入
        Consumer<String> consumer = System.out::println;
        consumer.accept("hello java 8 !");

        // Listの操作
        List<String> list = new ArrayList<>(Arrays.asList("java", "scala", "groovy"));
        list.sort((s1, s2) -> s1.length() - s2.length());
        // ラムダ式で出力
        list.forEach(s -> System.out.println(s));
        // listの各要素をラムダ式で大文字に変換
        list.replaceAll(s -> s.toUpperCase());
        // ラムダ式で指定した条件に合った要素を除去
        list.removeIf(s -> s.startsWith("J"));
        // メソッド参照で出力
        list.forEach(System.out::println);

        // Mapの操作
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        int i = map.getOrDefault("d", -1);
        System.out.println(i);
        BiConsumer sysout = (k, v) -> System.out.println(" -- " + k + " = " + v);
        map.forEach(sysout);
        map.putIfAbsent("d", 4);
        map.replace("a", 5);
        map.replaceAll((k, v) -> v * 2);
        map.forEach(sysout);
        BiFunction<String, Integer, Integer> remapping = (k, v) -> {
            System.out.println(" parameter key is " + k);
            System.out.println(" parameter value is " + v);
            return (v == null) ? 999 : v * 2;
        };
        // compute
        System.out.println(">> compute 1");
        map.compute("a", remapping);
        map.forEach(sysout);
        // compute
        System.out.println(">> compute 2");
        map.compute("e", remapping);
        map.forEach(sysout);
        // computeIfAbsent
        System.out.println(">> computeIfAbsent 1");
        map.computeIfAbsent("a", (k) -> {
            System.out.println(" parameter is " + k);
            return 100;
        });
        map.forEach(sysout);
        // computeIfPresent
        System.out.println(">> computeIfPresent 1");
        map.computeIfPresent("a", remapping);
        map.forEach(sysout);
        // merge
        System.out.println(">> merge 1");
        map.merge("f", 1000, (oldValue, newValue) -> {
            System.out.println(" parameter oldValue is " + oldValue);
            System.out.println(" parameter newValue is " + newValue);
            return oldValue + newValue;
        });
        map.forEach(sysout);
        // merge
        System.out.println(">> merge 2");
        map.merge("a", 1000, (oldValue, newValue) -> {
            System.out.println(" parameter oldValue is " + oldValue);
            System.out.println(" parameter newValue is " + newValue);
            return oldValue + newValue;
        });
        map.forEach(sysout);

        // ラムダ式を変数に代入
        TriFunction function = (a, b, c) -> a + b + c;
        int result = function.apply(1, 2, 3);
        System.out.println(result);

        String value = repeat("+").apply(10);
        System.out.println(value);

        outer("effectively final variable.").run();

        // インターフェースのstaticメソッドを用いたファクトリ
        Stream<String> stringStream = Stream.of("java", "groovy", "scala");
        stringStream.forEach(s -> System.out.println(" -- " + s));

        // Stream
        List<String> list1 = Arrays.asList("java", "scala", "groovy");
        list1.stream()
                .map(s -> s.toUpperCase())
                .sorted((a, b) -> b.length() - a.length())
                .forEach(System.out::println);
        // "j"で始まる要素のみにフィルタリング
        List<String> list2 = Arrays.asList("java", "javascript", "scala", "groovy", "kotlin");
        list2.stream()
                .filter(s -> s.startsWith("j"))
                .forEach(System.out::println);
        // 先頭の要素が同一の文字列をグルーピング
        Map<Character, List<String>> map1 = list2.stream()
                .collect(Collectors.groupingBy(s -> s.charAt(0)));
        System.out.println(map1.get('j'));
        System.out.println(map1.get('s'));
        System.out.println(map1.get('g'));
        System.out.println(map1.get('k'));
        // match
        System.out.println(list2.stream().allMatch(s -> s.startsWith("j")));
        System.out.println(list2.stream().anyMatch(s -> s.startsWith("j")));
        System.out.println(list2.stream().noneMatch(s -> s.startsWith("j")));
        // reduce
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        Optional<Integer> result1 = stream.reduce((a, b) -> a * b); // call terminal
        System.out.println(result1);
        try {
            Integer result2 = stream.reduce(1, (a, b) -> a * b); // throws IllegalStateException
            System.out.println(result2);
        } catch (IllegalStateException e) {
            System.out.println(" -reduce : IllegalStateException");
        }
        // IntStream
        IntStream intStream = IntStream.of(1, 2, 3, 4, 5);
        int total = intStream.sum();
        IntStream intStream1 = IntStream.range(1, 10); // 1-10までの値を返すIntStream
        Stream<String> stream1 = Stream.of("java", "scala", "groovy");
        IntStream intStream2 = stream1.mapToInt(s -> s.length()); // IntStreamに変換
        Stream<String> stream2 = Stream.of("java", "scala", "groovy");
        List<String> list3 = stream2.collect(Collectors.toList()); // Listに変換
        
    }

    /**
     * 独自FunctionalInterface
     */
    @FunctionalInterface
    interface TriFunction {
        public int apply(int a, int b, int c);
    }

    /**
     * ラムダ式を返すメソッド
     *
     * @param value
     * @return
     */
    public static Function<Integer,String> repeat(String value) {
        return (times) -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0; i<times; i++){
                stringBuilder.append(value);
            }
            return stringBuilder.toString();
        };
    }

    /**
     * 実質的final変数（effectively final variable）を参照するラムダ式
     */
    public static Runnable outer(String message) {
        Runnable runnable = () -> {
            // ラムダ式から外部の実質的final変数を参照可能
            System.out.println(message);
        };
        return runnable;
    }
}
