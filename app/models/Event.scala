package models

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Reads, Writes}

import java.time.{LocalDate, LocalTime}
import java.util.UUID

case class Event(id: Long, title: String, date: LocalDate, startAt: LocalTime, endAt: LocalTime, orgUserId: UUID, members: Option[List[User]], itemId: Long, description: Option[String])

object Event {
  implicit val EventReads: Reads[Event] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "title").read[String] and
      (JsPath \ "date").read[LocalDate] and
      (JsPath \ "startAt").read[LocalTime] and
      (JsPath \ "endAt").read[LocalTime] and
      (JsPath \ "orgUserId").read[UUID] and
      (JsPath \ "members").readNullable[List[User]] and
      (JsPath \ "itemId").read[Long] and
      (JsPath \ "description").readNullable[String]
    )(Event.apply _)

  implicit val EventWrites: Writes[Event] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "title").write[String] and
      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "startAt").write[LocalTime] and
      (JsPath \ "endAt").write[LocalTime] and
      (JsPath \ "orgUserId").write[UUID] and
      (JsPath \ "members").writeNullable[List[User]] and
      (JsPath \ "itemId").write[Long] and
      (JsPath \ "description").writeNullable[String]
    )(unlift(Event.unapply))
}