package models.daos

import com.google.inject.Inject
import models.Event
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class EventDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DatabaseDAO {
  /**
   * Извлекает событие по дате начала и дате окончания
   *
   * @param startDateTime Дата и время начала события
   * @param endDateTime Дата и время окончанния события
   * @return
   */
  def getByDateTime(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Future[Option[Event]] = {
    db.run(events.filter(evt =>
      evt.startDateTime >= startDateTime && evt.endDateTime <= endDateTime ||
        evt.endDateTime >= startDateTime && evt.endDateTime <= endDateTime
    ).result.headOption)
  }

  /**
   * Извлекает событие по его ID
   *
   * @param eventID ID события
   * @return
   */
  def getByID(eventID: String): Future[Option[Event]] = {
    db.run(events.filter(_.id === eventID.toLong).result.headOption)
  }

  /**
   * Добавление нового события
   *
   * @param event Событие для добавления
   * @return
   */
  def add(event: Event): Future[Event] = {
    db.run(events +=
        Event(event.id, event.title, event.startDateTime, event.endDateTime, event.orgUserId, event.members, event.itemId, event.description))
      .map(_ => event)
  }

  /**
   * Обновляет данные события
   *
   * @param event Объект для обновления
   * @return
   */
  def update(event: Event): Future[Event] =
    db.run(events.filter(_.id === event.id)
      .map(evt => (evt.title, evt.startDateTime, evt.endDateTime, evt.orgUserId, evt.members, evt.itemId, evt.description))
      .update((event.title, event.startDateTime, event.endDateTime, event.orgUserId, Some(Json.toJson(event.members)), event.itemId, event.description)).map(_ => event))

  /**
   * Удаляет данные события
   *
   * @param eventID ID события
   * @return
   */
  def remove(eventID: Long): Future[Unit] =
    db.run(items.filter(_.id === eventID).delete).map(_ => ())
}
