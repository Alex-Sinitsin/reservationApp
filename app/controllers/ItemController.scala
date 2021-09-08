package controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ItemForm
import models.services._
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.{HasSignUpMethod, JWTEnvironment}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ItemController @Inject()(silhouette: Silhouette[JWTEnvironment],
                               controllerComponents: ControllerComponents,
                               itemService: ItemService,
                               hasSignUpMethod: HasSignUpMethod)
                              (implicit ex: ExecutionContext) extends AbstractController(controllerComponents) {

  /**
   * Выводит список всех объектов
   *
   * @return
   */
  def listAll(id: Long): Action[AnyContent] = silhouette.SecuredAction.async {
    implicit request: Request[AnyContent] =>
      if (id == -1)
        itemService.retrieveAll.flatMap { items =>
          Future.successful(Ok(Json.toJson(items)).withHeaders("X-Total-Count" -> items.size.toString))
        }
      else
        itemService.retrieveByID(id).flatMap {
          case Some(item) => Future.successful(Ok(Json.toJson(item)))
          case None => Future.successful(NotFound(Json.toJson(Json.obj("status" -> "error", "code" -> NOT_FOUND, "message" -> "Объект не найден!"))))
        }
  }

  /**
   * Сохраняет данные объекта
   *
   * @param itemID ID объекта, который необходимо сохранить
   * @return Результат выполнения операции
   */
  def saveItem(itemID: Long): Action[AnyContent] = silhouette.SecuredAction(hasSignUpMethod[JWTEnvironment#A](CredentialsProvider.ID)).async { implicit request: SecuredRequest[JWTEnvironment, AnyContent] =>

    val currentUser = request.identity

    ItemForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(Json.toJson(formWithErrors.errors.toString))),
      data => {
        itemService.createOrUpdate(itemID, data, currentUser).flatMap {
          case ItemCreated(item) => Future.successful(Created(Json.toJson(Json.obj("status" -> "success", "message" -> "Объект успешно добавлен!", "data" -> item))))
          case ItemUpdated(item) => Future.successful(Ok(Json.toJson(Json.obj("status" -> "success", "message" -> "Объект успешно обновлен!", "data" -> item.copy(id = itemID)))))
          case ItemAlreadyExist => Future.successful(Conflict(Json.toJson(Json.obj("status" -> "error", "code" -> CONFLICT, "message" -> "Объект с таким именем уже существует!"))))
          case OperationForbidden => Future.successful(Forbidden(Json.toJson(Json.obj("status" -> "error", "code" -> FORBIDDEN, "message" -> "Недостаточно прав для выполнения операции!"))))
          case _ => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "code" -> INTERNAL_SERVER_ERROR, "message" -> "Произошла ошибка при сохранении объекта!"))))
        }
      }
    )
  }

  /**
   * Удаляет данные объекта
   *
   * @param itemID ID объекта, который необходимо удалить
   * @return Результат выполнения операции
   */
  def deleteItem(itemID: Long): Action[AnyContent] = silhouette.SecuredAction(hasSignUpMethod[JWTEnvironment#A](CredentialsProvider.ID)).async { implicit request: SecuredRequest[JWTEnvironment, AnyContent] =>

    val currentUser = request.identity

    itemService.delete(itemID, currentUser).flatMap {
      case ItemDeleted => Future.successful(Ok(Json.toJson(Json.obj("status" -> "success", "message" -> "Объект успешно удален!"))))
      case ItemNotFound => Future.successful(NotFound(Json.toJson(Json.obj("status" -> "error", "code" -> NOT_FOUND, "message" -> "Объект не найден!"))))
      case OperationForbidden => Future.successful(Forbidden(Json.toJson(Json.obj("status" -> "error", "code" -> FORBIDDEN, "message" -> "Недостаточно прав для выполнения операции!"))))
      case _ => Future.successful(BadRequest(Json.toJson(Json.obj("status" -> "error", "code" -> INTERNAL_SERVER_ERROR, "message" -> "Произошла ошибка при удалении объекта!"))))
    }
  }
}
