package model.services

import model.entities.UserEntity

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by joaquinbucca on 9/15/16.
  */
class UsersService(implicit executionContext: ExecutionContext) {

  import model.db.ProductionDatabase.database._

  def getUsers: Future[Seq[UserEntity]] = usersModel.getAll

  def getUserByUsername(login: String): Future[Option[UserEntity]] = usersModel.getByUsername(login)

  def createUser(user: UserEntity): Future[UserEntity] = {
    usersModel.store(user).flatMap(u =>  usersModel.getByUsername(user.username).map( f => f.get ))
  }

  def updateUser(userUpdate: UserEntity): Future[UserEntity] =
    usersModel.store(userUpdate).flatMap( r => usersModel.getByUsername(userUpdate.username).map( f => f.get ))

  def deleteUser(username: String): Future[String] = usersModel.deleteByUsername(username).map(rs => username)

}

