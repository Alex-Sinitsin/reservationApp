package models

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Reads, Writes}

case class Item(id: Long, name: String)

object Item {
  implicit val ItemReads: Reads[Item] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "name").read[String]
    )(Item.apply _)

  implicit val ItemWrites: Writes[Item] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "name").write[String]
    )(unlift(Item.unapply))
}
