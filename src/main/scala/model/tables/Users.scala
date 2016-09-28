package model.tables

import com.datastax.driver.core.Row
import model.entities.UserEntity

import scala.concurrent.Future
import com.websudos.phantom.dsl._
import scala.collection.JavaConversions._

/**
  * Created by joaquinbucca on 9/16/16.
  */
class Users extends CassandraTable[ConcreteUsers, UserEntity] {

//  object id extends LongColumn(this) with PartitionKey[Long]
  object username extends StringColumn(this) with PartitionKey[String]
  object password extends StringColumn(this)
//  object registrationDate extends DateTimeColumn(this)

  override def fromRow(r: Row): UserEntity = UserEntity(username(r), password(r))
  def fromResultSet(r: ResultSet): UserEntity = {
    println("###########################################################")

    for (row <- r.all()) println(row)

    println("###########################################################")

    fromRow(r.one())
  }
}

abstract class ConcreteUsers extends Users with RootConnector {

  def store(user: UserEntity): Future[ResultSet] = {
    insert
      .value(_.username, user.username)
      .value(_.password, user.password)
      .consistencyLevel_=(ConsistencyLevel.ALL)
      .future()
  }

  def getAll: Future[Seq[UserEntity]] = {
    select.limit(100).fetch()
  }

  def getByUsername(username: String): Future[Option[UserEntity]] = {
    select.where(_.username eqs username).one()
  }

  def deleteByUsername(username: String): Future[ResultSet] = {
    delete
      .where(_.username eqs username)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}