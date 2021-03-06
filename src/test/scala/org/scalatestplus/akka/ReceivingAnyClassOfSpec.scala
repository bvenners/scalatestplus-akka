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

import akka.actor.ActorSystem
import akka.testkit.TestActors
import org.scalatest.{AsyncWordSpecLike, BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatest.time.{Seconds, Span}
import java.util.concurrent.ExecutionException

class ReceivingAnyClassOfSpec(system: ActorSystem) extends AsyncSpecBase(system) {

  def this() = this(ActorSystem("ReceivingAnyClassOfSpec"))
  
  "receivingAnyClassOf" should {
    "async send back messages of same type" in {
      echo ! "hello world"
      val fut = receivingAnyClassOf(classOf[String])
      fut.map(_ => succeed)
    }
    "async send back messages of same type with span" in {
      echo ! "hello world"
      val fut = receivingAnyClassOf(Span(1, Seconds))(classOf[String])
      fut.map(_ => succeed)
    }
    "match from list of classes" in {
      echo ! "hello world"
      val fut = receivingAnyClassOf(classOf[Int], classOf[Double], classOf[String], classOf[Any])
      fut.map(_ => succeed)
    }
    "should not match for number classes" in {
      echo ! "hello world"
      val futureEx = recoverToExceptionIf[ExecutionException] {
        receivingAnyClassOf(classOf[Int], classOf[Double])
      }
      futureEx.map(ex => Option(ex.getCause).map(cause => cause.getClass) shouldBe Some(classOf[AssertionError]))
    }
  }
  "assertingReceiveAnyClassOf" should {
    "async send back messages of same type" in {
      echo ! "hello world"
      assertingReceiveAnyClassOf(classOf[String])
    }
    "async send back messages of same type with span" in {
      echo ! "hello world"
      assertingReceiveAnyClassOf(Span(1, Seconds))(classOf[String])
    }
    "match from list of classes" in {
      echo ! "hello world"
      assertingReceiveAnyClassOf(classOf[Int], classOf[Double], classOf[String], classOf[Any])
    }
    "should not match for number classes" in {
      echo ! "hello world"
      val futureEx = recoverToExceptionIf[ExecutionException] {
        assertingReceiveAnyClassOf(classOf[Int], classOf[Double])
      }
      futureEx.map(ex => Option(ex.getCause).map(cause => cause.getClass) shouldBe Some(classOf[AssertionError]))
    }
  }
}

