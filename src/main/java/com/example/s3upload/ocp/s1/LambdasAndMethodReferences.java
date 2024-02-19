package com.example.s3upload.ocp.s1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdasAndMethodReferences {

  public static void main(String[] args) {
    staticMR();
    boundMR();
    unboundMR();
    constructorMR();
  }

  public static void staticMR() {
    List<Integer> list = List.of(1, 2, 7, 4, 5);
    Consumer<List<Integer>> sortConsumer = l -> Collections.sort(l);
    Consumer<List<Integer>> sortConsumerMethodRef = Collections::sort;
    List<Integer> mutableList = new ArrayList<>(list);
    List<Integer> mutableList1 = new ArrayList<>(list);
    sortConsumer.accept(mutableList);
    sortConsumerMethodRef.accept(mutableList1);
    System.out.println("Initial List: " + list);
    System.out.println("Sorted list with lambda: " + mutableList);
    System.out.println("Sorted list with method reference: " + mutableList1);
  }

  public static void boundMR() {
    System.out.println("----- Bound Method Reference -----");
    String name = "Mr. Joe Bloggs";
    Predicate<String> startsWithMr = s -> name.startsWith(s);
    Predicate<String> startsWith = name::startsWith;
    System.out.println(startsWithMr.test("Mr."));
    System.out.println(startsWithMr.test("Ms."));
    System.out.println(startsWith.test("Mr."));
    System.out.println(startsWith.test("Ms."));
  }

  public static void unboundMR() {
    System.out.println("----- Unbound Method Reference -----");
    Predicate<String> stringIsEmtpy = String::isEmpty;
    System.out.println(stringIsEmtpy.test(""));
    System.out.println(stringIsEmtpy.test("xyz"));

    BiPredicate<String, String> stringEquals = (s1, s2) -> s1.startsWith(s2);
    System.out.println(stringEquals.test("Mr. Joe Bloggs", "Mr."));
    System.out.println(stringEquals.test("Mr. Joe Bloggs", "Ms."));

    BiPredicate<String, String> stringEquals1 = String::startsWith;
    System.out.println(stringEquals1.test("Mr. Joe Bloggs", "Mr."));
    System.out.println(stringEquals1.test("Mr. Joe Bloggs", "Ms."));
  }

  public static void constructorMR() {
    System.out.println("----- Constructor Method Reference -----");
    Supplier<List<String>> listSupplier = ArrayList::new;
    List<String> list = listSupplier.get();
    list.add("Lambda");
    System.out.println(list);

    Function<Integer, List<String>> listFunction = initialCapacity -> new ArrayList<String>(initialCapacity);
    List<String> list1 = listFunction.apply(10);
    list1.add("Lambda");
    System.out.println(list1);

    Function<Integer, List<String>> listFunction1 = ArrayList::new;
    List<String> list2 = listFunction1.apply(10);
    list2.add("Lambda");
    System.out.println(list2);
  }

}
