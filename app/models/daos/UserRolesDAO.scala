package models.daos

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class UserRolesDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends TableDefinitions with HasDatabaseConfigProvider[JdbcProfile] {
  def getUserRole: DBIOAction[Int, NoStream, Effect.Read] = userRoles.filter(_.name === "User").map(_.id).result.headOption.map{
    case Some(id) => id
    case None => throw new RuntimeException("Роль пользователя отсутсвует в БД")
  }
}
