package models.daos

import com.google.inject.Inject
import models.Event
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._

import java.sql.Timestamp
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
    val startTimestamp = Timestamp.valueOf(startDateTime)
    val endTimestamp = Timestamp.valueOf(endDateTime)

    db.run(
      sql"""SELECT * FROM app.events WHERE app.events.start_datetime BETWEEN $startTimestamp AND $endTimestamp OR
                           app.events.end_datetime BETWEEN $startTimestamp AND $endTimestamp OR
                           $startTimestamp BETWEEN app.events.start_datetime AND app.events.end_datetime OR
                           $endTimestamp BETWEEN app.events.start_datetime AND app.events.end_datetime"""
        .as[Event].headOption)
  }

  /**
   * Извлекает событие по его ID
   *
   * @param eventID ID события
   * @return
   */
  def getByID(eventID: Long): Future[Option[Event]] = {
    db.run(events.filter(_.id === eventID).result.headOption)
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
  def delete(eventID: Long): Future[Boolean] =
    db.run(events.filter(_.id === eventID).delete).map(_ > 0)
}
