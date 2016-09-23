package utils

import com.redis.RedisClient
import com.typesafe.config.ConfigFactory

object RedisDb {

  val config = ConfigFactory.load()
  val host = config.getString("redis.hostname")
  val port = config.getInt("redis.port")

  val redis = RedisClient(host, port)

}
