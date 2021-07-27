package models

import play.api.libs.json.{JsValue, Json}
import slick.jdbc.PostgresProfile.api._


case class User(id: Int, name: String, position: String, email: String, password: String)
case class Item(id: Int, name: String)
case class Event(id: Int, title: String, orgUserId: Int, members: Option[JsValue], itemId: Int, description: Option[String])

trait DatabaseSchema {

  class Users(tag: Tag) extends Table[User](tag, "Users") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def position = column[String]("position")
    def email = column[String]("email")
    def password = column[String]("password")
    def * = (id, name, position,email,password) <> (User.tupled, User.unapply)
  }

  val users =  TableQuery[Users]

  class Items(tag: Tag) extends Table[Item](tag, "Items") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> (Item.tupled, Item.unapply)
  }

  val items = TableQuery[Items]

  private implicit val jsValueMappedColumnType: BaseColumnType[JsValue] =
    MappedColumnType.base[JsValue, String](Json.stringify, Json.parse)

  class Events(tag: Tag) extends Table[Event](tag, "Events") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def orgUserId = column[Int]("orgUserID")
    def members = column[Option[JsValue]]("members")
    def itemId = column[Int]("itemID")
    def description = column[Option[String]]("description")

    // Настраиваем внешние ключи
    def item = foreignKey("ITM_FK", itemId, items)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
    def orgPerson = foreignKey("ORG_FK", orgUserId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def * = (id, title, orgUserId, members, itemId, description) <> (Event.tupled, Event.unapply)
  }

  val events = TableQuery[Events]

  val allSchemas = users.schema ++ items.schema ++ events.schema
}