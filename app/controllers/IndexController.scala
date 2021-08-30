package controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import play.api.mvc._
import utils.auth.JWTEnvironment

import javax.inject.Inject
import scala.concurrent.ExecutionContext

/**
 *  Контроллер главной страницы
 *
 * @param silhouette Стек `Silhouette`
 * @param controllerComponents Экземпляр трейта `ControllerComponents`
 * @param ex Контекст выполнения
 */
class IndexController @Inject() (silhouette: Silhouette[JWTEnvironment],
                                 controllerComponents: ControllerComponents,
                                 passwordHasherRegistry: PasswordHasherRegistry)
                                (implicit ex: ExecutionContext) extends AbstractController(controllerComponents) {

  def index: Action[AnyContent] = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok
  }
}
