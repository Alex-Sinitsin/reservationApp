package controllers

import com.mohiva.play.silhouette.api.Silhouette
import models.User
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.JWTEnvironment

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

/**
 *  Контроллер главной страницы
 *
 * @param silhouette Стек `Silhouette`
 * @param controllerComponents Экземпляр трейта `ControllerComponents`
 * @param ex Контекст выполнения
 */
class IndexController @Inject() (silhouette: Silhouette[JWTEnvironment], controllerComponents: ControllerComponents)
                                (implicit ex: ExecutionContext) extends AbstractController(controllerComponents) {

  def index: Action[AnyContent] = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    val listUsers: List[User] = List(
      User(UUID.fromString("8c6d9fba-18fc-4ce7-afe2-14f84a0fa0e4"), "Alex", "Sin", "Dev","alex@example.com", Some("Admin")),
      User(UUID.randomUUID(), "Ega", "Kov", "Dev","ega@example.com", Some("User")),
      User(UUID.randomUUID(), "Roma", "Smile", "Dev","roma@example.com", Some("User"))
    )

    val h = listUsers.span(user => user.id == UUID.fromString("8c6d9fba-18fc-4ce7-afe2-14f84a0fa0e4"))._2

    Ok(Json.obj("listUsers" -> Json.toJson(listUsers), "MembersWithDelUser" -> Json.toJson(h)))
  }
}
