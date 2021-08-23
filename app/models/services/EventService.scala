package models.services

import forms.EventForm.EventData
import models.Event
import models.daos.{EventDAO, UserDAO}
import play.api.libs.json.Json

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EventService @Inject()(userDAO: UserDAO, eventDAO: EventDAO)(implicit ex: ExecutionContext){
  def saveEvent(eventData: EventData, members: List[UUID]): Future[EventResult] = {
    eventDAO.getByTitle(eventData.title).flatMap {
      case Some(event) => Future.successful(EventAlreadyExists)
      case None =>
        for {
          memberUsers <- userDAO.findUsersByID(members).map(data => data.toList)
          eventToAdd = Event(-1, eventData.title, eventData.date, eventData.startAt, eventData.endAt, UUID.fromString(eventData.orgUserID), Some(Json.toJson(memberUsers)), eventData.itemID, eventData.description)
          newEvent <- eventDAO.add(eventToAdd)
        } yield EventCreated(newEvent)
    }
  }
}

sealed trait EventResult
case object EventAlreadyExists extends EventResult
case class EventCreated(event: Event) extends EventResult
