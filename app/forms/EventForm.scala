package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

object EventForm {
  case class EventData(title: String, startDateTime: LocalDateTime, endDateTime: LocalDateTime, orgUserID: String, members: Option[List[String]], itemID: Long, description: Option[String])

  implicit val EventFormat: OFormat[EventData] = Json.format[EventData]

  /**
   * Форма Play Framework.
   */
  val form: Form[EventData] = Form(
    mapping(
      "title" -> nonEmptyText,
      "startDateTime" -> localDateTime("yyyy-MM-dd HH:mm"),
      "endDateTime" -> localDateTime("yyyy-MM-dd HH:mm"),
      "orgUserID" -> nonEmptyText,
      "members" -> optional(list(text)),
      "itemID" -> longNumber(1),
      "description" -> optional(text)
    )(EventData.apply)(EventData.unapply)
  )
}
