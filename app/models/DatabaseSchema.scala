package models

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._

import java.time.{LocalDate, LocalTime}

case class User(id: Int, name: String, position: String, email: String, password: String)
case class Item(id: Int, name: String)
case class Event(id:Int, title: String, date: LocalDate, startAt: LocalTime, endAt: LocalTime, orgUserId: Int, members: Option[List[User]], itemId: Int, description: Option[String])

trait DatabaseSchema {
  class Users(tag: Tag) extends Table[User](tag, "Users") {
    def id = column[Int]("ID", O.SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def position = column[String]("position")
    def email = column[String]("email", O.Unique)
    def password = column[String]("password")
    def * = (id, name, position,email,password) <> (User.tupled, User.unapply)
  }

  val users =  TableQuery[Users]

  class Items(tag: Tag) extends Table[Item](tag, "Items") {
    def id = column[Int]("ID", O.SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> (Item.tupled, Item.unapply)
  }

  val items = TableQuery[Items]

  // Json Converters

  implicit val ListUserWrites: Writes[List[User]] = Json.writes[List[User]]

  implicit val UserReads: Reads[User] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "position").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )(User.apply _)

  implicit val UserWrites: Writes[User] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "position").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "password").write[String]
    )(unlift(User.unapply))

  implicit val ItemReads: Reads[Item] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String]
    )(Item.apply _)

  implicit val ItemWrites: Writes[Item] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(Item.unapply))

  implicit val EventReads: Reads[Event] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "title").read[String] and
      (JsPath \ "date").read[LocalDate] and
      (JsPath \ "startAt").read[LocalTime] and
      (JsPath \ "endAt").read[LocalTime] and
      (JsPath \ "orgUserId").read[Int] and
      (JsPath \ "members").readNullable[List[User]] and
      (JsPath \ "itemId").read[Int] and
      (JsPath \ "description").readNullable[String]
    )(Event.apply _)

  implicit val EventWrites: Writes[Event] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "title").write[String] and
      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "startAt").write[LocalTime] and
      (JsPath \ "endAt").write[LocalTime] and
      (JsPath \ "orgUserId").write[Int] and
      (JsPath \ "members").writeNullable[List[User]] and
      (JsPath \ "itemId").write[Int] and
      (JsPath \ "description").writeNullable[String]
    )(unlift(Event.unapply))

    implicit val OptListUserMappedColumnType: BaseColumnType[Option[List[User]]] =
    MappedColumnType.base[Option[List[User]], String](
      list => Json.stringify(Json.toJson(list)),
      column => Json.parse(column).asOpt[List[User]]
    )

  class Events(tag: Tag) extends Table[Event](tag, "Events") {
    def id = column[Int]("ID", O.SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def date = column[LocalDate]("date")
    def startAt = column[LocalTime]("startAt")
    def endAt = column[LocalTime]("endAt")
    def orgUserId = column[Int]("orgUserID")
    def members = column[Option[List[User]]]("members", O.SqlType("json"))
    def itemId = column[Int]("itemID")
    def description = column[Option[String]]("description")

    // Настраиваем внешние ключи
    def item = foreignKey("ITM_FK", itemId, items)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
    def orgPerson = foreignKey("ORG_FK", orgUserId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def * = (id, title, date, startAt, endAt, orgUserId, members, itemId, description) <> (Event.tupled, Event.unapply)
  }

  val events = TableQuery[Events]

  val allSchemas = users.schema ++ items.schema ++ events.schema
}