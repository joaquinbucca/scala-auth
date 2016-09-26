package routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import model.entities.UserEntity
import model.services.{AuthService, UsersService}

import scala.concurrent.ExecutionContext

/**
  * Created by joaquinbucca on 9/15/16.
  */
class UsersRouter(val authService: AuthService, usersService: UsersService)
                 (implicit executionContext: ExecutionContext)  extends CirceSupport with SecurityDirectives {

  import usersService._

  val route = pathPrefix("users") {
    pathEndOrSingleSlash {
      get {
        complete(getUsers.map(_.asJson))
      }
    } ~
      pathPrefix("me") {
        pathEndOrSingleSlash {
          authenticate { loggedUser =>
            get {
              complete(loggedUser)
            } ~
              post {
                entity(as[UserEntity]) { userUpdate =>
                  complete(updateUser(userUpdate).map(_.asJson))
                }
              }
          }
        }
      } ~
      pathPrefix(Rest) { username =>
        pathEndOrSingleSlash {
          get {
            complete(getUserByUsername(username).map(_.asJson))
          } ~
            post {
              entity(as[UserEntity]) { userUpdate =>
                complete(updateUser(userUpdate).map(_.asJson))
              }
            } ~
            delete {
              onSuccess(deleteUser(username)) { ignored =>
                complete(NoContent)
              }
            }
        }
      }
  }
}
