package models.daos

import com.google.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

import models.Item

class ItemDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DatabaseDAO {
  /**
   * Добавление нового объекта
   *
   * @param item Объект для добавления
   * @return
   */
  def add(item: Item): Future[Item] =
    db.run(items += Item(item.id, item.name)).map(_ => item)

  /**
   * Обновляет данные объекта
   *
   * @param item Объект для обновления
   * @return
   */
  def update(item: Item): Future[Item] =
    db.run(items.filter(_.id === item.id).map(itm => itm.name).update(item.name)).map(_ => item)

  /**
   * Удаляет данные объекта
   *
   * @param itemID ID объекта
   * @return
   */
  def remove(itemID: Long): Future[Unit] =
    db.run(items.filter(_.id === itemID).delete).map(_ => ())
}
