import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import routes.RouteHandler
import services.{AuthService, UsersService}
import utils.{Config, DatabaseService}

/**
  * Created by joaquinbucca on 9/15/16.
  */

object Main extends App with Config {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val logger = Logging(system, getClass)

  val databaseService = new DatabaseService()

  val usersService = new UsersService(databaseService)
  val authService = new AuthService(usersService, databaseService)

  val routeHandler = new RouteHandler(authService, usersService)

  Http().bindAndHandle(routeHandler.routes, httpHost, httpPort)
}