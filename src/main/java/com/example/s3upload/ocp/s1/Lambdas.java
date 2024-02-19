package com.example.s3upload.ocp.s1;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambdas {

  public static void main(String[] args) {
    System.out.println("----- Consumer -----");
    Printable<String> printer = (String s) -> System.out.println(s);
    printer.print("Printable Lambda!");

    Consumer<String> lambdaConsumer = (String s) -> System.out.println(s);
    Consumer<String> methodConsumer = System.out::println;
    lambdaConsumer.accept("Consumer Lambda!");
    methodConsumer.accept("Consumer Method Reference!");

    System.out.println("----- Supplier -----");
    Retrievable<Integer> retrievable = () -> 77;
    System.out.println(retrievable.retrieve());

    Supplier<Integer> supplier = () -> 77;
    System.out.println(supplier.get());

    System.out.println("----- Predicate -----");
    Evaluate<Integer> evaluate = (Integer i) -> i < 0;
    System.out.println("Evaluate: " + evaluate.isNegative(-1));
    System.out.println("Evaluate: " + evaluate.isNegative(1));

    Predicate<Integer> predicate = (Integer i) -> i < 0;
    System.out.println("Predicate: " + predicate.test(-1));
    System.out.println("Predicate: " + predicate.test(1));

    System.out.println(check(4, (Integer i) -> i % 2 == 0));
    System.out.println(check(7, (Integer i) -> i % 2 == 0));
    System.out.println(check("Mr. Joe Bloggs", (String s) -> s.startsWith("Mr.")));
    System.out.println(check("Ms. Ann Bloggs", (String s) -> s.startsWith("Mr.")));
    Person mike = new Person("Mike", 33, 1.8);
    Person ann = new Person("Ann", 13, 1.4);
    System.out.println(check(mike, (Person p) -> p.age() > 18));
    System.out.println(check(ann, (Person p) -> p.age() > 18));

    System.out.println("----- Function -----");
    Functionable<Integer, String> function = (Integer i) -> "Number is: " + i;
    System.out.println(function.applyThis(25));
    Function<Integer, String> function2 = (Integer i) -> "Number is: " + i;
    System.out.println(function2.apply(25));

    System.out.println("----- Part 2 -----");
    List<Person> listPeople = getPeople();
    System.out.println("----- Sort Age -----");
    sortAge(listPeople);
    System.out.println("----- Sort Name -----");
    sortName(listPeople);
    System.out.println("----- Sort Height -----");
    sortHeight(listPeople);
  }

  public static <T> boolean check(T t, Predicate<T> predicate) {
    return predicate.test(t);
  }

  private static List<Person> sortHeight(List<Person> people) {
    people.sort(Comparator.comparingDouble(Person::height));
    people.forEach(System.out::println);
    return people;
  }

  private static List<Person> sortName(List<Person> people) {
    people.sort(Comparator.comparing(Person::name));
    people.forEach(System.out::println);
    return people;
  }

  private static List<Person> sortAge(List<Person> people) {
    people.sort(Comparator.comparingInt(Person::age));
    people.forEach(System.out::println);
    return people;
  }

  private static List<Person> getPeople() {
    List<Person> result = new ArrayList<>();
    result.add(new Person("Mike", 33, 1.8));
    result.add(new Person("Mary", 25, 1.4));
    result.add(new Person("Alan", 34, 1.7));
    result.add(new Person("Zoe", 30, 1.5));
    return result;
  }

}
