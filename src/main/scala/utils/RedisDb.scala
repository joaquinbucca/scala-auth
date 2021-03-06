package utils

import akka.actor.ActorSystem
import akka.util.Timeout
import com.redis.RedisClient
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object RedisDb {

  implicit val system = ActorSystem()
  implicit val timeout : Timeout = 20 seconds
  val config = ConfigFactory.load()
  val host = config.getString("redis.host")
  val port = config.getInt("redis.port")

//  val (host, port)= Await.result(ConsulHandler.getServiceHostByType("REDIS").map(a => a), 10 seconds)

  val redis = RedisClient(host, port)

}
