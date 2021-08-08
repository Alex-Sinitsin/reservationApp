package models

case class Event(id: Int, title: String, orgUserId: Int, members: Option[Seq[User]], itemId: Int, description: Option[String])
