package services

import entities.{UserEntity, UserEntityUpdate}
import utils.DatabaseService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by joaquinbucca on 9/15/16.
  */
class UsersService(databaseService: DatabaseService)(implicit executionContext: ExecutionContext) {

  val user = new UserEntity(username = "joaquin", password = "pass")

  def getUsers(): Future[Seq[UserEntity]] = Future(Seq(user))
  //db.run(users.result)

  def getUserById(id: Long): Future[Option[UserEntity]] = Future(Option(user))
  //db.run(users.filter(_.id === id).result.headOption)

  def getUserByLogin(login: String): Future[Option[UserEntity]] = getUserById(12l)
  //db.run(users.filter(_.username === login).result.headOption)

  def createUser(user: UserEntity): Future[UserEntity] = Future(user)
  //db.run(users returning users += user)

  def updateUser(id: Long, userUpdate: UserEntityUpdate): Future[Option[UserEntity]] = getUserById(12l)
//    getUserById(id).flatMap {
//    case Some(user) =>
//      val updatedUser = userUpdate.merge(user)
//      db.run(users.filter(_.id === id).update(updatedUser)).map(_ => Some(updatedUser))
//    case None => Future.successful(None)
//  }

  def deleteUser(id: Long): Future[Int] = Future(1)
    //db.run(users.filter(_.id === id).delete)


}
