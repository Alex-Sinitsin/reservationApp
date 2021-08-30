package forms

import models.Item
import play.api.data.Form
import play.api.data.Forms._

object ItemForm {

  /**
   * Форма Play Framework.
   */
  val form: Form[Item] = Form(
    mapping(
      "id" -> ignored(-1L),
      "name" -> nonEmptyText
    )(Item.apply)(Item.unapply)
  )
}
