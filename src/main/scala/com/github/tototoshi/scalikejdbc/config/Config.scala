package com.github.tototoshi.scalikejdbc.config

import com.typesafe.config.{ Config => TypesafeConfig, ConfigFactory }
import scala.collection.mutable.{ Map => MutableMap, ListBuffer }
import scalikejdbc._

class ConfigurationException(val message: String) extends Exception(message)

object Config {

  var _config: TypesafeConfig = ConfigFactory.load()

  def dbNames: List[String] = {
    val it = _config.entrySet.iterator
    val buf: ListBuffer[String] = new ListBuffer
    while (it.hasNext) {
      val entry = it.next
      val key = entry.getKey
      key.split("\\.").toList match {
        case List("db", dbName, _) => {
          buf.append(dbName)
        }
        case _ => ()
      }
    }
    buf.toList.distinct
  }

  def apply(dbName: Symbol): Map[String, String] = {
    val it = _config.getConfig("db." + dbName.name).entrySet.iterator
    val configMap: MutableMap[String, String] = MutableMap.empty
    while (it.hasNext) {
      val entry = it.next
      val key = entry.getKey
      configMap(key) = _config.getString("db." + dbName.name + "." + key)
    }
    configMap.toMap
  }

  def setup(): Unit = {
    setup('default)
  }

  def setup(dbName: Symbol): Unit = {
    val configMap = apply(dbName)

    (for {
      driver <- configMap.get("driver")
      url <- configMap.get("url")
      user <- configMap.get("user")
      password <- configMap.get("password")
    } yield {
      (driver, url, user, password)
    }) match {
      case Some((driver, url, user, password)) => {
        Class.forName(driver)
        ConnectionPool.add(dbName, url, user, password)
      }
      case None => {
        throw new ConfigurationException("Configuration error for database " + dbName + ". " + configMap.toString)
      }
    }
  }

  def setupAll(): Unit = {
    dbNames.foreach { dbName => setup(Symbol(dbName)) }
  }

  def close(): Unit = {
    close('default)
  }

  def close(dbName: Symbol): Unit = {
    ConnectionPool.close(dbName)
  }

  def closeAll(): Unit = {
    ConnectionPool.closeAll
  }

}
