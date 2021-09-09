package controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.EventForm
import models.{Event, User}
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

  case class EventWithOrgUserInfo(event: Event, orgUserInfo: User)

  /**
   * Выводит список всех событий
   *
   * @return
   */
  def listAll(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: Request[AnyContent] =>
    eventService.retrieveAll.flatMap { events =>
      Future.successful(Ok(Json.toJson(events)).withHeaders("X-Total-Count" -> events.size.toString))
    }
  }

  /**
   * Получение события по его ID
   *
   * @param eventID ID события
   * @return
   */
  def getEventByID(eventID: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: Request[AnyContent] =>
    eventService.getEventByID(eventID).flatMap {
      case Some(event) => Future.successful(Ok(Json.toJson(event)))
      case None => Future.successful(NotFound(Json.toJson(Json.obj("status" -> "error", "code" -> NOT_FOUND, "message" -> "Событие не найдено!"))))
    }
  }

  /**
   * Обрабатывает добавление нового события
   *
   * @return
   */
  def createEvent(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: Request[AnyContent] =>

    EventForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(Json.toJson(formWithErrors.errors.toString))),
      data => {
        val members: List[UUID] = data.members.get.map(UUID.fromString)

        eventService.saveEvent(data, members).flatMap {
          case EventCreated(_) => Future.successful(Created(Json.toJson(Json.obj("status" -> "success", "message" -> "Событие успешно добавлено!"))))
          case InvalidEndDate => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "message" -> "Событие не может заканчиваться в прошлом!"))))
          case DateTimeEqualException => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "message" -> "Событие не может начинаться и заканчиваться в одно и то же время!"))))
          case EventAlreadyExists => Future.successful(Conflict(Json.toJson(Json.obj("status" -> "error", "code" -> CONFLICT, "message" -> "На указанное время запланировано другое событие!"))))
          case _ => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "code" -> INTERNAL_SERVER_ERROR, "message" -> "Произошла ошибка при добавлении события!"))))
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

      val currentUser: User = request.identity

      EventForm.form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(Json.toJson(formWithErrors.errors.toString))),
        data => {
          val members: List[UUID] = data.members.get.map(UUID.fromString)

          eventService.updateEvent(eventID, data, members, currentUser).flatMap {
            case EventUpdated(_) => Future.successful(Ok(Json.toJson(Json.obj("status" -> "success", "message" -> "Событие успешно обновлено!"))))
            case EventNotFound => Future.successful(NotFound(Json.toJson(Json.obj("status" -> "error", "code" -> NOT_FOUND, "message" -> "Событие не найдено!"))))
            case InvalidEndDate => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "message" -> "Событие не может заканчиваться в прошлом!"))))
            case DateTimeEqualException => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "message" -> "Событие не может начинаться и заканчиваться в одно и то же время!"))))
            case EventAlreadyExists => Future.successful(Conflict(Json.toJson(Json.obj("status" -> "error", "code" -> CONFLICT, "message" -> "На указанное время запланировано другое событие!"))))
            case EventCreatedByAnotherUser(action) =>
              if (action == "update") Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "message" -> "Событие создано другим пользователем и недоступно для редактирования!"))))
              else Future.successful(Ok)
            case _ => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "code" -> INTERNAL_SERVER_ERROR, "message" -> "Произошла ошибка при обновлении события!"))))
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
        case EventDeleted => Future.successful(Ok(Json.toJson(Json.obj("status" -> "success", "message" -> "Событие успешно удалено!"))))
        case EventCreatedByAnotherUser(action) =>
          if (action == "delete") Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "message" -> "Событие создано другим пользователем и недоступно для удаления!"))))
          else Future.successful(Ok)
        case EventNotFound => Future.successful(NotFound(Json.toJson(Json.obj("status" -> "error", "code" -> NOT_FOUND, "message" -> "Событие не найдено!"))))
        case _ => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "code" -> INTERNAL_SERVER_ERROR, "message" -> "Произошла ошибка при удалении события!"))))
      }
  }
}
