package model.db

/**
  * Created by joaquinbucca on 9/20/16.
  */

import com.websudos.phantom.db.DatabaseImpl
import com.websudos.phantom.dsl._
import model.tables.ConcreteUsers
import utils.DbConnector._

class UsersDatabase(override val connector: KeySpaceDef) extends DatabaseImpl(connector) {
  object usersModel extends ConcreteUsers with connector.Connector
}

/**
  * This is the production database, it connects to a secured cluster with multiple contact points
  */
object ProductionDb extends UsersDatabase(connector)

trait ProductionDatabaseProvider {
  def database: UsersDatabase
}

trait ProductionDatabase extends ProductionDatabaseProvider {
  override val database = ProductionDb
}

object ProductionDatabase extends ProductionDatabase with ProductionDatabaseProvider

/**
  * Thanks for the Phantom plugin, you can start an embedded cassandra in memory,
  * in this case we are using it for tests
  */
object EmbeddedDb extends UsersDatabase(testConnector)

trait EmbeddedDatabaseProvider {
  def database: UsersDatabase
}

trait EmbeddedDatabase extends EmbeddedDatabaseProvider {
  override val database = EmbeddedDb
}