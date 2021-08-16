package modules

import com.google.inject.AbstractModule

import net.codingwell.scalaguice.ScalaModule

import play.api.libs.concurrent.AkkaGuiceSupport

import models.daos.{LoginInfoDAO, LoginInfoDAOImpl}
import models.services.{AuthTokenService, AuthTokenServiceImpl}

/**
 * Базовый модуль Guice
 */
class BaseModule extends AbstractModule with ScalaModule with AkkaGuiceSupport {

  /**
   * Настройки модуля.
   */
  override def configure(): Unit = {
    bind[AuthTokenService].to[AuthTokenServiceImpl]
    bind[LoginInfoDAO].to[LoginInfoDAOImpl]
  }
}
