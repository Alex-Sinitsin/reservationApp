package forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * CredentialsSingUpData
 *
 * @param name       The first name of a user.
 * @param lastName        The last name of a user.
 * @param position        The last name of a user.
 * @param email           The email of the user.
 * @param password        The password of the user.
 */
case class CredentialsSingUpData(name: String, lastName: String, position: String, email: String, password: String)

/**
 * The form which handles the sign up process.
 */
object SignUpForm {
  /**
   * A play framework form.
   */
  val form: Form[CredentialsSingUpData] = Form(
    mapping(
      "name" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "position" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(CredentialsSingUpData.apply)(CredentialsSingUpData.unapply)
  )
}
