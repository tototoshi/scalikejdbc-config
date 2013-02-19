package com.github.tototoshi.scalikejdbc.config

import org.scalatest.FunSpec
import org.scalatest.matchers._
import scalikejdbc._

class ConfigSpec extends FunSpec with ShouldMatchers {

  def fixture = new {

  }

  describe("Config") {

    it ("should get configuration by db name") {
      val expected = Map(
        "driver" -> "org.h2.Driver",
        "url" -> "jdbc:h2:memory",
        "user" -> "sa",
        "password" -> "secret"
      )
      Config('foo) should be (expected)
    }

    it ("should get db names") {
      val expected = List("default", "foo", "bar")
      Config.dbNames should be (expected)
    }

    describe ("#setup") {
      it ("should setup default connection with no argument") {
        Config.setup()
        val res = DB readOnly { implicit session =>
          SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
        }
        res should be (Some(1))
        Config.close()
      }
      it ("should setup a connection pool") {
        Config.setup('foo)
        val res = NamedDB('foo) readOnly { implicit session =>
          SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
        }
        res should be (Some(1))
        Config.close('foo)
      }
    }

    describe ("#setupAll") {
      it ("should read application.conf and setup all connection pool") {
        Config.setupAll()
        val res = NamedDB('foo) readOnly { implicit session =>
          SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
        }
        res should be (Some(1))
        val res2 = NamedDB('bar) readOnly { implicit session =>
          SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
        }
        res2 should be (Some(1))
        Config.closeAll()
      }
    }

    describe ("#close") {
      describe ("When no argument is passed") {
        it ("should close default connection pool") {
          Config.setup()
          Config.setup('foo)
          Config.close()
          intercept[IllegalStateException] {
            DB readOnly { implicit session =>
              SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
            }
          }
          val res = NamedDB('foo) readOnly { implicit session =>
            SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
          }
          res should be (Some(1))
          Config.close('foo)
        }
      }

      it ("should close a connection pool") {
        Config.setup('foo)
        Config.close('foo)
        intercept[IllegalStateException] {
          NamedDB('foo) readOnly { implicit session =>
            SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
          }
        }
      }
    }

    describe ("#closeAll") {
      it ("should close all connection pools") {
        Config.setup('foo)
        Config.setup('bar)
        Config.closeAll()
        intercept[IllegalStateException] {
          NamedDB('foo) readOnly { implicit session =>
            SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
          }
        }
        intercept[IllegalStateException] {
          NamedDB('bar) readOnly { implicit session =>
            SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
          }
        }
      }
    }
  }

}
