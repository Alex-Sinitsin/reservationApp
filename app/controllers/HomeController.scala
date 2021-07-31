package controllers

import models._

import javax.inject._
import play.api.mvc._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsArray, JsError, JsSuccess, Json, Reads, Writes}
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

  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(a, path) => f(a)
        case e @ JsError(_) => Future.successful(Redirect(routes.HomeController.index()))
      }
    }.getOrElse(Future.successful(Redirect(routes.HomeController.index())))
  }

  def index(): Request[AnyContent] = Action async   { implicit request: Request[AnyContent] =>
    //    dao.createSchemaIfNotExist
    //    dao.dropSchemaIfExist

    //    dao.createUser(User(-1,"Лиза", "Директор", "liza@mail.ru", "pass")).onComplete {
    //      case Success(result) =>  println(result)
    //      case Failure(exception) => println(exception)
    //    }

    //    dao.updateUser(User(1,"Алексей", "Разработчик", "alex@mail.ru", "pass1word")).onComplete {
    //      case Success(result) =>  println(result)
    //      case Failure(exception) => println(exception)
    //    }


//    Ok(views.html.index(Json.toJson(users)))


//    withJsonBody[List[User]]{
//    val users: Future[List[User]] = dao.getAllUsers.map(usl => Ok(Json.toJson(usl)(dao.ListUserWrites)))
//    }(dao.UserReads)


//    withJsonBody[List[User]] {
//
//    }(dao.UserReads)
  }
}