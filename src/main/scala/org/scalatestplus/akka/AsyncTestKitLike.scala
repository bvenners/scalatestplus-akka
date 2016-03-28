/*
 * Copyright 2016 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatestplus.akka

import akka.testkit.TestKitBase
import org.scalatest._
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.Span

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

trait AsyncTestKitLike extends TestKitBase with AsyncTestSuiteMixin with PatienceConfiguration {
  this: AsyncTestSuite =>

  /**
    * An object which is an instance of the given type (after erasure) must be received within
    * the allotted time frame; the object will be returned.
    *
    * @tparam T the type of the expected message
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingA[T: ClassTag](implicit config: PatienceConfig): Future[T] = {
    val fd = FiniteDuration(config.timeout.length, config.timeout.unit)
    Future(expectMsgType[T](fd))
  }

  /**
    * Synonym for `receivingA`.
    */
  def receivingAn[T: ClassTag](implicit config: PatienceConfig): Future[T] = {
    val fd = FiniteDuration(config.timeout.length, config.timeout.unit)
    Future(expectMsgType[T](fd))
  }

  /**
    * An object which is an instance of the given type (after erasure) must be received within
    * the allotted time frame; the object will be returned.
    *
    * @tparam T the type of the expected message
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingA[T: ClassTag](timeout: Span): Future[T] = Future {
    expectMsgType[T](asFiniteDuration(timeout))
  }

  /**
    * Synonym for `receivingA`.
    */
  def receivingAn[T: ClassTag](span: Span): Future[T] = {
    val fd = FiniteDuration(span.length, span.unit)
    Future(expectMsgType[T](fd))
  }


  /**
    * Within the given time period, a message must be received and the given partial function
    * must be defined for that message; the result from applying the partial function to the
    * received message is returned. The duration may be left unspecified (empty parentheses are
    * required in this case) to use the deadline from the innermost enclosing within block instead.
    *
    * Use this variant to implement more complicated or conditional
    * processing.
    *
    * @tparam T the type of the value returned by `pf`
    * @param pf     the partial function used to verify and transform the message
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received message as transformed by the partial function
    */
  def receiving[T](pf: PartialFunction[Any, T])(implicit config: PatienceConfig): Future[T] = {
    receiving(config.timeout)(pf)
  }

  /**
    * Within the given time period, a message must be received and the given partial function
    * must be defined for that message; the result from applying the partial function to the
    * received message is returned. The duration may be left unspecified (empty parentheses are
    * required in this case) to use the deadline from the innermost enclosing within block instead.
    *
    * Use this variant to implement more complicated or conditional
    * processing.
    *
    * @tparam T the type of the value returned by `pf`
    * @param pf      the partial function used to verify and transform the message
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received message as transformed by the partial function
    */
  def receiving[T](timeout: Span)(pf: PartialFunction[Any, T]): Future[T] = Future {
    expectMsgPF[T](asFiniteDuration(timeout))(pf)
  }

  /**
    * The given message object must be received within the specified time; the object will be returned.
    *
    * @tparam T the type of the value expected
    * @param msg    the expected message
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingMsg[T](msg: T)(implicit config: PatienceConfig): Future[T] = {
    receivingMsg(msg, config.timeout)
  }

  /**
    * The given message object must be received within the specified time; the object will be returned.
    *
    * @tparam T the type of the value expected
    * @param msg     the expected message
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingMsg[T](msg: T, timeout: Span): Future[T] = Future {
    expectMsg(asFiniteDuration(timeout), msg)
  }

  /**
    * An object must be received within the given time, and it must be an instance of
    * at least one of the supplied Class objects; the received object will be returned.
    *
    * @tparam T the common type of the expected `classes`
    * @param classes The classes of message to accept
    * @param config  the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingAnyClassOf[T](classes: Class[_ <: T]*)(implicit config: PatienceConfig): Future[T] = {
    receivingAnyClassOf(config.timeout)(classes: _*)
  }

  /**
    * An object must be received within the given time, and it must be an instance of
    * at least one of the supplied Class objects; the received object will be returned.
    *
    * @tparam T the common type of the expected `classes`
    * @param classes The classes of message to accept
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingAnyClassOf[T](timeout: Span)(classes: Class[_ <: T]*): Future[T] = Future {
    expectMsgAnyClassOf(asFiniteDuration(timeout), classes: _*)
  }

  /**
    * An object must be received within the given time, and it must be equal (compared
    * with ==) to at least one of the passed reference objects; the received object will be returned.
    *
    * @tparam T the common subtype of the expected `objs`
    * @param objs   The classes of message to accept
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingAnyOf[T](objs: T*)(implicit config: PatienceConfig): Future[T] = {
    receivingAnyOf(config.timeout)(objs: _*)
  }

  /**
    * An object must be received within the given time, and it must be equal (compared
    * with ==) to at least one of the passed reference objects; the received object will be returned.
    *
    * @tparam T the common subtype of the expected `objs`
    * @param objs    The classes of message to accept
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingAnyOf[T](timeout: Span)(objs: T*): Future[T] = Future {
    expectMsgAnyOf(asFiniteDuration(timeout), objs: _*)
  }

  /**
    * No message must be received within the given time. This also fails if a message has
    * been received before calling this method which has not been removed from the queue
    * using one of the other methods.
    *
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the `Assertion`
    */
  def assertingReceiveNoMsg(implicit config: PatienceConfig): Future[Assertion] = {
    assertingReceiveNoMsg(config.timeout)
  }

  /**
    * No message must be received within the given time. This also fails if a message has
    * been received before calling this method which has not been removed from the queue
    * using one of the other methods.
    *
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the `Assertion`
    */
  def assertingReceiveNoMsg(timeout: Span): Future[Assertion] = {
    Future {
      expectNoMsg(asFiniteDuration(timeout))
    }.map(hasSucceeded)
  }

  /**
    * n messages must be received within the given time; the received messages are returned.
    *
    * @param n      the number of messages to receive
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of received messages in `Seq` length `n`
    */
  def receivingN[T](n: Int)(implicit config: PatienceConfig): Future[Seq[Any]] = {
    receivingN(n, config.timeout)
  }

  /**
    * n messages must be received within the given time; the received messages are returned.
    *
    * @param n       the number of messages to receive
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of received messages in `Seq` length `n`
    */
  def receivingN[T](n: Int, timeout: Span): Future[Seq[Any]] = Future {
    receiveN(n, asFiniteDuration(timeout))
  }

  /**
    * A number of objects matching the size of the supplied object array must be received within
    * the given time, and for each of the given objects there must exist at least one among the received
    * ones which equals (compared with ==) it. The full sequence of received objects is returned.
    *
    * @tparam T the common subtype of the expected `objs`
    * @param objs    The messages to accept
    * @param config  the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received messages
    */
  def receivingAllOf[T](objs: T*)(implicit config: PatienceConfig): Future[Seq[T]] = {
    receivingAllOf(config.timeout)(objs: _*)
  }

  /**
    * A number of objects matching the size of the supplied object array must be received within
    * the given time, and for each of the given objects there must exist at least one among the received
    * ones which equals (compared with ==) it. The full sequence of received objects is returned.
    *
    * @tparam T the common subtype of the expected `objs`
    * @param objs    The messages to accept
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received messages
    */
  def receivingAllOf[T](timeout: Span)(objs: T*): Future[Seq[T]] = Future {
    expectMsgAllOf(asFiniteDuration(timeout), objs: _*)
  }


  /**
    * A number of objects matching the size of the supplied Class array must be received within
    * the given time, and for each of the given classes there must exist at least one among the
    * received objects whose class equals (compared with ==) it (this is not a conformance check).
    * The full sequence of received objects is returned.
    *
    * @tparam T the common subtype of the expected messages
    * @param classes    The types of messages to accept
    * @param config  the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received messages
    */
  def receivingAllClassOf[T](classes: Class[_ <: T]*)(implicit config: PatienceConfig): Future[Seq[T]] = {
    receivingAllClassOf(config.timeout)(classes: _*)
  }

  /**
    * A number of objects matching the size of the supplied Class array must be received within
    * the given time, and for each of the given classes there must exist at least one among the
    * received objects whose class equals (compared with ==) it (this is not a conformance check).
    * The full sequence of received objects is returned.
    *
    * @tparam T the common subtype of the expected messages
    * @param classes    The types of messages to accept
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received messages
    */
  def receivingAllClassOf[T](timeout: Span)(classes: Class[_ <: T]*): Future[Seq[T]] = Future {
    expectMsgAllClassOf(asFiniteDuration(timeout), classes: _*)
  }

  /**
    * Simple function to turn any future returned by `receive*` method into an assertion required to
    * terminate a test case
    *
    * {{{
    * "An echo actor" should {
    *   "reply with a message of the same type" in {
    *     echo ! "hello world"
    *     receivingA[String].map(hasSucceeded)
    *   }
    * }
    * }}}
    *
    * @tparam T
    * @return
    */
  def hasSucceeded[T]: T => Assertion = { _: T => Succeeded }


  private def asFiniteDuration(timeout: Span): FiniteDuration = {
    FiniteDuration(timeout.length, timeout.unit)
  }
}

