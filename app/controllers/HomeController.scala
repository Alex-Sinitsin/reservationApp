package controllers

import models._

import javax.inject._
import play.api.mvc._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  val dao = new DatabaseDAO(dbConfigProvider)

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
//    dao.createSchemaIfNotExist
//    dao.dropSchemaIfExist

    dao.createUser(User(-1,"Лиза", "Директор", "liza@mail.ru", "pass")).onComplete {
      case Success(result) =>  println(result)
      case Failure(exception) => println(exception)
    }

//    dao.getAllUsers.foreach(println)

    Ok(views.html.index())
  }
}