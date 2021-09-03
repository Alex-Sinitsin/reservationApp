package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Json, OFormat}

/**
 * Форма, которая принимает данные для авторизации со стороны клиента.
 */
object SignInForm {

  /**
   * Данные формы.
   *
   * @param email Электронная почта пользователя.
   * @param password Пароль пользователя.
   */
  case class CredentialsSignInData(email: String, password: String)

  implicit val CredentialsSignInFormat: OFormat[CredentialsSignInData] = Json.format[CredentialsSignInData]

  /**
   * Форма Play Framework.
   */
  val form: Form[CredentialsSignInData] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(CredentialsSignInData.apply)(CredentialsSignInData.unapply)
  )
}