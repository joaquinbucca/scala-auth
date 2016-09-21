package model.services

import model.entities.UserEntity

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by joaquinbucca on 9/15/16.
  */
class UsersService(implicit executionContext: ExecutionContext) {

  import model.db.ProductionDatabase.database._

  def getUsers: Future[Seq[UserEntity]] = usersModel.getAll

  def getUserById(id: Long): Future[Option[UserEntity]] = usersModel.getById(id)

  //todo: implement search by username then
  def getUserByLogin(login: String): Future[Option[UserEntity]] = usersModel.getById(12l)

  def createUser(user: UserEntity): Future[UserEntity] = usersModel.store(user).map( r => usersModel.fromResultSet(r))

  def updateUser(id: Long, userUpdate: UserEntity): Future[UserEntity] = usersModel.store(userUpdate).map( r => usersModel.fromResultSet(r))

  def deleteUser(id: Long): Future[Int] = usersModel.deleteById(id).map(rs => id.toInt)

}

