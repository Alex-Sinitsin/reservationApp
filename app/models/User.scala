package models

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher

import java.util.UUID

/**
 * The user object.
 *
 * @param id The unique ID of the user.
 * @param lastName the last name of the authenticated user.
 * @param password the user's password
 */
case class User(
  id: UUID,
  email: String,
  name: String,
  lastName: String,
  password: String) extends Identity {

  /**
   * Generates login info from email
   *
   * @return login info
   */
  def loginInfo = LoginInfo(CredentialsProvider.ID, email)

  /**
   * Generates password info from password.
   *
   * @return password info
   */
  def passwordInfo = PasswordInfo(BCryptSha256PasswordHasher.ID, password)
}
