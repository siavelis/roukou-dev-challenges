package org.roukou.dev.challenges.april;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RuffleGift {

  public static void main(String[] args) {
    var participants = List
            .of("Dimitris", "Orestis", "John", "Riccardo", "Pavlos");

    Supplier<String> supplier = () -> participants.get(ThreadLocalRandom.current().nextInt(participants.size()));


    var winners = Stream.generate(supplier).limit(100).collect(Collectors.toList())
          .stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    var winner = winners.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .get().getKey();



    System.out.println("The winner is: " + winner + ". CONGRATS");
  }
}
