package com.github.tototoshi.scalikejdbc.config

import com.typesafe.config.{ Config => TypesafeConfig, ConfigFactory }
import scala.collection.mutable.{ Map => MutableMap, ListBuffer }
import scala.util.control.NonFatal

class ConfigurationException(val message: String) extends Exception(message) {
  def this(e: Throwable) = this(e.getMessage)
}

object TypesafeConfigReader {

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

  def readAsMap(dbName: Symbol): Map[String, String] = {
    try {
      val dbConfig = _config.getConfig("db." + dbName.name)
      val it = dbConfig.entrySet.iterator
      val configMap: MutableMap[String, String] = MutableMap.empty
      while (it.hasNext) {
        val entry = it.next
        val key = entry.getKey
        configMap(key) = _config.getString("db." + dbName.name + "." + key)
      }
      configMap.toMap
    } catch {
      case NonFatal(e) => throw new ConfigurationException(e)
    }
  }

  def read(dbName: Symbol): JDBCSetting = {
    val configMap = TypesafeConfigReader.readAsMap(dbName)

    (for {
      driver <- configMap.get("driver")
      url <- configMap.get("url")
      user <- configMap.get("user")
      password <- configMap.get("password")
    } yield {
      JDBCSetting(driver, url, user, password)
    }) getOrElse {
      throw new ConfigurationException("Configuration error for database " + dbName + ". " + configMap.toString)
    }
  }

}
