package controllers

import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import utils.auth.JWTEnvironment

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import java.time.{LocalDate, LocalTime}

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
    val ld = LocalDate.now().toString
    val lt1 = LocalTime.parse("13:00").toString
    val lt2 = LocalTime.parse("13:30").toString
    Ok(Json.obj("ld"->ld, "lt1"->lt1, "lt2"->lt2))
  }
}
