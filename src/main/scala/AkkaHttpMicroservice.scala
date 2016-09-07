import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.DefaultJsonProtocol


case class LoginRequest(username: String, password: String)

case class AuthResponse(auth: Boolean, username: String, token: String)

case class AuthRequest(username: String, token: String)


object AuthChecker {
//  def apply(username: String, password: String): AuthChecker = AuthChecker(calculateDistance(ip1Info, ip2Info), ip1Info, ip2Info)

//  private def calculateDistance(ip1Info: IpInfo, ip2Info: IpInfo): Option[Double] = {  }

}

trait Protocols extends DefaultJsonProtocol {
  implicit val authRequestFormat = jsonFormat2(AuthRequest.apply)
  implicit val authResponseFormat = jsonFormat3(AuthResponse.apply)
  implicit val loginRequestFormat = jsonFormat2(LoginRequest.apply)
}

trait Service extends Protocols {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

  lazy val authApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.auth-api.host"), config.getInt("services.auth-api.port"))

  def authApiRequest(request: HttpRequest): Future[HttpResponse] = Source.single(request).via(authApiConnectionFlow).runWith(Sink.head)

  def fetchUserInfo(username: String): Future[Either[String, AuthResponse]] = {
    authApiRequest(RequestBuilding.Get(s"/json/$username")).flatMap { response =>
      response.status match {
        case OK => Unmarshal(response.entity).to[AuthResponse].map(Right(_))
        case BadRequest => Future.successful(Left(s"$username: incorrect IP format"))
        case _ => Unmarshal(response.entity).to[String].flatMap { entity =>
          val error = s"FreeGeoIP request failed with status code ${response.status} and entity $entity"
          logger.error(error)
          Future.failed(new IOException(error))
        }
      }
    }
  }

  val routes = {
    logRequestResult("akka-http-microservice") {
      pathPrefix("auth") {
        (get & path(Segment)) { auth =>
          complete {
            fetchUserInfo(auth).map[ToResponseMarshallable] {
              case Right(authInfo) => authInfo
              case Left(errorMessage) => BadRequest -> errorMessage
            }
          }
        } ~
        (post & entity(as[AuthRequest])) { authPairSummaryRequest =>
          complete {
            AuthResponse(true, authPairSummaryRequest.username, "super token")
          }
        }
      }
    }
  }
}

object AkkaHttpMicroservice extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
