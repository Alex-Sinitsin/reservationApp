package models

import play.api.libs.json.{Json, OFormat}

import java.util.UUID

case class EventMember(event_id: Long, user_id: UUID)
case class EventWithMembers(eventId: Long, data: Seq[(Event, Seq[User])])
case class EventTemp(events: Seq[Event], users: Seq[User], eventMembers: Seq[EventMember])

object EventMember {
  implicit val eventMemberFormat: OFormat[EventMember] = Json.format[EventMember]
}
object EventWithMembers {
  implicit val eventWithMembersFormat: OFormat[EventWithMembers] = Json.format[EventWithMembers]
}
