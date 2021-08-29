package controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.EventForm
import models.User
import models.services._
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth._

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Контроллер для работы с событиями
 *
 * @param silhouette           Стек Silhouette
 * @param controllerComponents Экземпляр трейта `ControllerComponents`
 * @param ex                   Контекст выполнения
 */
class EventController @Inject()(silhouette: Silhouette[JWTEnvironment],
                                controllerComponents: ControllerComponents,
                                eventService: EventService,
                                hasSignUpMethod: HasSignUpMethod)
                               (implicit ex: ExecutionContext) extends AbstractController(controllerComponents) {

  /**
   * Обрабатывает добавление нового события
   *
   * @return
   */
  def addEvent(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: Request[AnyContent] =>

    val members: List[UUID] = request.body.asJson.get("members").as[List[String]].map(UUID.fromString)

    EventForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(Json.toJson(formWithErrors.errors.toString))),
      data => {
        eventService.saveEvent(data, members).flatMap {
          case EventCreated(_) => Future.successful(Ok(Json.obj("result" -> "Событие успешно добавлено!")))
          case InvalidEndDate => Future.successful(BadRequest(Json.obj("error" -> "Событие не может заканчиваться в прошлом!")))
          case DateTimeEqualException => Future.successful(BadRequest(Json.obj("error" -> "Событие не может начинаться и заканчиваться в одно и то же время!")))
          case EventAlreadyExists => Future.successful(Conflict(Json.obj("error" -> "На указанное время запланировано другое событие!")))
          case _ => Future.successful(BadRequest(Json.obj("error" -> "Произошла ошибка при добавлении события!")))
        }
      }
    )
  }

  /**
   * Обрабатывает обновление существующего события
   *
   * @return
   */
  def updateEvent(eventID: Long): Action[AnyContent] = silhouette.SecuredAction(hasSignUpMethod[JWTEnvironment#A](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[JWTEnvironment, AnyContent] =>

      val members: List[UUID] = request.body.asJson.get("members").as[List[String]].map(UUID.fromString)
      val currentUser: User = request.identity

      EventForm.form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(Json.toJson(formWithErrors.errors.toString))),
        data => {
          eventService.updateEvent(eventID, data, members, currentUser).flatMap {
            case EventUpdated(_) => Future.successful(Ok(Json.obj("success" -> "Событие успешно обновлено!")))
            case EventNotFound => Future.successful(NotFound(Json.obj("error" -> "Событие не найдено!")))
            case InvalidEndDate => Future.successful(BadRequest(Json.obj("error" -> "Событие не может заканчиваться в прошлом!")))
            case DateTimeEqualException => Future.successful(BadRequest(Json.obj("error" -> "Событие не может начинаться и заканчиваться в одно и то же время!")))
            case EventAlreadyExists => Future.successful(Conflict(Json.obj("error" -> "На указанное время запланировано другое событие!")))
            case EventCreatedByAnotherUser(action) =>
              if (action == "update") Future.successful(BadRequest(Json.obj("error" -> "Событие создано другим пользователем и недоступно для редактирования!")))
              else Future.successful(Ok)
            case _ => Future.successful(BadRequest(Json.obj("error" -> "Произошла ошибка при обновлении события!")))
          }
        }
      )
  }

  /**
   * Обрабатывает удаление события
   *
   * @param eventID ID события
   * @return
   */
  def deleteEvent(eventID: Long): Action[AnyContent] = silhouette.SecuredAction(hasSignUpMethod[JWTEnvironment#A](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[JWTEnvironment, AnyContent] =>

      val currentUser: User = request.identity

      eventService.deleteEvent(eventID, currentUser).flatMap {
        case EventDeleted => Future.successful(Ok(Json.obj("success" -> "Событие успешно удалено!")))
        case EventCreatedByAnotherUser(action) =>
          if (action == "delete") Future.successful(BadRequest(Json.obj("error" -> "Событие создано другим пользователем и недоступно для удаления!")))
          else Future.successful(Ok)
        case EventNotFound => Future.successful(NotFound(Json.obj("error" -> "Событие не найдено!")))
        case _ => Future.successful(BadRequest(Json.obj("error" -> "Произошла ошибка при удалении события!")))
      }
  }
}
