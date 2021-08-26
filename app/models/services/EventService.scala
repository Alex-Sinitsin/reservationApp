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

  /**
   * Функция обрабатывает дату и время окончания события
   *
   * Если время, например, 13:00, то от него отнимается 1 минута и возвращается 12:59, иначе возвращается необработанное время
   *
   * @param dateTime Дата и время, необходимое для обработки
   * @return Обработанные дата и время
   */
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

  /**
   * Фрагмент кода для обновления события
   *
   * @param eventID ID события
   * @param eventData Данные о событии
   * @param members ID пользователей
   * @param newEndDateTime Обработанные дата и время окончания события
   * @return Результат выполнения операции и событие, которое было обновлено
   */
  private def updateEventFunction(eventID: String, eventData: EventData, members: List[UUID], newEndDateTime: LocalDateTime): Future[EventResult] = {
    for {
      memberUsers <- userDAO.findUsersByID(members).map(data => data.toList)
      updatedEvent <-
        eventDAO.update(Event(eventID.toLong, eventData.title, eventData.startDateTime, newEndDateTime, UUID.fromString(eventData.orgUserID), Some(Json.toJson(memberUsers)), eventData.itemID, eventData.description))
    } yield EventUpdated(updatedEvent)
  }

  /**
   * Обрабатывает обновления данных события
   *
   * @param eventID ID события
   * @param eventData Данные о событии
   * @param members ID пользователей
   * @return Результат выполнения операции и событие, которое было обновлено
   */
  def updateEvent(eventID: String, eventData: EventData, members: List[UUID]): Future[EventResult] = {
    val newEndDateTime: LocalDateTime = newEventDateTimeBuilder(eventData.endDateTime)

    val compareDateTimeValue = eventData.startDateTime compareTo eventData.endDateTime

    if (compareDateTimeValue > 0) Future.successful(InvalidEndDate)
    else if (compareDateTimeValue == 0) Future.successful(DateTimeEqualException)
    else {
      eventDAO.getByID(eventID).flatMap {
        case Some(eventWithID) =>
          if (eventWithID.startDateTime != eventData.startDateTime && eventWithID.endDateTime != eventData.endDateTime) {
            eventDAO.getByDateTime(eventData.startDateTime, eventData.endDateTime).flatMap {
              case Some(eventWithDateTime) =>
                if(eventWithDateTime.id == eventID.toLong) updateEventFunction(eventID, eventData, members, newEndDateTime)
                else Future.successful(EventAlreadyExists)
              case None => updateEventFunction(eventID, eventData, members, newEndDateTime)
            }
          } else updateEventFunction(eventID, eventData, members, newEndDateTime)
        case None =>
          Future.successful(EventNotFound)
      }
    }
  }

  def deleteEvent(eventID: String) : Future[EventResult] = {
    eventDAO.getByID(eventID).flatMap {
      case Some(_) =>
        eventDAO.delete(eventID.toLong).flatMap {delResult =>
          if (delResult) Future.successful(EventDeleted)
          else Future.successful(Error("Неизвестная ошибка"))
        }
      case None => Future.successful(EventNotFound)
    }
  }
}

/**
 * Объекты, использующиеся для возврата рельтата
 */
sealed trait EventResult

case object InvalidEndDate extends EventResult
case object DateTimeEqualException extends EventResult
case object EventAlreadyExists extends EventResult
case object EventNotFound extends EventResult
case object EventDeleted extends EventResult

case class EventCreated(event: Event) extends EventResult
case class EventUpdated(event: Event) extends EventResult
case class Error(msg: String) extends EventResult
