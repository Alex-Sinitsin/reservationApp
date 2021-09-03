package modules

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}

import com.mohiva.play.silhouette.api.crypto._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.util._
import com.mohiva.play.silhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository

import com.typesafe.config.Config

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader
import net.ceedubs.ficus.readers.EnumerationReader._ //Без нее не работает

import net.codingwell.scalaguice.ScalaModule

import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import play.api.mvc.Cookie

import models.daos._
import models.services.{UserService, UserServiceImpl}
import utils.auth.JWTEnvironment

import scala.concurrent.ExecutionContext

/**
 * Модуль Guice, который объединяет все зависимости Silhouette.
 */
class SilhouetteModule extends AbstractModule with ScalaModule {
  implicit val ec: ExecutionContext = ExecutionContext.global
  /**
   * Очень вложенный дополнительный считыватель для поддержки этих случаев:
   * Не задано, установите None, будет использоваться значение по умолчанию ('Lax')
   * Установите значение null, установите Some (None), будет использовать "Без ограничений"
   * Установите строковое значение, попробуйте сопоставить, Some (Option (string))
   */
  implicit val JWTValueReader: ValueReader[Option[Option[Cookie.SameSite]]] =
    (config: Config, path: String) => {
      if (config.hasPathOrNull(path)) {
        if (config.getIsNull(path))
          Some(None)
        else {
          Some(Cookie.SameSite.parse(config.getString(path)))
        }
      } else {
        None
      }
    }
  /**
   * Настройка модуля.
   */
  override def configure(): Unit = {
    bind[Silhouette[JWTEnvironment]].to[SilhouetteProvider[JWTEnvironment]]
    bind[UserService].to[UserServiceImpl]
    bind[UserDAO].to[UserDAOImpl]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  @Provides
  def providePasswordInfo(dbConfig: DatabaseConfigProvider): DelegableAuthInfoDAO[PasswordInfo] = {
    new PasswordInfoDAO(dbConfig)
  }

  /**
   * Обеспечивает реализацию уровня HTTP
   *
   * @param client Play's WS клиент.
   * @return Реализация уровня HTTP
   */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /**
   * Обеспечивает среду Silhouette
   *
   * @param userService          Реализация пользовательского сервиса
   * @param authenticatorService Реализация службы аутентификации
   * @param eventBus             Экземпляр шины событий
   * @return Среду Silhouette.
   */
  @Provides
  def provideEnvironment(userService: UserService,
                         authenticatorService: AuthenticatorService[JWTAuthenticator],
                         eventBus: EventBus): Environment[JWTEnvironment] = {

    Environment[JWTEnvironment](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  /**
   * Предоставляет репозиторий информации об авторизации
   *
   * @param passwordInfoDAO Реализация делегируемого пароля auth info DAO
   * @return Экземпляр репозитория информации аутентификации
   */
  @Provides
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDAO)

  /**
   * Предоставляет подписывающее лицо для аутентификатора
   *
   * @param configuration Play конфигурация
   * @return Подписывающее лицо для аутентификатора
   */
  @Provides
  @Named("authenticator-signer")
  def provideAuthenticatorSigner(configuration: Configuration): Signer = {
    val config = configuration.underlying.as[JcaSignerSettings]("silhouette.authenticator.signer")

    new JcaSigner(config)
  }

  /**
   * Предоставляет шифровальщик для аутентификатора
   *
   * @param configuration Play конфигурация
   * @return Шифровальщик для аутентификатора
   */
  @Provides
  @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")

    new JcaCrypter(config)
  }

  /**
   * Предоставляет услугу аутентификатора
   *
   * @param crypter Реализация шифровальщика
   * @param idGenerator Реализация генератора идентификаторов
   * @param configuration Play конфигурация
   * @param clock Экземпляр часов
   * @return Сервис аутентификации
   */
  @Provides
  def provideAuthenticatorService(@Named("authenticator-crypter") crypter: Crypter,
                                  idGenerator: IDGenerator,
                                  configuration: Configuration,
                                  clock: Clock): AuthenticatorService[JWTAuthenticator] = {

    val config = configuration.underlying.as[JWTAuthenticatorSettings]("silhouette.authenticator")
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new JWTAuthenticatorService(config, None, encoder, idGenerator, clock)
  }

  /**
   * Предоставляет реестр хэшей паролей
   *
   * @return Реестр хэшей паролей
   */
  @Provides
  def providePasswordHasherRegistry(): PasswordHasherRegistry = {
    PasswordHasherRegistry(new BCryptSha256PasswordHasher(), Seq(new BCryptPasswordHasher()))
  }

  /**
   * Предоставляет поставщика учетных данных
   *
   * @param authInfoRepository     Реализация репозитория информации авторизации
   * @param passwordHasherRegistry Реестр хэшей паролей
   * @return Поставщика учетных данных
   */
  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider =
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
}
