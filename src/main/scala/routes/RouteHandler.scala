package routes

import akka.http.scaladsl.server.Directives._
import model.services.{AuthService, UsersService}

import scala.concurrent.ExecutionContext

/**
  * Created by joaquinbucca on 9/15/16.
  */
class RouteHandler(usersService: UsersService, authService: AuthService)(implicit ex : ExecutionContext) {

  val authRouter = new AuthRouter(authService)
  val usersRouter = new UsersRouter(authService, usersService)

  val routes = {
    logRequestResult("akka-http-microservice") {
      authRouter.route ~
      usersRouter.route

    }
  }

}
