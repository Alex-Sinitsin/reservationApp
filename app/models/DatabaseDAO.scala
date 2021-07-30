package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class DatabaseDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {
  // Скорее всего будет частично переделываться
  //TODO: Разобраться с возвратом ошибок и попробовать что-ниюудь вывести в шаблон вида
  // TODO: Сделать различные проверки при обновлении и удалении пользователей

  def createSchemaIfNotExist: Future[Unit] = {
    dbConfig.db.run(allSchemas.createIfNotExists).andThen {
      case Success(v) => println("Схема базы данных успешно создана!")
      case Failure(ex) => println(ex)
    }
  }

  def dropSchemaIfExist: Future[Unit] = {
    dbConfig.db.run(allSchemas.dropIfExists).andThen{
      case Success(_) =>  println("Схема базы данных успешно удалена!")
      case Failure(v) => println(v)
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

  def updateUser(user: User) = ???

  def deleteUser(id: Int): Future[Int] = {
    dbConfig.db.run(users.filter(_.id === id).delete).andThen{case Success(_) => println("User was successfully deleted")}
  }

  def getAllUsers: Future[Seq[User]] = {
    dbConfig.db.run(users.result)
  }
}