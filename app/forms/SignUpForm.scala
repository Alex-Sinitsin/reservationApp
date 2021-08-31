package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Json, OFormat}

/**
 * Форма, которая принимает данные для регистрации со стороны клиента
 */
object SignUpForm {

  /**
   * Данные формы
   *
   * @param name       Имя пользователя.
   * @param lastName   Фамилия пользователя.
   * @param position   Должность пользователя.
   * @param email      Электронная почта пользователя.
   * @param password   Пароль пользователя.
   */
  case class CredentialsSignUpData(name: String, lastName: String, position: String, email: String, password: String, confirmPassword: String)

  implicit val CredentialsSignUpFormat: OFormat[CredentialsSignUpData] = Json.format[CredentialsSignUpData]

  /**
   * Форма Play Framework.
   */
  val form: Form[CredentialsSignUpData] = Form(
    mapping(
      "name" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "position" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText(minLength = 8),
      "confirmPassword" -> nonEmptyText(minLength = 8)
    )(CredentialsSignUpData.apply)(CredentialsSignUpData.unapply)
  )
}
