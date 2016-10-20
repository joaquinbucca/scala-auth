package utils

import java.net.{Inet4Address, InetAddress}

import com.typesafe.config.ConfigFactory
import consul.Consul
import consul.v1.common.Types

import scala.concurrent.Future

object ConsulHandler {


  val config = ConfigFactory.load()

  val consulConfig = config.getConfig("consul")
  val consulAddress = consulConfig.getString("address")
  val consulPort = consulConfig.getInt("port")
  val httpPort = config.getInt("http.port")


  val consul = new Consul(new Inet4Address(consulAddress, consulPort))
  import consul.v1._

  def register(sId : String, sType: String) : Future[Boolean] = {
    // register service to consul
    val serviceId: Types.ServiceId = Types.ServiceId(sId)
    val stockType: Types.ServiceType = Types.ServiceType(sType)
    val address = InetAddress.getLocalHost.getHostAddress
    val myServiceCheck = agent.service.httpCheck(s"http://$address:$httpPort/health","15s")
    val myService = agent.service.LocalService(serviceId,ServiceType("micro"), Set(ServiceTag("prod")),Some(httpPort),Some(myServiceCheck))

    agent.service.register(myService)
  }

  def getServicesAddressesByType(sType: String, tags: Option[ServiceTag] = Option.empty) : Future[Seq[String]] = {
    catalog.service(ServiceType(sType)).map( services => services.map(s => s.Address + ":" + s.ServicePort))
  }

  def getServiceHostByType(s: String) : Future[(String, Int)] = catalog.service(ServiceType(s)).map( services => (services.head.Address, services.head.ServicePort))
}
