package controllers

import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.JsString
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
class IndexController @Inject() (silhouette: Silhouette[JWTEnvironment], controllerComponents: ControllerComponents)
                                (implicit ex: ExecutionContext) extends AbstractController(controllerComponents) {

  def index: Action[AnyContent] = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(JsString("Hello"))
  }
}
