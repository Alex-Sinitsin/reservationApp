package models

import com.mohiva.play.silhouette.api.Identity

import java.util.UUID

/**
 * User model Object
 *
 * @param id d
 * @param name d
 * @param lastName d
 * @param position d
 * @param email d
 * @param role d
 */
case class User(id: UUID, name: String, lastName: String, position: String, email: String, role: Option[String]) extends Identity

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
