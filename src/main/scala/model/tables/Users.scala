package model.tables

import com.datastax.driver.core.Row
import model.entities.UserEntity

import scala.concurrent.Future
import com.websudos.phantom.dsl._

/**
  * Created by joaquinbucca on 9/16/16.
  */
class Users extends CassandraTable[ConcreteUsers, UserEntity] {

  object id extends LongColumn(this) with PartitionKey[Long]
  object username extends StringColumn(this)
  object password extends StringColumn(this)
//  object registrationDate extends DateTimeColumn(this)

  override def fromRow(r: Row): UserEntity = UserEntity(Option(id(r)), username(r), password(r))
  def fromResultSet(r: ResultSet): UserEntity = fromRow(r.one())
}

abstract class ConcreteUsers extends Users with RootConnector {

  def store(user: UserEntity): Future[ResultSet] = {
    insert
      .value(_.id, user.id.getOrElse(0l))
      .value(_.username, user.username)
      .value(_.password, user.password)
      .consistencyLevel_=(ConsistencyLevel.ALL)
      .future()
  }

  def getAll: Future[Seq[UserEntity]] = {
    select.limit(100).fetch()
  }

  def getById(id: Long): Future[Option[UserEntity]] = {
    select.where(_.id eqs id).one()
  }

  def deleteById(id: Long): Future[ResultSet] = {
    delete
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}