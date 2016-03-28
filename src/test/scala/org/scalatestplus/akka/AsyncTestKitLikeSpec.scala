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

import java.util.concurrent.ExecutionException

import akka.actor.ActorSystem
import org.scalatest.Assertion
import org.scalatest.time.{Milliseconds, Span}

import scala.concurrent.Future

class AsyncTestKitLikeSpec(system: ActorSystem) extends AsyncSpecBase(system) {

  def this() = this(ActorSystem("AsyncTestKitLike"))

  val span = Span(150, Milliseconds)

  "receivingA using patience" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingA[String],
      setOfGood = Set("hello world"),
      setOfBad = Set(1, 3.7, 'c'))
  }

  "receivingAn using patience" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingAn[Int],
      setOfGood = Set(1),
      setOfBad = Set("hello world", List()))
  }

  "receivingA using span" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingA[String](span),
      setOfGood = Set("hello world"),
      setOfBad = Set(1, 3.7, 'c'))
  }

  "receivingAn using span" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingAn[Int](span),
      setOfGood = Set(1),
      setOfBad = Set("hello world", List()))
  }

  "receiving using patience" should {
    behave like asyncReceiveSingleMessage(
      testing = receiving { case str: String => str },
      setOfGood = Set("hello world"),
      setOfBad = Set(1, 3.7, 'c'))

    "expose exceptions in partial function" in {
      val result = recoverToExceptionIf[IllegalArgumentException] {
        receiving { case _ => throw new IllegalArgumentException }
      }
      testActor ! 12345
      result.map(hasSucceeded)
    }
  }

  "receiving using span" should {
    behave like asyncReceiveSingleMessage(
      testing = receiving(span) { case str: String => str },
      setOfGood = Set("hello world"),
      setOfBad = Set(1, 3.7, 'c'))

    "expose exceptions in partial function" in {
      val result = recoverToExceptionIf[IllegalArgumentException] {
        receiving(span) { case _ => throw new IllegalArgumentException }
      }
      testActor ! 12345
      result.map(hasSucceeded)
    }
  }

  "receiveMsg using patience" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingMsg("hello world"),
      setOfGood = Set("hello world"),
      setOfBad = Set(1, "goodbye cruel world"))
    behave like asyncReceiveSingleMessageWithoutTimeout(
      testing = receivingMsg(42),
      setOfGood = Set(42),
      setOfBad = Set(53))
  }

  "receiveMsg using span" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingMsg("hello world", span),
      setOfGood = Set("hello world"),
      setOfBad = Set(1, "goodbye cruel world"))
    behave like asyncReceiveSingleMessageWithoutTimeout(
      testing = receivingMsg(42, span),
      setOfGood = Set(42),
      setOfBad = Set(53))
  }

  "receivingAnyClassOf using patience" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingAnyClassOf(classOf[String]),
      setOfGood = Set("hello world", "goodbye cruel world"),
      setOfBad = Set(1, 3.7, 'c'))
    behave like asyncReceiveSingleMessageWithoutTimeout(
      testing = receivingAnyClassOf(classOf[Double], classOf[Int]),
      setOfGood = Set(42, 3.14),
      setOfBad = Set("goodbye cruel world"))
  }

  "receivingAnyClassOf using span" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingAnyClassOf(span)(classOf[String]),
      setOfGood = Set("hello world", "goodbye cruel world"),
      setOfBad = Set(1, 3.7, 'c'))
    behave like asyncReceiveSingleMessageWithoutTimeout(
      testing = receivingAnyClassOf(span)(classOf[Double], classOf[Int]),
      setOfGood = Set(42, 3.14),
      setOfBad = Set("goodbye cruel world"))
  }

  "receivingAnyOf using patience" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingAnyOf(42, "hello world"),
      setOfGood = Set("hello world", 42),
      setOfBad = Set(1, 3.7, 'c'))
  }

  "receivingAnyOf using span" should {
    behave like asyncReceiveSingleMessage(
      testing = receivingAnyOf(span)(42, "hello world"),
      setOfGood = Set("hello world", 42),
      setOfBad = Set(1, 3.7, 'c'))
  }

  "receivingNoMsg using patience" should {
    behave like asyncReceiveNoMessage("Hello world", assertingReceiveNoMsg)
  }

  "receivingNoMsg using span" should {
    behave like asyncReceiveNoMessage("Hello world", assertingReceiveNoMsg(span))
  }


  "receiveN using patience" should {
    behave like asyncReceiveMultipleMessages(
      testing = receivingN(3),
      setOfGood = Set(Seq(1, 2, 3)),
      setOfBad = Set[Seq[Any]](),
      setOfTimeout = Set(Seq(1, 2)))
  }

  "receiveN using span" should {
    behave like asyncReceiveMultipleMessages(
      testing = receivingN(3, span),
      setOfGood = Set(Seq(1, 2, 3)),
      setOfBad = Set[Seq[Any]](),
      setOfTimeout = Set(Seq(1, 2)))
  }

  "receivingAllOf using patience" should {
    behave like asyncReceiveMultipleMessages(
      testing = receivingAllOf(1, 2, 3),
      setOfGood = Set(Seq(1, 2, 3)),
      setOfBad = Set(Seq(1, 2, 4)),
      setOfTimeout = Set(Seq(1, 2)))
  }

  "receivingAllOf using span" should {
    behave like asyncReceiveMultipleMessages(
      testing = receivingAllOf(span)(1, 2, 3),
      setOfGood = Set(Seq(1, 2, 3)),
      setOfBad = Set(Seq(1, 2, 4)),
      setOfTimeout = Set(Seq(1, 2)))
  }

  "receivingAllClassOf using patience" should {
    behave like asyncReceiveMultipleMessages(
      testing = receivingAllClassOf(classOf[Int], classOf[String], classOf[Char]),
      setOfGood = Set(Seq(1, "hello", 'c')),
      setOfBad = Set(Seq(1, 2, 4)),
      setOfTimeout = Set(Seq(1, "wait")))
  }

  "receivingAllClassOf using span" should {
    behave like asyncReceiveMultipleMessages(
      testing = receivingAllClassOf(span)(classOf[Int], classOf[String], classOf[Char]),
      setOfGood = Set(Seq(1, "hello", 'c')),
      setOfBad = Set(Seq(1, 2, 4)),
      setOfTimeout = Set(Seq(1, "wait")))
  }


  def asyncReceiveSingleMessageWithoutTimeout[A](testing: => Future[A],
                                                 setOfGood: Set[Any],
                                                 setOfBad: Set[Any]): Unit = {

    setOfGood foreach { msg =>
      s"Succeed - $msg" in {
        val result = testing
        testActor ! msg // Send message after creating future
        result.map(m => m shouldBe msg)
      }
    }

    setOfBad foreach { msg =>
      s"Fail if the message is incorrect - $msg" in {
        val result = recoverToExceptionIf[ExecutionException] {
          testing
        } map { ex =>
          ex.getCause shouldBe an[AssertionError]
        }
        testActor ! msg // Send message after future
        result
      }
    }
  }

  def asyncReceiveSingleMessage[A](testing: => Future[A],
                                   setOfGood: Set[Any],
                                   setOfBad: Set[Any]): Unit = {

    asyncReceiveSingleMessageWithoutTimeout(testing, setOfGood, setOfBad)

    s"Fail if the message times out" in {
      recoverToExceptionIf[ExecutionException] {
        testing
      } map { ex =>
        ex.getCause shouldBe an[AssertionError]
      }
    }
  }

  def asyncReceiveNoMessage(msg: Any, receive: => Future[Assertion]): Unit = {
    "succeed when it times out without receiving a message" in {
      receive
    }

    "fail if a message is received" in {
      val result = recoverToExceptionIf[ExecutionException] {
        receive
      } map { ex =>
        ex.getCause shouldBe an[AssertionError]
      }
      testActor ! msg
      result
    }
  }

  def asyncReceiveMultipleMessages[A](testing: => Future[Seq[A]],
                                      setOfGood: Set[Seq[Any]],
                                      setOfBad: Set[Seq[Any]],
                                      setOfTimeout: Set[Seq[Any]]): Unit = {
    setOfGood foreach { goodMsgs =>
      s"succeed when all messages are received - $goodMsgs" in {
        val result = testing
        goodMsgs foreach { msg =>
          testActor ! msg
        } // Send messages after creating future
        result.map(ms => ms shouldBe goodMsgs)
      }
    }

    setOfBad foreach { badMsgs =>
      s"Fail if the message is incorrect - $badMsgs" in {
        val result = recoverToExceptionIf[ExecutionException] {
          testing
        } map { ex =>
          ex.getCause shouldBe an[AssertionError]
        }
        badMsgs foreach { msg =>
          testActor ! msg
        } // Send messages after creating future
        result
      }
    }

    setOfTimeout foreach { timeoutMsgs =>
      s"Fail if the message times out - $timeoutMsgs" in {
        val result = recoverToExceptionIf[ExecutionException] {
          testing
        } map { ex =>
          ex.getCause shouldBe an[AssertionError]
        }
        timeoutMsgs foreach { msg =>
          testActor ! msg
        } // Send messages after creating future
        result
      }
    }
  }
}

