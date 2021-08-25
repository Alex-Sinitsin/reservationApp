package controllers

import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import utils.auth.JWTEnvironment

import java.time.format.DateTimeFormatter
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import java.time.{Duration, LocalDate, LocalDateTime, LocalTime}

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
    val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val ld = LocalDate.now()
    val lt1 = LocalTime.parse("13:00")
    val lt2 = LocalTime.parse("13:30")

    val dateTimeOne: LocalDateTime = LocalDateTime.parse("2019-04-28 14:32", dtf).minus(Duration.ofMinutes(1))
    val dateTimeTwo: LocalDateTime = LocalDateTime.parse("2019-04-28 14:32", dtf)

    val value = dateTimeOne compareTo dateTimeTwo

    Ok(Json.obj("ldt1"->dateTimeOne, "ldt2"->dateTimeTwo, "val"->value))
  }
}
