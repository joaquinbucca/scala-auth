import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import model.services.{AuthService, UsersService}
import routes.RouteHandler
import utils.Config

/**
  * Created by joaquinbucca on 9/15/16.
  */

object Main extends App with Config {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val logger = Logging(system, getClass)


  private val usersService: UsersService = new UsersService
  val routeHandler = new RouteHandler(usersService, new AuthService(usersService))

  Http().bindAndHandle(routeHandler.routes, httpHost, httpPort)
}