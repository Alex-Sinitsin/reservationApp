package forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The form which handles the submission of the credentials.
 */
object SignInForm {

  /**
   * A play framework form.
   */
  val form: Form[CredentialsSingInData] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(CredentialsSingInData.apply)(CredentialsSingInData.unapply)
  )

  /**
   * The form data.
   *
   * @param email The email of the user.
   * @param password The password of the user.
   */
  case class CredentialsSingInData(email: String, password: String)
}