package forms

import forms.SignUpForm.CredentialsSignUpData
import models.Event
import org.joda.time.format.DateTimeFormat
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Json, OFormat}

import java.time.{LocalDate, LocalTime}

object EventForm {
  case class EventData(title: String, date: LocalDate, startAt: LocalTime, endAt: LocalTime, orgUserID: String, members: Option[List[String]], itemID: Long, description: Option[String])

  implicit val EventFormat: OFormat[EventData] = Json.format[EventData]

  /**
   * Форма Play Framework.
   */
  val form: Form[EventData] = Form(
    mapping(
      "title" -> nonEmptyText,
      "date" -> localDate("yyyy-MM-dd"),
      "startAt" -> localTime("HH:MM"),
      "endAt" -> localTime("HH:MM"),
      "orgUserID" -> nonEmptyText,
      "members" -> optional(list(text)),
      "itemID" -> longNumber(1),
      "description" -> optional(text)
    )(EventData.apply)(EventData.unapply)
  )
}
