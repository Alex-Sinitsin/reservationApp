package controllers

import io.circe.{Decoder, Encoder, parser}
import io.circe.syntax.EncoderOps
import models._

import javax.inject._
import play.api.mvc._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsArray, JsError, JsSuccess, Json, Reads, Writes}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import io.circe.syntax._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  val dao = new DatabaseDAO(dbConfigProvider)

  // Circe Json Encoder и Decoder
  implicit val UserEncoder: Encoder[User] =
    Encoder.forProduct5("id", "name", "position", "email", "password")(User.unapply(_).get)
  implicit val UserDecoder: Decoder[User] =
    Decoder.forProduct5("id", "name", "position", "email", "password")(User.apply)

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    //    dao.createSchemaIfNotExist
    //    dao.dropSchemaIfExist

//    dao.createUser(User(-1, "Елизавета", "HR", "liza@pochta.ru", "password"))
    Ok(views.html.index())
  }

  def users(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    for {
      users <- dao.getAllUsers
    } yield Ok(users.asJson.toString)
  }
}