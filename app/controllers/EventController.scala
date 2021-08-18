package controllers

import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.{JsString, Json}
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import utils.auth.JWTEnvironment

/**
 * Контроллер для работы с событиями
 *
 * @param silhouette Стек Silhouette
 * @param controllerComponents Экземпляр трейта `ControllerComponents`
 * @param ex Контекст выполнения
 */
class EventController @Inject() (silhouette: Silhouette[JWTEnvironment], controllerComponents: ControllerComponents)
                                (implicit ex: ExecutionContext) extends AbstractController(controllerComponents) {

  def addEvent(): Action[AnyContent] = silhouette.SecuredAction { implicit request: Request[AnyContent] =>
    Ok(JsString("Event added"))
  }
}
