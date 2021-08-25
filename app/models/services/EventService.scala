package models.services

import forms.EventForm.EventData
import models.Event
import models.daos.{EventDAO, UserDAO}
import play.api.libs.json.Json

import java.time.{Duration, LocalDateTime}
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EventService @Inject()(userDAO: UserDAO, eventDAO: EventDAO)(implicit ex: ExecutionContext) {

  private def newEventDateTimeBuilder(dateTime: LocalDateTime): LocalDateTime = {
    dateTime.getMinute match {
      case 0 => dateTime.minus(Duration.ofMinutes(1))
      case _ => dateTime
    }
  }

  /**
   * Метод сохраняет новое событие
   *
   * @param eventData Данные с формы
   * @param members Данные из формы (ID пользователей-участников события)
   * @return Добавленное событие
   */
  def saveEvent(eventData: EventData, members: List[UUID]): Future[EventResult] = {
    val newEndDateTime: LocalDateTime = newEventDateTimeBuilder(eventData.endDateTime)

    val compareDateTimeValue = eventData.startDateTime compareTo eventData.endDateTime

    if (compareDateTimeValue > 0) Future.successful(InvalidEndDate)
    else if (compareDateTimeValue == 0) Future.successful(DateTimeEqualException)
    else {
      eventDAO.getByDateTime(eventData.startDateTime, eventData.endDateTime).flatMap {
        case Some(_) => Future.successful(EventAlreadyExists)
        case None =>
          for {
            memberUsers <- userDAO.findUsersByID(members).map(data => data.toList)
            createdEvent <-
              eventDAO.add(Event(-1, eventData.title, eventData.startDateTime, newEndDateTime, UUID.fromString(eventData.orgUserID), Some(Json.toJson(memberUsers)), eventData.itemID, eventData.description))
          } yield EventCreated(createdEvent)
      }
    }
  }

  //TODO: Сделать проверку если пришли различные даты и время
  def updateEvent(eventID: String, eventData: EventData, members: List[UUID]): Future[EventResult] = {
    val newEndDateTime: LocalDateTime = newEventDateTimeBuilder(eventData.endDateTime)

    val compareDateTimeValue = eventData.startDateTime compareTo eventData.endDateTime

    if (compareDateTimeValue > 0) Future.successful(InvalidEndDate)
    else if (compareDateTimeValue == 0) Future.successful(DateTimeEqualException)
    else {
      eventDAO.getByID(eventID).flatMap {
        case Some(_) =>
          for {
            memberUsers <- userDAO.findUsersByID(members).map(data => data.toList)
            updatedEvent <-
              eventDAO.update(Event(eventID.toLong, eventData.title, eventData.startDateTime, newEndDateTime, UUID.fromString(eventData.orgUserID), Some(Json.toJson(memberUsers)), eventData.itemID, eventData.description))
          } yield EventUpdated(updatedEvent)
        case None =>
          Future.successful(EventNotFound)
      }
    }
  }
}

sealed trait EventResult
case object InvalidEndDate extends EventResult
case object DateTimeEqualException extends EventResult
case object EventAlreadyExists extends EventResult
case object EventNotFound extends EventResult
case class EventCreated(event: Event) extends EventResult
case class EventUpdated(event: Event) extends EventResult
