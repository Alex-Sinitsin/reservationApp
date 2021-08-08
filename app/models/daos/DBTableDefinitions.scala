package models.daos

import models.{Event, Item, User}
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._

import java.time.ZonedDateTime
import java.util.UUID

//TODO: Сделать значения поля Id рандомным


trait DBTableDefinitions {

    class Users(tag: Tag) extends Table[User](tag, "Users") {
      def id = column[UUID]("ID", O.PrimaryKey, O.AutoInc)
      def name = column[String]("name")
      def position = column[String]("position")
      def email = column[String]("email", O.Unique)
      def password = column[String]("password")
      def * = (id, name, position,email,password).<>(User.tupled, User.unapply)
    }


  class Items(tag: Tag) extends Table[Item](tag, "Items") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc, O.Unique)
    def name = column[String]("name")
    def * = (id, name).<>(Item.tupled, Item.unapply)
  }

  class Events(tag: Tag) extends Table[Event](tag, "Events") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc, O.Unique)
    def title = column[String]("title")
    def orgUserId = column[Int]("orgUserID")
    def members = column[Option[Seq[User]]]("members", O.SqlType("json"))
    def itemId = column[Int]("itemID")
    def description = column[Option[String]]("description")

    def * = (id, title, orgUserId, members, itemId, description).<>(Event.tupled, Event.unapply)
  }

  val items = TableQuery[Items]
  val events = TableQuery[Events]
  val users = TableQuery[Users]


  // Json Converters

  implicit val UserReads: Reads[User] = (
      (JsPath \ "id").read[UUID] and
      (JsPath \ "name").read[String] and
      (JsPath \ "position").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )(User.apply _)

  implicit val UserWrites: Writes[User] = (
      (JsPath \ "id").write[UUID] and
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
      (JsPath \ "orgUserId").read[Int] and
      (JsPath \ "members").readNullable[Seq[User]] and
      (JsPath \ "itemId").read[Int] and
      (JsPath \ "description").readNullable[String]
    )(Event.apply _)

  implicit val EventWrites: Writes[Event] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "title").write[String] and
      (JsPath \ "orgUserId").write[Int] and
      (JsPath \ "members").writeNullable[Seq[User]] and
      (JsPath \ "itemId").write[Int] and
      (JsPath \ "description").writeNullable[String]
    )(unlift(Event.unapply))

    implicit val OptSeqUserMappedColumnType: BaseColumnType[Option[Seq[User]]] =
    MappedColumnType.base[Option[Seq[User]], String](
      seq => Json.stringify(Json.toJson(seq)),
      column => Json.parse(column).asOpt[Seq[User]]
    )

}