package model.services

import model.entities.{TokenEntity, UserEntity}
import utils.RedisDb._
import spray.json.DefaultJsonProtocol._
import com.redis.serialization.SprayJsonSupport._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by joaquinbucca on 9/15/16.
  */
class AuthService(usersService: UsersService)(implicit executionContext: ExecutionContext) {

  def signIn(login: String, password: String): Future[Option[TokenEntity]] =
    Future(Option(new TokenEntity(username = "asdasd")))

  def signUp(newUser: UserEntity): Future[TokenEntity] = {
    usersService.createUser(newUser).flatMap(user => {
      val token = createToken(user)
      implicit val tokenEntityFormat = jsonFormat2(TokenEntity)
      token.map(t => redis.set(t.token, t))
      token
    })
  }

  def authenticate(token: String): Future[Option[UserEntity]] = redis.get[TokenEntity](token).flatMap {
    case Some(t) => usersService.getUserById(t.userId)
    case None => return Future(None)
  }

  def createToken(user: UserEntity): Future[TokenEntity] = Future(new TokenEntity(username = user.username))

}
