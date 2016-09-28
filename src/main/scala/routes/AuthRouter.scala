package routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import model.entities.UserEntity
import model.services.AuthService

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Created by joaquinbucca on 9/15/16.
  */
class AuthRouter(val authService: AuthService)(implicit executionContext: ExecutionContext) extends CirceSupport with SecurityDirectives{

  import authService._

  val route = pathPrefix("auth") {
    path("signIn") {
      pathEndOrSingleSlash {
        post {
          entity(as[LoginPassword]) { loginPassword =>
            Try(signIn(loginPassword.login, loginPassword.password)) match {
              case scala.util.Success(tk) => complete(tk.map(_.asJson))
              case scala.util.Failure(err) => failWith(err)
            }
          }
        }
      }
    } ~
      path("signUp") {
        pathEndOrSingleSlash {
          post {
            entity(as[UserEntity]) { userEntity =>
              complete(Created -> signUp(userEntity).map(_.asJson))
            }
          }
        }
      }
  }

  private case class LoginPassword(login: String, password: String)

}
