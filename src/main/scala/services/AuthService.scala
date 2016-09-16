package services

import entities.{TokenEntity, UserEntity}
import utils.DatabaseService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by joaquinbucca on 9/15/16.
  */
class AuthService(usersService: UsersService, databaseService: DatabaseService)(implicit executionContext: ExecutionContext) {

  def signIn(login: String, password: String): Future[Option[TokenEntity]] =
    Future(Option(new TokenEntity(userId = Option(123123l))))

  def signUp(newUser: UserEntity): Future[TokenEntity] = {
    usersService.createUser(newUser).flatMap(user => createToken(user))
  }

  def authenticate(token: String): Future[Option[UserEntity]] = Future(Option(new UserEntity(username = "joaquin", password = "pass")))

  def createToken(user: UserEntity): Future[TokenEntity] = Future(new TokenEntity(userId = user.id))

}
