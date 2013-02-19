package com.github.tototoshi.scalikejdbc.config

import org.scalatest.FunSpec
import org.scalatest.matchers._
import scalikejdbc._

class TypesafeConfigReaderSpec extends FunSpec with ShouldMatchers {

  def fixture = new {

  }

  describe("TypesafeConfigReader") {

    it ("should get configuration by db name") {
      val expected = Map(
        "driver" -> "org.h2.Driver",
        "url" -> "jdbc:h2:memory",
        "user" -> "sa",
        "password" -> "secret"
      )
      TypesafeConfigReader.readAsMap('foo) should be (expected)
    }

    it ("should get db names") {
      val expected = List("default", "foo", "bar")
      TypesafeConfigReader.dbNames should be (expected)
    }
  }

}
