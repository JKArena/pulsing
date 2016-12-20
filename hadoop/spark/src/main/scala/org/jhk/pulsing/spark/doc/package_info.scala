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
package org.jhk.pulsing.spark.doc

/**
 * More or less, doc for Scala as for Java 8 + EcmaScript 6, so not to refer to book while writing code
 * 
 * Mutable integer variable
 * var x:Int
 * 
 * Immutable integer value
 * val x:Int
 * 
 * Lazily evaluated integer value
 * lazy val x:Int
 * 
 * x.foo() is the same as x foo
 * x.foo(y) is the same as x foo y
 * x.::(y) is the same as y :: x
 * 
 * Scala also provides placeholder notation when defining anoynymous functions (a.k.a lambdas). This sytax uses the _ keyword as a placeholder for a 
 * function argument. If more thatn one placeholder is used, each consecutive placeholder refers to consecutive arguments to the function literal.
 * 
 * The scala.Predef object is automatically imported into scope by Scala. This places its members available to all programs. It's a handy mechanism for 
 * providing convenience functions to users, like directly writing println instead of Console's println or System.out.println. Predef also provides what 
 * it calls primitive widenings. These are a set of implicit conversions that automatically migrate from lower precision types to higher precision types
 * 
 * implicit def byte2short(x: Byte): Short = x.toShort
 * implicit def byte2int(x: Byte): Int = x.toInt
 * ...
 * 
 * These methods are calls to the runtime-conversion methods. The implicit before the method means the compiler may attempt to apply this method to a type Byte, 
 * if it's required for correct compilation.
 * 
 * Scala has no ?: syntax; you merely use if blocks.
 * var value = if(true) "true string" else "false string"
 * 
 * One of the keys to using expressions is realizing that there's no need for a return statement. An expression evalutes to a value, so there' no need to return.
 * def createErrorMessage(errorCode: Int) : String = errorCode.match {
 * 	case 1 => "Network Failure"
 * 	case 2 => "I/O Failure"
 * 	case _ => "Unknown Error"
 * }
 * 
 * class Point2(var x: Int, var y: Int) extends Equals {
 * 	def move(mx: Int, my: Int) : Unit = {
 * 		x = x + mx
 * 		y = y + my
 * 	}
 * 	override def hashCode() : Int = y + (31*x)
 * 	override def canEqual(that: Any) : Boolean = that match {
 * 		that.isInstanceOf[Point2]
 *  }
 *  override def equals(that: Any): Boolean = {
 *  	def strictEquals(other: Point2) = this.x == other.x && this.y == other.y
 *  
 *  	that match {
 *  		case p: Point2 => (p canEqual this) && strictEquals(p)
 *  		case _ => false
 *  	}
 *  }
 * }
 * 
 * val x = new Point2(1, 1)
 * x.##
 * 
 * Above is a convention in Scala. For compatibility with Java, Scala utilizes the same equals and hashCode methods defined on java.lang.Object. But 
 * Scala also abstracts primitives such that they appear as full objects. The compiler will box and unbox the primitives as needed for you. These primitive-like 
 * objects are all subtypes of scala.AnyVal whereas "standard" objects, those that would have extended java.lang.Object, are subtypes of scala.AnyRef. scala.AnyRef 
 * can be considered an alias for java.lang.Object. As the hashCode and equals methods are defined on AnyRef, Scala provides the methods ## and == that you can use 
 * for both AnyRef and AnyVal.ypes. BUt the equals and hashCode method are used when overriding the behavior. This split provides bettern runtime consistency 
 * and still retains Java interoperability.
 * 
 * In Scala, there's a scala.Equals trait that can help us fix this issue. The Equals trait defines a canEqual method that's used in tandem with the standard 
 * equals method. The canEqual method allows subclasses to opt out of their parent classes' equality implementation. This is done by allowing the other 
 * parameter in the equals method an opportunity to cause an equality failure.
 * 
 * In Scala, the ## method is equivalent to the hashCode method in Java as the == method is equivalent to the equals method in Java. In Scala, when calling the 
 * equals or hashCode method it's better to use ## and ==. These methods provide additional support for value t
 * 
 * scala.collection.immutable.HashMap uses the hash at the time of insertion to store values but doesn't update when an object's state mutates; in nutshell 
 * hashCode should never change.
 * 
 * import collection.mutable.{HashMap=>MutableHashMap}
 * 
 * Scala does its best to discourage the use of null in general programming. It does this through the scala.Option class found in the standard library. 
 * An Option can be considered a container of something or nothing. This is done through the two subclasses of Option: Some and None. Some denotes a container 
 * of exactly one item. None denotes an empty container, a role similar to what Nil plays for List.
 * 
 * var x : Option[String] = None
 * x.get => java.util.NoSuchElementException: None.get in
 * x.getOrElse("default") => String = default
 * 
 * x = Some("Now Initialized")
 * x.get => String = Now Initialized
 * 
 * Scala objects are instances of a singleton class. This class name is compiled as the name of the object with a $ appended to the end. A MODULE$ static 
 * field on this class is designed to be the sole instance.
 * 
 * class Bird
 * class Cat {
 * 	def catch(b: Bird): Unit = ...
 * 	def eat(): Unit = ...
 * }
 * 
 * val cat = new Cat
 * val bird = new Bird
 * 
 * trait Cat
 * trait Bird
 * trait Catch
 * trait FullTummy
 * 
 * def catch(hunter: Cat, prey: Bird): Cat with Catch
 * def eat(consumer: Cat with Catch): Cat with FullTummy
 * 
 * val story = (catch _) andThen (eat _)
 * The catch and eat methods use the type signatures to define the expected input and output states of the function. The 
 * with keyword is used to combine a type with another.
 * 
 * trait JdbcTemplate {
 * 	def query(psc: Connection => PreparedStatement, rowMapper: (ResultSet, Int) => AnyRef): List[AnyRef]
 * }
 * AnyRef is equivalent in Scala to java.lang.Object
 * 
 * object Predicates {
 * 	def or[T](f1: T => Boolean, f2: T => Boolean) = (t: T) => f1(t) || f2(t)  //Explicit anonymous function
 * 	def and[T](f1: T => Boolean, f2: T => Boolean) = (t: T) => f1(t) && f2(t)
 *  val notNull[T]: T => Boolean = _ != null //Placeholder function syntax
 * }
 * 
 * Scala 2.8.x brings with it the ability to use named parameters. Scala uses the static type of a variable to bind parameter names, 
 * however the defaults are determined by the runtime type. The unfortunate circumstance of named parameters in Scala is that they use 
 * the static type to determine ordering.
 * 
 * class Foo {
 * 	def foo(one: Int=1,
 * 					two: String="two",
 * 					three: Double=2.5): String = two + one+ three
 * }
 * 
 * val x = new Foo
 * x.foo() //uses default
 * x.foo(two="not two")
 * x.foo(0, "zero", 0.1)
 * x.foo(4, three=0.4)
 * 
 * Multiple inheritance
 * Scala traits are linearized 
 * trait Animal { def talk: String }
 * trait Mammal extends Animal { override def talk = "Wtf" }
 * trait Cat { override def talk = "Meow" }
 * val x = new Mammal with Cat
 * x.talk => Meow
 * 
 * ____________________________________________________________________________________________________________________________________________________________________
 * 
 * The Scala compiler provides several optimizations of functional style code into performance runtime bytecodes. The compiler will optimize tail recursion to 
 * execute as a looping construct at runtime, rather than a recursive function call. Tail recursion is when a method calls itself as the last statement, or its tail. 
 * Tail recursion can cause the stack to grow substantially if not optimized. Tail call optimization isn't as much about improving speed as preventing stack overflow 
 * errors.
 * 
 * The compiler can also optimize a pattern match that looks like a Java switch statement to act like a switch statement at runtime. The compiler can figure out 
 * if it's more efficient and still correct to use a branch lookup table. The compiler will then emit a tableswitch bytecode for this pattern match. The 
 * tableswitch bytecode is a branching statement that can be more efficient than multiple comparison branch statements. The switch and tail recursion optimizations 
 * come with optional annotations. The annotations will ensure the optimization is applied where expected or an error is issued.
 * 
 * For Scala to apply the tableswitch optimization, the following has to hold true:
 * 1) The matched value must be a known integer
 * 2) Every match expression must be "simple". It can't contain any type checks, if statements or extractors. The expression must also have its value available at 
 * compile time: The value of the expression must not be computed at runtime but instead always be the same value.
 * 3) There should be more than two case statements, otherwise the optimization is unneeded.
 * 
 * The compiler currently provides two annotations that can be used to prevent compilation if an optimization isn't applied. These are the @tailrec and @switch annotations, 
 * which you can apply to the expression you want optimized.
 * 
 * import annotation.switch
 * 
 * def annotated(x: Int) = (x: @switch) match {
 * 	case 1 => "One"
 * 	case 2 => "Two!"
 * 	case z => z + "?"
 * }
 * 
 * Tail call optimization can be applied to a method with @tailrec annotation. Tail call optimization is the conversion of a recursive function that calls itself as the last 
 * statement into something that won't absorb stack space but rather execute similarly to a traditional while or for loop. The JVM doesn't support TCO natively, so tail 
 * recursive methods will need to rely on Scala compiler performing the optimization.
 * 
 * To optimize tail calls, the Scala compiler requires the following:
 * 1) The method must be final or private: It can't be polymorphic.
 * 2) The method must have its return type annotated.
 * 3) The method must call itself as the "end" of one of its branches.
 * 
 * case class Node(name: String, edges: List[Node] = Nil)
 * def search(start: Node, predicate: Node => Boolean) = {
 * 	@tailrec
 * 	def loop(nodeQueue: List[Node], visited: Set[Node]): Option[Node] =
 * 		nodeQueue match {
 * 			case head :: tail if p(head) => Some(head)
 * 			case head :: tail if !visited.contains(head) => loop(tail ++ head.edges, visited + head);
 * 			case head :: tail => loop(tail, visited)
 * 			case Nil => None
 * 		} 
 * 	}
 * 	loop(List(start), Set())
 * }
 * 
 * @author Ji Kim
 */
object package_info {
  
}
