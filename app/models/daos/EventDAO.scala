package models.daos

import com.google.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile.api._
import models.Event
import play.api.libs.json.Json

class EventDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DatabaseDAO {
  /**
   * Добавление нового события
   *
   * @param event Событие для добавления
   * @return
   */
  def add(event: Event): Future[Event] =
    db.run(events +=
      Event(event.id, event.title, event.date, event.startAt, event.endAt, event.orgUserId, Some(Json.toJson(event.members)), event.itemId, event.description))
      .map(_ => event)

  //TODO: Продумать уникальное поле

  /**
   * Извлекает событие по его названию
   *
   * @param title Название события
   * @return Событие, если найдено, иначе None
   */
  def getByTitle(title: String): Future[Option[Event]] = {
    db.run(events.filter(_.title === title).result.headOption)
  }

  /**
   * Обновляет данные события
   *
   * @param event Объект для обновления
   * @return
   */
  def update(event: Event): Future[Event] =
    db.run(events.filter(_.id === event.id)
      .map(evt => (evt.title, evt.date, evt.startAt, evt.endAt, evt.orgUserId, evt.members, evt.itemId, evt.description))
      .update((event.title, event.date, event.startAt, event.endAt, event.orgUserId, Some(Json.toJson(event.members)), event.itemId, event.description)).map(_ => event))

  /**
   * Удаляет данные события
   *
   * @param eventID ID события
   * @return
   */
  def remove(eventID: Long): Future[Unit] =
    db.run(items.filter(_.id === eventID).delete).map(_ => ())
}
