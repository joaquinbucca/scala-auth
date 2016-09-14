import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.DefaultJsonProtocol


case class LoginRequest(username: String, password: String)

case class AuthResponse(auth: Boolean, username: String, token: String)

case class AuthRequest(username: String, token: String)

case class RegisterRequest(username: String, password: String, repeated: String, name: String, lastname: String)



trait Protocols extends DefaultJsonProtocol {
  implicit val authRequestFormat = jsonFormat2(AuthRequest.apply)
  implicit val authResponseFormat = jsonFormat3(AuthResponse.apply)
  implicit val loginRequestFormat = jsonFormat2(LoginRequest.apply)
  implicit val registerRequestFormat = jsonFormat5(RegisterRequest.apply)
}

trait Service extends Protocols {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

  def register(registerRequest: RegisterRequest) : Future[AuthResponse] = Future(AuthResponse(true, registerRequest.username, "as"))
  def login(loginRequest: LoginRequest) : Future[AuthResponse] = Future(AuthResponse(true, loginRequest.username, "logged"))
  def authenticate(username: String, token: String) : Future[AuthResponse] = Future(AuthResponse(true, username, token))

  val routes = {
    logRequestResult("akka-http-microservice") {
      pathPrefix("auth") {
        (get & path(Segment)) { username =>
          complete {
            authenticate(username, "authhhh")
          }
        } ~
        (post & entity(as[LoginRequest])) { loginRequest =>
          complete {
            login(loginRequest)
          }
        }
      } ~
      pathPrefix("register") {
        (post & entity(as[RegisterRequest])) { registerRequest =>
          complete {
            register(registerRequest)
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
