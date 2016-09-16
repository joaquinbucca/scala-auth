package entities.tables

import com.datastax.driver.core.Row
import com.websudos.phantom.dsl.{CassandraTable, ConsistencyLevel, LongColumn, PartitionKey, ResultSet, RootConnector, StringColumn}
import entities.UserEntity

import scala.concurrent.Future

/**
  * Created by joaquinbucca on 9/16/16.
  */
class Users extends CassandraTable[ConcreteUsers, UserEntity] {

  object id extends LongColumn(this) with PartitionKey[Long]
  object username extends StringColumn(this)
  object password extends StringColumn(this)
//  object registrationDate extends DateTimeColumn(this)

  override def fromRow(r: Row): UserEntity = UserEntity(Option(id(r)), username(r), password(r))
}

abstract class ConcreteUsers extends Users with RootConnector {

  def store(user: UserEntity): Future[ResultSet] = {
    insert.value(_.id, user.id).value(_.username, user.username)
      .value(_.password, user.password)
      .consistencyLevel_=(ConsistencyLevel.ALL)
      .future()
  }

  def getById(id: Long): Future[Option[UserEntity]] = {
    select.where(_.id eqs id).one()
  }
}