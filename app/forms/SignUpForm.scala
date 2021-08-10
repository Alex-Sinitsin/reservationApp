package forms

import models.services.CredentialsSingUpData
import play.api.data.Form
import play.api.data.Forms._

/**
 * The form which handles the sign up process.
 */
object SignUpForm {
  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "position" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(CredentialsSingUpData.apply)(CredentialsSingUpData.unapply)
  )
}
