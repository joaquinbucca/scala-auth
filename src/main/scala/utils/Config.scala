package utils

import com.typesafe.config.ConfigFactory

/**
  * Created by joaquinbucca on 9/15/16.
  */
trait Config {

  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")

  val httpHost = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")

}
