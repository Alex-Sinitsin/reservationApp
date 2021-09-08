package models.services

import models.{Item, User}
import models.daos.ItemDAO

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ItemService @Inject()(itemDAO: ItemDAO)(implicit ex: ExecutionContext) {

  /**
   * Функция проверки роли "Администратор" пользователя
   *
   * @param currentUser Данные авторизованного пользователя
   * @return
   */
  private def isAdmin(currentUser: User): Boolean = {
    if(currentUser.role.contains("Admin")) true
    else false
  }

  /**
   * Извлекает список объектов
   *
   * @return
   */
  def retrieveAll: Future[Seq[Item]] = itemDAO.getAll

  /**
   * Извлекает данные объекта по его ID
   *
   * @param id ID объекта
   * @return Данные объекта, иначе None
   */
  def retrieveByID(id: Long): Future[Option[Item]] = itemDAO.getByID(id)

  /**
   * Создает или обновляет объект
   *
   * @param itemID ID объекта
   * @param itemData Данные с формы
   * @param currentUser Данные авторизованного пользователя
   * @return
   */
  def createOrUpdate(itemID: Long, itemData: Item, currentUser: User): Future[ItemResult] = {
    if(isAdmin(currentUser)) {
      itemDAO.getByID(itemID).flatMap {
        case Some(_) =>
          itemDAO.getByName(itemData.name).flatMap{
            case Some(_) =>Future.successful(ItemAlreadyExist)
            case None => itemDAO.update(itemID, itemData).map(ItemUpdated)
          }
        case None => itemDAO.add(itemData).map(ItemCreated)
      }
    } else Future.successful(OperationForbidden)
  }

  /**
   * Удаляет объект
   *
   * @param itemID ID объекта
   * @param currentUser Данные авторизованного пользователя
   * @return
   */
  def delete(itemID: Long, currentUser: User): Future[ItemResult] = {
    if(isAdmin(currentUser)) {
      itemDAO.getByID(itemID).flatMap {
        case Some(_) => itemDAO.delete(itemID).flatMap { delResult =>
          if(delResult) Future.successful(ItemDeleted)
          else Future.successful(ItemDeleteError("Произошла ошибка при удалении объекта!")) }
        case None => Future.successful(ItemNotFound)
      }
    } else Future.successful(OperationForbidden)
  }
}


/**
 * Объекты, использующиеся для возврата рельтата
 */
sealed trait ItemResult

case object OperationForbidden extends ItemResult
case object ItemDeleted extends ItemResult
case object ItemNotFound extends ItemResult
case object ItemAlreadyExist extends ItemResult

case class ItemUpdated(item: Item) extends ItemResult
case class ItemCreated(item: Item) extends ItemResult
case class ItemDeleteError(msg: String) extends ItemResult