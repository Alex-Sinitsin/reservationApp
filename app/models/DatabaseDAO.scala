package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * Объект для работы с базой данных
 * @param dbConfigProvider - Провайдер конфигурации базы данных
 * @param ec - Исполняемы контекст
 */
@Singleton
class DatabaseDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  // TODO: Сделать различные проверки при обновлении и удалении пользователей

  def createSchemaIfNotExist: Future[Unit] = {
    dbConfig.db.run(allSchemas.createIfNotExists).andThen {
      case Success(_) => println("Схема базы данных успешно создана!")
      case Failure(ex) => println(ex)
    }
  }

  def dropSchemaIfExist: Future[Unit] = {
    dbConfig.db.run(allSchemas.dropIfExists).andThen{
      case Success(_) =>  println("Схема базы данных успешно удалена!")
      case Failure(ex) => println(ex)
    }
  }

  //UserDAO Implement

  def createUser(userData: User): Future[String] = {
    dbConfig.db.run(users.filter(userRow => userRow.email === userData.email).result).flatMap{userMatch =>
      if(userMatch.nonEmpty) Future.failed(new Exception("Пользователь с таким Email уже существует!"))
      else {
        dbConfig.db.run(users += userData).flatMap(_ => Future.successful("Новый пользователь успешно добавлен!"))
      }
    }
  }

  def updateUser(userRowData: User): Future[String] = {
    //TODO: Сделать шифрование паролей, когда будет подключена библиотека Silhouette (bcrypt)
    val updateUserById = users.filter(_.id === userRowData.id)
      .map(user => (user.name, user.position, user.email, user.password))
      .update((userRowData.name, userRowData.position, userRowData.email, userRowData.password ))
    db.run(updateUserById).flatMap { counter =>
      if(counter > 0) Future.successful("Данные пользователя успешно обновлены!")
      else Future.failed(new Exception("Произошла ошибка при обновлении данных пользователя!"))
    }
  }

  def deleteUser(id: Int): Future[String] = {
    dbConfig.db.run(users.filter(_.id === id).delete).flatMap {counter =>
      if(counter > 0) Future.successful("Данные пользователя успешно удалены!")
      else Future.failed(new Exception("Произошла ошибка при удалении данных пользователя!"))}
  }

  def getAllUsers: Future[List[User]] = {
    dbConfig.db.run(users.result).map(_.toList)
  }
}