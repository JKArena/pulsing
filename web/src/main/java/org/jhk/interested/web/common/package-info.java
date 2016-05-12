/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Docs for Java 8 so to use during development
 * 
 * If a lambda has a statement expression as its body, it's compatible with a function descriptor that returns void 
 * (provided the parameter list is compatible too).
 * 
 * Predicate<String> predicate = str -> list.add(str); // Predicate has a boolean return
 * Consumer<String> consumer = str -> list.add(str); //Consumer has a void return
 * 
 * doSomething((Task)() -> SOP("")); //to avoid ambiguity
 * public static void doSomething(Runnable runnable)
 * public static void doSomething(Task task);
 * 
 * BufferedReaderProcessor brp = (BufferedReader br) -> br.readLine();
 * 
 * try(Bufferedreader br = new BufferedReader(new FileReader())){
 *  return brp.process(br);
 * }
 * 
 * ________________________________________________________________________________________________________________
 * 
 * Predicate interface defines an abstract method named test that accepts an object of generic type T and return a boolean
 * 
 * public interface Predicate<T> {
 *      boolean test(T t);
 * }
 * 
 * Consumer interface defines an abstract method named accept that takes an object of generic type T and returns no result (void).
 * 
 * public interface Consumer<T> {
 *      void accept(T t);
 * }
 * 
 * Function interface defines an abstract method named apply that takes an object of generic type T as input and returns an object of 
 * generic type R.
 * 
 * public interface Function<T, R> {
 *      R apply(T t);
 * }
 * 
 * Supplier
 * 
 * public interface Supplier<T> {
 *      T get()
 * }
 * ________________________________________________________________________________________________________________
 * 
 * An interface can now contain method signatures for which an implementing class doesn't provide an implementation.
 * Also it can contain static methods.
 * 
 * default void sort(Comparator<? super E> c) { //of List interface
 *      Collections.sort(this, c);
 * } 
 * 
 * ________________________________________________________________________________________________________________
 * 
 * 
 * Regular stream + parallel streams
 * 
 * list.stream() + list.parallelStream()
 * 
 * Various methods such as reduce, collect (groupingBy), filter, anyMatch, distinct, limit, collect, skip, map, and etc on streams
 * numbers.stream().reduce(0, (a, b) -> a + b) with initial value of 0. similar to hadoop
 * 
 * menu.stream().collect(
 * groupingBy(dish -> { dish.getCalories() <= 400 ? Calorie.DIET : Calorie.NORMAL })
 * );
 * 
 * IntStream, DoubleStream, and etc exists for respectable mapToInt, mapToDouble to avoid autoboxing costs (really 
 * is autoboxing worth it?). Can convert specific primitive stream to boxed stream by invoking boxed().
 * 
 * Stream.of("A", "B", "C").map(String::toUpperCase).forEach(System.out::println)
 * Stream<String> lines = Files.lines(...)
 * 
 * Stream.iterate and Stream.generate lets you create what we call an infinite stream: a stream that doesn't have 
 * a fixed size like when you create a stream from a fixed collection. Should use limit(n) on such streams.
 * Stream.iterate(0, n -> n +2).limit(10).forEach(System.out::println);
 * 
 * 
 * ________________________________________________________________________________________________________________
 * 
 * Method references are a shorthand for lambda expressions. For example Foo:bar is of (Foo foo) -> foo.bar();
 * 
 * Thread.currentThread()::dumpStack
 * String::substring
 * System.out::println
 * 
 * Can also have a method reference to an instance method of an existing object. For example local variable stuff 
 * that holds and object type Another, which supports an instance method getValue(); can write stuff::getValue. 
 * Not sure though if this is worth it, since might be confusing.
 * 
 * ________________________________________________________________________________________________________________
 * 
 * list.sort(Comparator.comparing(Foo:getValue).reversed()  //reverse order
 *              .thenComparing(Foo.getAnotherValue));       //chain comparison like promise
 * 
 * Also negate, and, or are nice usages to avoid multiple comparator as done before Java 8
 * 
 * ________________________________________________________________________________________________________________
 * 
 * Flattening streams
 * 
 * words.stream()
 *      .map(word -> word.split(" "))       //converts each word into an array of its individualletters
 *      .map(Arrays::stream)                //makes each array into a separate stream
 *      .distinct()
 *      .collect(toList())
 * 
 * above will result in Stream<Stream<String>>
 * 
 * words.stream()
 *      .map(word -> word.split(" "))
 *      .flatMap(Arrays::stream)
 *      .distinct()
 *      .collect(toList())
 * 
 * ________________________________________________________________________________________________________________
 * 
 * Fork/join framework works in work stealing philosophy. In practice, this means that the tasks are more or less 
 * evenly divided on all the threads in ForkJoinPool and each of these threads hold a doubly linked queue of the tasks 
 * assigned to it, and as soon as it completes a task it pulls another one from the head of the queue and starts executing 
 * it. When it finishes all tasks in the queue, the thread randomly chooses a queue of a different thread and "steals" a 
 * task, taking it from the tail of the queue.
 * 
 * Spliterator are like Iterators except they are used to traverse content in parallel.
 * 
 * ________________________________________________________________________________________________________________
 * 
 * Rules for default method resolution
 * 1) Classes always win.
 * 2) Sub-interfaces win. If B extends A, B is more specific than A.
 * 3) If the choice is still ambiguous, the class inheriting from multiple interfaces has to explicitly select which 
 * default method implementation to use. For this reason introduction of X.super.m(...) has been added
 * public class C implements B, A {
 *      void hello() {
 *          B.super.hello();
 *      }
 * }
 * 
 * ________________________________________________________________________________________________________________
 * 
 * Optional<Stuff> optStuff = Optional.ofNullable(stuff);
 * Optional<String> name = optStuff.map(Stuff::getName);
 * 
 * Optional doesn't implement Serializable.
 * 
 * ________________________________________________________________________________________________________________
 * 
 * CompletableFuture extends Future.
 * 
 * CompletableFuture.supplyAsync(() -> doSomething(stuff));
 * 
 * The supplyAsync method accepts a Supplier as argument and returns a CompletableFuture that will be asynchronously 
 * completed with the value obtained by invoking that Supplier. This Suppier will be run by one of the Executors in 
 * the ForkJoinPool, but you can specify a different Executor by passing it as a second argument to the overloaded 
 * version of this method.
 * 
 * return List<String>
 * List<CompletableFuture<Stirng>> stuffs = content.stream()
 *                                          .map(stuff -> CompletableFuture.supplyAsync( () -> stuff.getName()))
 *                                          .collect(Collectors.toList());
 * return stuffs.stream().map(CompletableFuture::join).collect(toList());
 * 
 * CompletableFutures have an advantage over parallel streams api b/c they allow you to specify a different 
 * Executor to submit the task (whereas parallel share the same ones).
 * 
 * CompletableFutures API provides thenCompose method allowing you to pipeline two asynchronous operations (like promise) 
 * passing the result of the first operation to the second operation when it becomes available. Can compose two 
 * CompletableFutures by invoking the thenCompose method on the first CompletableFuture and passing to it a Function.
 * 
 * The thenCompose method has a variant with an Async suffix. In general a method without the Async suffix in its name 
 * executes its task in the same thread as the previous task, whereas a method terminating with Async always submits the 
 * succeeding task to the thread pool, so each of the tasks can be handled by a different thread.
 * 
 * When you need to combine the results of the operations performed by two completely independent CompletableFutures and you 
 * don't want to wait for the first to complete before starting on the second use the thenCombine method which takes a 
 * second argument a BiFunction defining how the results of the two CompletableFutures are to be combined when they both become 
 * available.
 * 
 * CompletableFuture.allOf factory method takes as input an array of CompletableFutures and returns a CompletableFuture<Void> that's 
 * completed only when all the CompletableFutures passed have completed (Promise.all).
 * 
 * ________________________________________________________________________________________________________________
 * 
 * LocalDateTime and etc are of user whereas java.time.Instant is intended for use only by a machine.
 * int day = Instant.now().get(ChronoField.DAY_OF_MONTH); will just thrown an exception.
 * 
 * LocalDate date = LocalDate.of(2014, 3, 18);
 * Duration duration = Duration.between(time1, time2);
 * 
 * Period tenDays = Period.between(LocalDate.of(2014, 3, 8), LocalDate.of(2014, 3, 18));
 * 
 * java.time.temporal.TemporalAdjusters.* provide things such as 
 * LocalDate date = date.with(lastyDayOfMonth());
 * ________________________________________________________________________________________________________________
 * 
 * @author Ji Kim
 */
package org.jhk.interested.web.common;