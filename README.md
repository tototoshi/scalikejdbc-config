# scalikejdbc-config

Easy setup for scalikejdbc


## Example

src/main/resources/application.conf
```
db.foo.url="jdbc:h2:memory"
db.foo.driver="org.h2.Driver"
db.foo.user="sa"
db.foo.password="secret"

db.bar.url="jdbc:h2:memory2"
db.bar.driver="org.h2.Driver"
db.bar.user="sa2"
db.bar.password="secret2"
```


```scala
scala> import com.github.tototoshi.scalikejdbc.config._

scala> import scalikejdbc._

scala> Config.setupAll

scala> NamedDB('foo) readOnly { implicit session =>
     |   SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
     | }
res0: Option[Int] = Some(1)

scala> NamedDB('bar) readOnly { implicit session =>
     |   SQL("SELECT 1 as one").map(rs => rs.int("one")).single.apply()
     | }
res1: Option[Int] = Some(1)

scala> Config.closeAll
```

