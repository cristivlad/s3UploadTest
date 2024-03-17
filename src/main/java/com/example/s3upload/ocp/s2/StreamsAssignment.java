package com.example.s3upload.ocp.s2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamsAssignment {

  public static void main(String[] args) {
    System.out.println(averageOfInts());
    sortList();
    filterStreamAndFlatMap();
  }

  /**
   * Stream a list of int primitives between the range of 0 (inclusive) and 5 (exclusive). Calculate
   * and output the average
   *
   * @return
   */
  private static double averageOfInts() {
    return IntStream.range(0,5).average().getAsDouble();
  }

  private static void sortList() {
    List<Item> items = List.of(new Item(1, "Screw"), new Item(2, "Nail"), new Item(3, "Bolt"));
    items.stream().sorted(Comparator.comparing(Item::getName)).forEach(System.out::print);
  }

  private static void filterStreamAndFlatMap() {
    Stream.of(Arrays.asList("a","b"), Arrays.asList("c","a"), Arrays.asList("c", "d", "b"))
        .filter(list -> list.contains("c"))
        .flatMap(List::stream)
        .forEach(System.out::println);
  }

}
