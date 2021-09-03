package utils.auth

import com.mohiva.play.silhouette.api.{Authenticator, Authorization}

import javax.inject.Inject

import play.api.mvc.Request

import scala.concurrent.Future

import models.User
import models.services.AuthenticateService

class HasSignUpMethod @Inject()(authenticateService: AuthenticateService) {

  /**
   * Предоставляет доступ только в том случае, если у пользователя есть метод аутентификации с данным провайдером.
   *
   * @param provider ID провайдера, с которым пользователь должен иметь метод аутентификации
   * @tparam A Тип аутентификатора
   */
  case class HasMethod[A <: Authenticator](provider: String) extends Authorization[User, A] {
    /**
     * Указывает, авторизован ли пользователь для доступа к действию.
     *
     * @param user          Объекта пользователя
     * @param authenticator Экземпляр аутентификатора.
     * @param request       Текущий запрос
     * @tparam B Тип тела запроса
     * @return Истина, если пользователь авторизован, иначе ложь
     */
    override def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]): Future[Boolean] = {
      authenticateService.userHasAuthenticationMethod(user.id, provider)
    }
  }

  def apply[A <: Authenticator](provider: String): Authorization[User, A] = HasMethod[A](provider)
}
