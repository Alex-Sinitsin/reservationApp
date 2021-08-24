package controllers

import com.mohiva.play.silhouette.api.Silhouette
import forms.EventForm
import models.services.{EventAlreadyExists, EventCreated, EventService}
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import utils.auth.JWTEnvironment

import java.util.UUID

/**
 * Контроллер для работы с событиями
 *
 * @param silhouette Стек Silhouette
 * @param controllerComponents Экземпляр трейта `ControllerComponents`
 * @param ex Контекст выполнения
 */
class EventController @Inject() (silhouette: Silhouette[JWTEnvironment], controllerComponents: ControllerComponents, eventService: EventService)
                                (implicit ex: ExecutionContext) extends AbstractController(controllerComponents) {

  def addEvent: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val members: List[UUID] = request.body.asJson.get("members").as[List[String]].map(UUID.fromString)

    EventForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(Json.toJson(formWithErrors.errors.toString()))),
      data => {
        eventService.saveEvent(data, members).flatMap {
            case EventCreated(event) => Future.successful(Ok(Json.obj("result" -> "Событие успешно добавлено")))
            case EventAlreadyExists => Future.successful(BadRequest(Json.obj("error" -> "Событие с таким названием уже существует")))
            case _ => Future.successful(BadRequest(Json.obj("error" -> "Произошла ошибка при добавлении события")))
        }
      }
    )
  }
}
