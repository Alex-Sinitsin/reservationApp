package models.daos

import com.google.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

import models.Item

class ItemDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DatabaseDAO {

  /**
   * Извлекает список всех объектов из таблицы
   *
   * @return Список объектов
   */
  def getAll: Future[Seq[Item]] = {
    db.run(items.result)
  }

  /**
   * Производит выборку объекта по его ID
   *
   * @param itemID ID объектаб который необходимо получить
   * @return Найденный объект, иначе None
   */
  def getByID(itemID: Long): Future[Option[Item]] = {
    db.run(items.filter(_.id === itemID).result.headOption)
  }


  /**
   * Производит выборку объекта по его имени
   *
   * @param name Имя объектаб который необходимо получить
   * @return Найденный объект, иначе None
   */
  def getByName(name: String): Future[Option[Item]] = {
    db.run(items.filter(_.name === name).result.headOption)
  }

  /**
   * Добавление нового объекта
   *
   * @param item Объект для добавления
   * @return Объект, который был добавлен
   */
  def add(item: Item): Future[Item] =
    db.run(items += Item(item.id, item.name)).map(_ => item)

  /**
   * Обновляет данные объекта
   *
   * @param item Объект для обновления
   * @return Объект, который был обновлен
   */
  def update(itemID: Long, item: Item): Future[Item] =
    db.run(items.filter(_.id === itemID).map(itm => itm.name).update(item.name)).map(_ => item)

  /**
   * Удаляет данные объекта
   *
   * @param itemID ID объекта
   * @return
   */
  def delete(itemID: Long): Future[Boolean] =
    db.run(items.filter(_.id === itemID).delete).map(_ > 0)
}
