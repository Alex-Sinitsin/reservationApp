package models

import com.mohiva.play.silhouette.api.Identity
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, JsValue, Json, OFormat, Reads}

import java.util.UUID

/**
 * User model Object
 *
 * @param ID d
 * @param name d
 * @param lastName d
 * @param position d
 * @param email d
 * @param role d
 */
case class User(ID: UUID, name: String, lastName: String, position: String, email: String, role: Option[String]) extends Identity

//{
//
//  /**
//   * Generates login info from email
//   *
//   * @return login info
//   */
//  def loginInfo: LoginInfo = LoginInfo(CredentialsProvider.ID, email)
//
//  /**
//   * Generates password info from password.
//   *
//   * @return password info
//   */
//  def passwordInfo: PasswordInfo = PasswordInfo(BCryptSha256PasswordHasher.ID, password)
//}
