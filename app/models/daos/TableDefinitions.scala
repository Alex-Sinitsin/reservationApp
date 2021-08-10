package models.daos

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.{AuthToken, User, UserRoles}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import java.time.ZonedDateTime
import java.util.UUID

trait TableDefinitions {
  class AuthTokens(tag: Tag) extends Table[AuthToken](tag, Some("auth"),"silhouette_tokens") {
    def id = column[UUID]("id", O.PrimaryKey)
    def userId = column[UUID]("user_id")
    def expiry = column[ZonedDateTime]("expiry")
    def * : ProvenShape[AuthToken] = (id, userId, expiry).<>(AuthToken.tupled, AuthToken.unapply)
  }

  case class DBUserRoles(id: Int, name: String)

  class UserRoles(tag: Tag) extends Table[DBUserRoles](tag, Some("auth"),"silhouette_user_roles") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name).<> (DBUserRoles.tupled, DBUserRoles.unapply)
  }

  case class DBUser(ID: UUID, name: String, lastName: String, position: String, email: String, roleId: Int)

  object DBUser {
    def toUser(u: DBUser): User = User(u.ID, u.name, u.lastName, u.position, u.email, Some(UserRoles.toHumanReadable(u.roleId)))
    def fromUser(u: User): DBUser = DBUser(u.ID, u.name, u.lastName, u.position, u.email, UserRoles.toDBReadable(u.role.toString))
  }

  class Users(tag: Tag) extends Table[DBUser](tag, Some("auth"),"silhouette_users") {
    def id = column[UUID]("ID", O.PrimaryKey)
    def name = column[String]("first_name")
    def lastName = column[String]("last_name")
    def position = column[String]("position")
    def email = column[String]("email")
    def roleId = column[Int]("role_id")
    def * : ProvenShape[DBUser] = (id, name, lastName, position, email, roleId).<> ((DBUser.apply _).tupled, DBUser.unapply)
  }

  case class DBLoginInfo(id: Option[Long], providerID: String, providerKey: String)

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, Some("auth"),"silhouette_login_info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")
    def * = (id.?, providerID, providerKey).<> ((DBLoginInfo.apply _).tupled, DBLoginInfo.unapply)
  }

  object DBLoginInfo {
    def fromLoginInfo(loginInfo: LoginInfo): DBLoginInfo = DBLoginInfo(None, loginInfo.providerID, loginInfo.providerKey)
    def toLoginInfo(dbLoginInfo: DBLoginInfo): LoginInfo = LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey)
  }

  case class DBUserLoginInfo(userID: UUID, loginInfoId: Long)

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, Some("auth"), "silhouette_user_login_info") {
    def userID = column[UUID]("user_id")
    def loginInfoId = column[Long]("login_info_id")
    def * = (userID, loginInfoId).<> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(hasher: String, password: String, salt: Option[String], loginInfoId: Long)

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, Some("auth"),"silhouette_password_info") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("login_info_id")
    def * = (hasher, password, salt, loginInfoId).<> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  val users = TableQuery[Users]
  val authTokens = TableQuery[AuthTokens]
  val userRoles = TableQuery[UserRoles]
  val loginInfos = TableQuery[LoginInfos]
  val userLoginInfos = TableQuery[UserLoginInfos]
  val passwordInfos = TableQuery[PasswordInfos]

  def loginInfoQuery(loginInfo: LoginInfo): Query[LoginInfos, DBLoginInfo, Seq] =
    loginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
