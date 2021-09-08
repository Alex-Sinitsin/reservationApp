package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Json, OFormat}

object UserForm {

  case class UserData(id: String, name: String, lastName: String, position: String, email: String, role: String)

  implicit val UserDataFormat: OFormat[UserData] = Json.format[UserData]

  /**
   * Форма Play Framework.
   */
  val form: Form[UserData] = Form(
    mapping(
      "id" -> ignored(""),
      "name" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "position" -> nonEmptyText,
      "email" -> email,
      "role" -> nonEmptyText
    )(UserData.apply)(UserData.unapply)
  )
}
