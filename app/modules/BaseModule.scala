package modules

import com.google.inject.AbstractModule
import models.daos.{LoginInfoDAO, LoginInfoDAOImpl, UserDAO, UserDAOImpl}
import models.services.{AuthTokenService, AuthTokenServiceImpl, UserService, UserServiceImpl}
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * The base Guice module.
 */
class BaseModule extends AbstractModule with ScalaModule with AkkaGuiceSupport {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[AuthTokenService].to[AuthTokenServiceImpl]
    bind[LoginInfoDAO].to[LoginInfoDAOImpl]
  }
  /**
   * Configures the module.
   */
//  override def configure(): Unit = {
//    bind[UserDAO].to[UserDAOImpl]
//    bind[UserService].to[UserServiceImpl]
//  }
}
