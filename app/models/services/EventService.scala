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
  def saveEvent(eventData: EventData, members: List[UUID]): Future[EventResult] = {
    val newEndDateTime: LocalDateTime = eventData.endDateTime.getMinute match {
      case 0 => eventData.endDateTime.minus(Duration.ofMinutes(1))
      case _ => eventData.endDateTime
    }

    val compareDateTimeValue = eventData.startDateTime compareTo eventData.endDateTime

    if (compareDateTimeValue > 0) Future.successful(InvalidEndDate)
    else if (compareDateTimeValue == 0) Future.successful(TimeEqualException)
    else {
      eventDAO.getByDateTime(eventData.startDateTime, eventData.endDateTime).flatMap {
        case Some(event) => Future.successful(EventAlreadyExists)
        case None =>
          for {
            memberUsers <- userDAO.findUsersByID(members).map(data => data.toList)
            eventToAdd = Event(-1, eventData.title, eventData.startDateTime, newEndDateTime, UUID.fromString(eventData.orgUserID), Some(Json.toJson(memberUsers)), eventData.itemID, eventData.description)
            newEvent <- eventDAO.add(eventToAdd)
          } yield EventCreated(newEvent)
      }
    }
  }
}

sealed trait EventResult
case object EventAlreadyExists extends EventResult
case object InvalidEndDate extends EventResult
case object TimeEqualException extends EventResult
case class EventCreated(event: Event) extends EventResult
