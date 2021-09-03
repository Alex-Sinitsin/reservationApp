package models

import com.mohiva.play.silhouette.api.Identity

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Reads, Writes}

import java.util.UUID

case class User(id: UUID, name: String, lastName: String, position: String, email: String, role: Option[String]) extends Identity

object User {
  implicit val UserReads: Reads[User] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "name").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "position").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "role").readNullable[String]
    )(User.apply _)

  implicit val UserWrites: Writes[User] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "lastName").write[String] and
      (JsPath \ "position").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "role").writeNullable[String]
    )(unlift(User.unapply))
}