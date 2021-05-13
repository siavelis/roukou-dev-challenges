package org.roukou.dev.challenges.april;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

public class AprilCodeChallenges {

  @Nested
  class WeekThreeChallenges {

    @Test
    void custom_solution_by_thomas() {
      List<String> input =
          List.of("alfa", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel");

      int max = input.stream().mapToInt(String::length).max().orElse(-1);
      int min = input.stream().mapToInt(String::length).min().orElse(-1);

      var result =
          input.stream()
              .collect(
                  CustomCollectors.pairing(
                      Collectors.filtering(s -> s.length() == max, toList()),
                      Collectors.filtering(s -> s.length() == min, toList()),
                      Pair::of));

      assertAll(
          "Longest and Shortest Words",
          () -> assertEquals(List.of("charlie", "foxtrot"), result.getLeft()),
          () -> assertEquals(List.of("alfa", "echo", "golf"), result.getRight()));
    }
  }

  @Nested
  class WeekFourChallenges {

    /**
     * Given a string, split it into a list of strings consisting of consecutive characters from the
     * original string.
     */
    @Test
    public void challenge_one_character_shorting_() {
      String input = "aaaaabbccccdeeeeeeaaafff";

      List<Integer> splitPositions =
          IntStream.range(0, input.length())
              .filter(i -> i == 0 || (i > 0 && input.charAt(i) != input.charAt(i - 1)))
              .mapToObj(i -> i)
              .collect(Collectors.toList());

      List<String> result =
          IntStream.range(0, splitPositions.size())
              .mapToObj(
                  i ->
                      i != splitPositions.size() - 1
                          ? input.substring(splitPositions.get(i), splitPositions.get(i + 1))
                          : input.substring(splitPositions.get(i)))
              .collect(Collectors.toList());

      assertEquals("[aaaaa, bb, cccc, d, eeeeee, aaa, fff]", result.toString());
    }

    @Test
    public void challenge_one_character_shorting() {
      String input = "aaaaabbccccdeeeeeeaaafff";

      // TODO: Add your code here
      // Two-pass approach:
      // (1) gather data about the boundaries between the runs,
      // (2) create the substrings based on output from the first.
      List<String> result =
          Arrays.asList(
              Arrays.stream(input.split(""))
                  .reduce(
                      "",
                      (res, character) -> {
                        if (res.length() > 1) {
                          var last = res.substring(res.length() - 1);
                          if (!last.equals(character)) {
                            res = res + ",";
                          }
                        }
                        return res + character;
                      })
                  .split(","));

      assertEquals("[aaaaa, bb, cccc, d, eeeeee, aaa, fff]", result.toString());
    }

    /**
     * Select the longest words from an input stream. That is, select the words whose lengths are
     * equal to the maximum word length.
     */
    @Test
    public void challenge_two_select_longest_word_in_one_pass() {
      Stream<String> input =
          Stream.of("alfa", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel")
              .parallel();

      // TODO:
      // create a helper class with four functions, and then pass method
      // references to these functions to the Collector.of() method.
      List<String> result =
          input.collect(Collector.of(Longest::new, Longest::acc, Longest::comb, Longest::finish));

      assertEquals(Arrays.asList("charlie", "foxtrot"), result);
    }

    class Longest {
      int len = -1;
      List<String> list = new ArrayList<>();

      void acc(String s) {
        int slen = s.length();
        if (slen == len) {
          list.add(s);
        } else if (slen > len) {
          len = slen;
          list.clear();
          list.add(s);
        } // ignore input string if slen < len
      }

      Longest comb(Longest other) {
        if (this.len > other.len) {
          return this;
        } else if (this.len < other.len) {
          return other;
        } else {
          this.list.addAll(other.list);
          return this;
        }
      }

      List<String> finish() {
        return list;
      }
    }

    /**
     * Given a Map<X, Set<Y>>, convert it to Map<Y, Set<X>>. Each set member of the input map's
     * values becomes a key in the result map. Each key in the input map becomes a set member of the
     * values of the result map. In the input map, an item may appear in the value set of multiple
     * keys. In the result map, that item will be a key, and its value set will be its corresponing
     * keys from the input map.
     *
     * <p>In this case the input is Map<String, Set<Integer>> and the result is Map<Integer,
     * Set<String>>.
     */
    @Test
    public void challege_three_invert_multimap() {
      Map<String, Set<Integer>> input = new HashMap<>();
      input.put("a", new HashSet<>(Arrays.asList(1, 2)));
      input.put("b", new HashSet<>(Arrays.asList(2, 3)));
      input.put("c", new HashSet<>(Arrays.asList(1, 3)));
      input.put("d", new HashSet<>(Arrays.asList(1, 4)));
      input.put("e", new HashSet<>(Arrays.asList(2, 4)));
      input.put("f", new HashSet<>(Arrays.asList(3, 4)));

      // TODO:
      // flatten the input structure in one stage
      // of the pipeline and then to create the result structure using a collector.
      Map<Integer, Set<String>> result =
          input.entrySet().stream()
              .flatMap(
                  e -> e.getValue().stream().map(v -> new AbstractMap.SimpleEntry<>(e.getKey(), v)))
              .collect(
                  Collectors.groupingBy(
                      Map.Entry::getValue,
                      Collectors.mapping(Map.Entry::getKey, Collectors.toSet())));

      assertEquals(new HashSet<>(Arrays.asList("a", "c", "d")), result.get(1));
      assertEquals(new HashSet<>(Arrays.asList("a", "b", "e")), result.get(2));
      assertEquals(new HashSet<>(Arrays.asList("b", "c", "f")), result.get(3));
      assertEquals(new HashSet<>(Arrays.asList("d", "e", "f")), result.get(4));
      assertEquals(4, result.size());
    }

    /**
     * Write a method that extracts all the superclasses and their implemented classes. Filter out
     * the abstract classes, then create a map with two boolean keys, true is associated to the
     * interfaces and false with the concrete classes. Do that for the provided classes, and arrange
     * the result in a Map<Class, ...> with those classes as the keys.
     */
    @Test
    public void challenge_four_map_of_interfaces_and_classes() {

      List<Class<?>> origin = List.of(ArrayList.class, HashSet.class, LinkedHashSet.class);

      // TODO: Provide your solution here

      Function<Class<?>, Stream<Class<?>>> superClasses =
          clazz ->
              Stream.<Class<?>>iterate(clazz, Class::getSuperclass).takeWhile(Objects::nonNull);

      Function<Stream<? extends Class<?>>, Stream<? extends Class<?>>> classAndInterfaces =
          stream ->
              stream
                  .flatMap(
                      clazz -> Stream.of(Stream.of(clazz), Arrays.stream(clazz.getInterfaces())))
                  .flatMap(Function.identity());

      Function<Class<?>, Stream<? extends Class<?>>> superClassesAndInterfaces =
          superClasses.andThen(classAndInterfaces);

      Predicate<Class<?>> isConcrete = c -> !Modifier.isAbstract(c.getModifiers());
      Predicate<Class<?>> isInterface = Class::isInterface;
      Predicate<Class<?>> isInterfaceOrConcreteClass = isInterface.or(isConcrete);

      // 1) To understand the algorithm, write out the previous processing as a stream pattern.
      //    This isn't used directly, but will be converted to a collector below.
      Map<Boolean, Set<Class<?>>> unusedResult =
          origin.stream()
              .flatMap(superClassesAndInterfaces)
              .filter(isInterfaceOrConcreteClass)
              .collect(Collectors.partitioningBy(isInterface, Collectors.toSet()));

      // 2) Convert the processing to a collector
      Collector<Class<?>, ?, Map<Boolean, Set<Class<?>>>> collector =
          Collectors.flatMapping(
              superClassesAndInterfaces,
              Collectors.filtering(
                  isInterfaceOrConcreteClass,
                  Collectors.partitioningBy(isInterface, Collectors.toSet())));

      // 3) use it as a downstream collector
      Map<Class<?>, Map<Boolean, Set<Class<?>>>> result =
          origin.stream().collect(Collectors.groupingBy(Function.identity(), collector));

      assertEquals(
          Map.of(
              ArrayList.class,
              Map.of(
                  false, Set.of(ArrayList.class, Object.class),
                  true,
                      Set.of(
                          List.class,
                          RandomAccess.class,
                          Cloneable.class,
                          Serializable.class,
                          Collection.class)),
              HashSet.class,
              Map.of(
                  false, Set.of(HashSet.class, Object.class),
                  true,
                      Set.of(
                          Set.class, Cloneable.class,
                          Serializable.class, Collection.class)),
              LinkedHashSet.class,
              Map.of(
                  false, Set.of(LinkedHashSet.class, HashSet.class, Object.class),
                  true,
                      Set.of(
                          Set.class, Cloneable.class,
                          Serializable.class, Collection.class))),
          result);
    }
  }
}
