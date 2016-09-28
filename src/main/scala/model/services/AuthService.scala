package model.services

import model.entities.{TokenEntity, UserEntity}
import utils.RedisDb._

import spray.json.DefaultJsonProtocol._
import com.redis.serialization.SprayJsonSupport._

import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by joaquinbucca on 9/15/16.
  */
class AuthService(usersService: UsersService)(implicit executionContext: ExecutionContext) {

  def signIn(login: String, password: String): Future[Option[TokenEntity]] ={
    usersService.getUserByUsername(login).flatMap {
      case Some(u) => if (u.password.equals(password)) {
        val token = createToken(u)
        saveToRedis(token).map(t => if (t) Option.apply(token) else throw new Error("couldnt save to redis"))
      } else throw new Error("incorrect password")
      case None => throw new Error("user doesnt exist")
    }
  }

  def signUp(newUser: UserEntity): Future[TokenEntity] = {
    usersService.createUser(newUser).flatMap(user => {
      val token = createToken(user)
      saveToRedis(token).map( t => if (t) token else throw new Error("couldnt save to redis"))
    })
  }

  def saveToRedis(token: TokenEntity): Future[Boolean] = {
    implicit val tokenEntityFormat = jsonFormat2(TokenEntity)
    redis.set(token.token, token)
  }

  def authenticate(token: String): Future[Option[UserEntity]] = {
    implicit val tokenEntityFormat = jsonFormat2(TokenEntity)
    redis.get[TokenEntity](token).flatMap {
      case Some(t) => usersService.getUserByUsername(t.username)
      case None => return Future(None)
    }
  }

  def createToken(user: UserEntity): TokenEntity = new TokenEntity(username = user.username)

}
