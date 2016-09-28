import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.websudos.phantom.dsl.KeySpaceDef
import model.db.ProductionDb
import model.services.{AuthService, UsersService}
import routes.RouteHandler
import utils.{Config, DbConnector}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

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

  private val connector: KeySpaceDef = ProductionDb.connector
  implicit val keySpace = connector.provider.space
  implicit val session = connector.session
  Await.result(ProductionDb.autocreate.future(), Duration.apply(10, "seconds"))

  Http().bindAndHandle(routeHandler.routes, httpHost, httpPort)
}