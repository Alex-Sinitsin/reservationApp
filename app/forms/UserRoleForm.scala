package forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * Форма, которая принимает данные для регистрации со стороны клиента
 */
object UserRoleForm {
  val form: Form[Int] = Form(
    single("roleId" -> number)
  )
}
