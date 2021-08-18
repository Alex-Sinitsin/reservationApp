package models.daos

import com.mohiva.play.silhouette.api.LoginInfo

import play.api.libs.json.Json

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import java.sql.Timestamp
import java.time.{LocalDate, LocalTime}
import java.util.UUID

import models._

/**
 * Трейт, содержит модель базы данных Slick и описание таблиц
 */
trait TableDefinitions {
  class AuthTokens(tag: Tag) extends Table[DBAuthToken](tag, Some("auth"),"silhouette_tokens") {
    def id = column[UUID]("id", O.PrimaryKey)
    def userId = column[UUID]("user_id")
    def expiry = column[Timestamp]("expiry")
    def * : ProvenShape[DBAuthToken] = (id, userId, expiry).<>(DBAuthToken.tupled, DBAuthToken.unapply)
  }

  case class DBUserRoles(id: Int, name: String)

  class UserRoles(tag: Tag) extends Table[DBUserRoles](tag, Some("auth"),"silhouette_user_roles") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name).<> (DBUserRoles.tupled, DBUserRoles.unapply)
  }

  case class DBUser(id: UUID, name: String, lastName: String, position: String, email: String, roleId: Int)

  object DBUser {
    def toUser(u: DBUser): User = User(u.id, u.name, u.lastName, u.position, u.email, Some(UserRoles.toHumanReadable(u.roleId)))
    def fromUser(u: User): DBUser = DBUser(u.id, u.name, u.lastName, u.position, u.email, UserRoles.toDBReadable(u.role.toString))
  }

  class Users(tag: Tag) extends Table[DBUser](tag, Some("auth"),"silhouette_users") {
    def id = column[UUID]("id", O.PrimaryKey)
    def name = column[String]("name")
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

  implicit val OptListUserMappedColumnType: BaseColumnType[Option[List[User]]] =
    MappedColumnType.base[Option[List[User]], String](
      list => Json.stringify(Json.toJson(list)),
      column => Json.parse(column).asOpt[List[User]]
    )

  class Events(tag: Tag) extends Table[Event](tag, Some("app"), "Events") {
    def id = column[Long]("ID", O.SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def date = column[LocalDate]("date")
    def startAt = column[LocalTime]("startAt")
    def endAt = column[LocalTime]("endAt")
    def orgUserId = column[UUID]("orgUserID")
    def members = column[Option[List[User]]]("members", O.SqlType("json"))
    def itemId = column[Long]("itemID")
    def description = column[Option[String]]("description")
    def * = (id, title, date, startAt, endAt, orgUserId, members, itemId, description).<> ((Event.apply _).tupled, Event.unapply)
  }

  class Items(tag: Tag) extends Table[Item](tag, Some("app"), "Items") {
    def id = column[Long]("ID", O.SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name).<> ((Item.apply _).tupled, Item.unapply)
  }

  val users = TableQuery[Users]
  val items = TableQuery[Items]
  val events = TableQuery[Events]
  val authTokens = TableQuery[AuthTokens]
  val userRoles = TableQuery[UserRoles]
  val loginInfos = TableQuery[LoginInfos]
  val userLoginInfos = TableQuery[UserLoginInfos]
  val passwordInfos = TableQuery[PasswordInfos]

  def loginInfoQuery(loginInfo: LoginInfo): Query[LoginInfos, DBLoginInfo, Seq] =
    loginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
