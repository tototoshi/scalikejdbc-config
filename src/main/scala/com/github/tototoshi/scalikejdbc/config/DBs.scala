package com.github.tototoshi.scalikejdbc.config

import scalikejdbc._

object DBs {

  def setup(): Unit = {
    setup('default)
  }

  def setup(dbName: Symbol): Unit = {
    val JDBCSetting(driver, url, user, password) = TypesafeConfigReader.read(dbName)
    Class.forName(driver)
    ConnectionPool.add(dbName, url, user, password)
  }

  def setupAll(): Unit = {
    TypesafeConfigReader.dbNames.foreach { dbName => setup(Symbol(dbName)) }
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

