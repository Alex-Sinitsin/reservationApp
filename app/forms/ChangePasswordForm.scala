package forms

import play.api.data.Forms._
import play.api.data._

object ChangePasswordForm {
  /**
   * Форма изменения пароля Play Framework
   */
  val form: Form[(String, String, String)] = Form(tuple(
    "currentPassword" -> nonEmptyText,
    "newPassword" -> nonEmptyText,
    "confirmPassword" -> nonEmptyText
  ).verifying ("Введенные пароли не совпадают", passwords => passwords._2 == passwords._3))
}
