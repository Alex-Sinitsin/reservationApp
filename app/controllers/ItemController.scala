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
  def listAll(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: Request[AnyContent] =>
    itemService.retrieveAll.flatMap { items => Future.successful(Ok(Json.toJson(items))) }
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
          case ItemCreated(_) => Future.successful(Ok(Json.obj("success" -> "Объект успешно добавлен!")))
          case ItemUpdated(_) => Future.successful(Ok(Json.obj("success" -> "Объект успешно обновлен!")))
          case ItemAlreadyExist => Future.successful(BadRequest(Json.obj("error" -> "Объект с таким именем уже существует!")))
          case OperationForbidden => Future.successful(Forbidden(Json.obj("error" -> "Недостаточно прав для выполнения операции!")))
          case _ => Future.successful(BadRequest(Json.obj("error" -> "Произошла ошибка при сохранении объекта!")))
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
      case ItemDeleted => Future.successful(Ok(Json.obj("success" -> "Объект успешно удален!")))
      case ItemNotFound => Future.successful(BadRequest(Json.obj("error" -> "Объект не найден!")))
      case OperationForbidden => Future.successful(Forbidden(Json.obj("error" -> "Недостаточно прав для выполнения операции!")))
      case _ => Future.successful(BadRequest(Json.obj("error" -> "Произошла ошибка при удалении объекта!")))
    }
  }
}
