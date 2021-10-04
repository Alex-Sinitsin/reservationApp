package models.services

import forms.EventForm.EventData
import models.daos.{EventDAO, EventMemberDAO, UserDAO}
import models.{Event, EventMember, EventTemp, EventWithMembers, User}

import java.time.{Duration, LocalDateTime}
import java.util.UUID
import javax.inject.Inject
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

class EventService @Inject()(userDAO: UserDAO, eventDAO: EventDAO, eventMemberDAO: EventMemberDAO)(implicit ex: ExecutionContext) {

  //TODO: Доделать обновление с удалением событий
  //TODO: Сделать метод для получения участников конкретного события и добавить для этого URI

  /**
   * Извлекает список событий
   *
   * @return
   */
  def retrieveAll: Future[ArrayBuffer[EventWithMembers]] = {
    val membersWithInfo: ArrayBuffer[EventWithMembers] = ArrayBuffer.empty

    val dataSummary = for {
      events <- eventDAO.getAll
      event_members <- eventMemberDAO.getAllMembers
      memberIds = event_members.map(member => member.user_id)
      memberInfo <- userDAO.findUsersByID(memberIds)
    } yield EventTemp(events, memberInfo, event_members)

    val d = dataSummary.flatMap { data =>
      val listOfEvents: Array[(Long, Seq[Event])] = data.events.groupBy(_.id).toArray
      val listOfMembers: Array[(Long, Seq[EventMember])] = data.eventMembers.groupBy(_.event_id).toArray

      listOfMembers.map { evtMembers =>
        listOfEvents.map { evts =>
          evtMembers._2.map { member =>
            val users = data.users.filter(_.id == member.user_id)
            evts._2.filter(evt => evt.id == evtMembers._1).map { evt =>
              membersWithInfo += EventWithMembers(evtMembers._1, Seq((evt, users)))
            }
          }
        }
      }
      Future.successful(membersWithInfo)
    }
    d
  }

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
   * Извлекает даные о событии
   *
   * @param eventID ID события
   * @return
   */
  def getEventByID(eventID: Long): Future[Option[Event]] = {
    eventDAO.getByID(eventID)
  }

  /**
   * Метод сохраняет новое событие
   *
   * @param eventData Данные с формы
   * @param members   Данные из формы (ID пользователей-участников события)
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
            createdEventID <- eventDAO.add(Event(-1, eventData.title, eventData.startDateTime, newEndDateTime, UUID.fromString(eventData.orgUserID), eventData.itemID, eventData.description))
            _ <- eventMemberDAO.create(createdEventID, members)
          } yield EventCreated(createdEventID)
      }
    }
  }

  /**
   * Фрагмент кода для обновления события
   *
   * @param eventID        ID события
   * @param eventData      Данные о событии
   * @param members        ID пользователей
   * @param newEndDateTime Обработанные дата и время окончания события
   * @return Результат выполнения операции и событие, которое было обновлено
   */
  private def updateEventFunction(eventID: Long, eventData: EventData, members: List[UUID], newEndDateTime: LocalDateTime, currentUser: User): Future[EventResult] = {
    if (currentUser.id == UUID.fromString(eventData.orgUserID) || currentUser.role.contains("Admin"))
      for {
        memberUsers <- userDAO.findUsersByID(members).map(data => data.toList)
        updatedEvent <- eventDAO.update(Event(eventID, eventData.title, eventData.startDateTime, newEndDateTime, UUID.fromString(eventData.orgUserID), eventData.itemID, eventData.description))
      } yield EventUpdated(updatedEvent)
    else Future.successful(EventCreatedByAnotherUser("update"))
  }

  /**
   * Обрабатывает обновления данных события
   *
   * @param eventID     ID события
   * @param eventData   Данные о событии
   * @param members     ID пользователей-участников
   * @param currentUser Объект данных авторизованного пользователей
   * @return Результат выполнения операции и событие, которое было обновлено
   */
  def updateEvent(eventID: Long, eventData: EventData, members: List[UUID], currentUser: User): Future[EventResult] = {
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
                if (eventWithDateTime.id == eventID)
                  updateEventFunction(eventID, eventData, members, newEndDateTime, currentUser)
                else Future.successful(EventAlreadyExists)
              case None => updateEventFunction(eventID, eventData, members, newEndDateTime, currentUser)
            }
          } else updateEventFunction(eventID, eventData, members, newEndDateTime, currentUser)
        case None =>
          Future.successful(EventNotFound)
      }
    }
  }

  /**
   * Обрабатывает удаление данных события
   *
   * @param eventID     ID события, которое необходимо удалить
   * @param currentUser Объект данных авторизованного пользователя
   * @return Результат выполнения операции
   */
  def deleteEvent(eventID: Long, currentUser: User): Future[EventResult] = {
    eventDAO.getByID(eventID).flatMap {
      case Some(eventData) =>
        if (currentUser.id == eventData.orgUserId || currentUser.role.contains("Admin"))
          eventDAO.delete(eventID).flatMap { delResult =>
            if (delResult) Future.successful(EventDeleted)
            else Future.successful(EventDeleteError("Произошла ошибка при удалении события!"))
          }
        else Future.successful(EventCreatedByAnotherUser("delete"))
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

case class EventCreated(eventId: Long) extends EventResult

case class EventCreatedByAnotherUser(action: String) extends EventResult

case class EventUpdated(event: Event) extends EventResult

case class EventDeleteError(msg: String) extends EventResult
