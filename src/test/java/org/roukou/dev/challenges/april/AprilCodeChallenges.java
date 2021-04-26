package org.roukou.dev.challenges.april;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
    public void challenge_one_character_shorting() {
      String input = "aaaaabbccccdeeeeeeaaafff";

      List<String> result = Arrays.asList(
          Arrays
              .stream(input.split(""))
              .reduce("", (res, character) -> {
                if (res.length() > 1) {
                  var last = res.substring(res.length() - 1);
                  if (!last.equals(character)) {
                    res = res + ",";
                  }
                }
                return res + character;
              })
              .split(",")
      );

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

      List<String> result = challenge_two_solution(input);

      assertEquals(Arrays.asList("charlie", "foxtrot"), result);
    }

    private List<String> challenge_two_solution(Stream<String> streamOfWords) {
      return streamOfWords.collect(groupingBy(String::length))
          .entrySet()
          .stream().max(Map.Entry.comparingByKey())
          .orElse(Map.entry(-1, Collections.emptyList()))
          .getValue();
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

      Map<Integer, Set<String>> result = challenge_three_solution(input);

      assertEquals(new HashSet<>(Arrays.asList("a", "c", "d")), result.get(1));
      assertEquals(new HashSet<>(Arrays.asList("a", "b", "e")), result.get(2));
      assertEquals(new HashSet<>(Arrays.asList("b", "c", "f")), result.get(3));
      assertEquals(new HashSet<>(Arrays.asList("d", "e", "f")), result.get(4));
      assertEquals(4, result.size());
    }


    private Map<Integer, Set<String>> challenge_three_solution(Map<String, Set<Integer>> input) {
      return input.entrySet()
          .stream()
          .map(entry -> entry.getValue().stream().map(i -> Pair.of(i, entry.getKey())).collect(toList()))
          .flatMap(Collection::stream)
          .collect(Collectors.groupingBy(Pair::getLeft))
          .entrySet()
          .stream().map(entry -> Map.entry(entry.getKey(), entry.getValue().stream().map(Pair::getRight).collect(toSet())))
          .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
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

      Map<Class<?>, Map<Boolean, Set<Class<?>>>> result = challenge_four_solution(origin);

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

    private Map<Class<?>, Map<Boolean, Set<Class<?>>>> challenge_four_solution(List<Class<?>> origin) {

      return origin.stream()
          .map(
              c -> Pair.of(
                  c,
                  Stream.concat(
                      Stream.concat(
                          Arrays.stream(c.getInterfaces()),
                          Arrays.stream(c.getInterfaces()).map(Class::getInterfaces).flatMap(Arrays::stream)
                      ),
                      Stream.concat(
                          Stream.of(c),
                          getSuperClassesStream(c).filter(cl -> !Modifier.isAbstract(cl.getModifiers()))
                      )
                  ).collect(toList())
              ))
          .map(pair ->
              Map.entry(
                  pair.getLeft(),
                  // map <true/false
                  Map.of(
                      Boolean.FALSE,
                      (Set<Class<?>>) pair.getRight().stream().filter(cl -> !cl.isInterface()).collect(Collectors.toCollection(LinkedHashSet::new)),
                      Boolean.TRUE,
                      (Set<Class<?>>) pair.getRight().stream().filter(Class::isInterface).collect(Collectors.toCollection(LinkedHashSet::new)))
              ))
          .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Stream<Class<?>> getSuperClassesStream(Class<?> c) {

      String rootClassName = Object.class.getName();
      Stream.Builder<Class<?>> streamBuilder = Stream.builder();

      do {
        c = c.getSuperclass();
        streamBuilder.add(c);
      } while (!Objects.equals(c.getName(), rootClassName));

      return streamBuilder.build();
    }

    // not used; since challenge_four does not check for Iterable.class
    private Stream<Class<?>> getInterfacesStream(Class<?> c) {

      Stream.Builder<Class<?>> streamBuilder = Stream.builder();
      Queue<Class<?>> interfacesQueue = new ArrayDeque<>(Arrays.asList(c.getInterfaces()));

      while (!interfacesQueue.isEmpty()) {
        Class<?> node = interfacesQueue.remove();
        interfacesQueue.addAll(Arrays.asList(node.getInterfaces()));

        streamBuilder.add(node);
      }

      return streamBuilder.build();
    }

  }
}
