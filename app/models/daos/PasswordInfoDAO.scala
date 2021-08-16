package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

import slick.jdbc.PostgresProfile.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.sql.FixedSqlAction
import slick.dbio.Effect

/**
 * DAO для сохранения информации о паролях
 *
 * @param dbConfigProvider Провайдер конфигурации базы данных
 * @param ec Контекст выполнения
 * @param classTag Экземпляр трейта `ClassTag`
 */
class PasswordInfoDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext, val classTag: ClassTag[PasswordInfo])
  extends DelegableAuthInfoDAO[PasswordInfo] with DatabaseDAO {

  protected def passwordInfoQuery(loginInfo: LoginInfo): Query[PasswordInfos, DBPasswordInfo, Seq] = for {
    dbLoginInfo <- loginInfoQuery(loginInfo)
    dbPasswordInfo <- passwordInfos if dbPasswordInfo.loginInfoId === dbLoginInfo.id
  } yield dbPasswordInfo

  protected def passwordInfoSubQuery(loginInfo: LoginInfo): Query[PasswordInfos, DBPasswordInfo, Seq] =
    passwordInfos.filter(_.loginInfoId in loginInfoQuery(loginInfo).map(_.id))

  protected def addAction(loginInfo: LoginInfo, authInfo: PasswordInfo): DBIOAction[Int, NoStream, Effect.Read with Effect.Write with Effect.Transactional] =
    loginInfoQuery(loginInfo).result.head.flatMap { dbLoginInfo =>
      passwordInfos +=
        DBPasswordInfo(authInfo.hasher, authInfo.password, authInfo.salt, dbLoginInfo.id.get)
    }.transactionally

  protected def updateAction(loginInfo: LoginInfo, authInfo: PasswordInfo): FixedSqlAction[Int, NoStream, Effect.Write] =
    passwordInfoSubQuery(loginInfo).
      map(dbPasswordInfo => (dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt)).
      update((authInfo.hasher, authInfo.password, authInfo.salt))

  /**
   * Находит информацию об авторизации, которая связана с определенными данными для входа
   *
   * @param loginInfo Связанная информация для входа.
   * @return Полученная информация для авторизации или None, если данных, связанных с такой информацией для входа, нет.
   */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    db.run(passwordInfoQuery(loginInfo).result.headOption).map { dbPasswordInfoOption =>
      dbPasswordInfoOption.map(dbPasswordInfo =>
        PasswordInfo(dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))
    }
  }

  /**
   * Добавляет данные об авторизации для конкретной информации для входа
   *
   * @param loginInfo Информация для входа в систему, для которой должна быть добавлена информация для авторизации
   * @param authInfo  Информация об входа, для которой необходимо добавить информацию об авторизации
   * @return Информацию для авторизации
   */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  /**
   * Обновляет информацию об авторизации для данной информации для входа
   *
   * @param loginInfo Информация для входа в систему, для которой должна быть обновлена информация об авторизации
   * @param authInfo  Информация об входа, для которой необходимо обновить информацию об авторизации
   * @return Обновленную информацию об авторизации
   */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  /**
   * Сохраняет информацию об аутентификации для данной информации для входа
   *
   * Этот метод либо добавляет информацию об аутентификации, если она не существует, либо обновляет информацию об аутентификации, если она уже существует.
   *
   * @param loginInfo Информация для входа в систему, для которой должна быть сохранена информация об авторизации
   * @param authInfo  Информация об входа, для которой необходимо сохранить информацию об авторизации
   * @return Сохраненная информация об авторизации
   */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val query = loginInfoQuery(loginInfo).joinLeft(passwordInfos).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (dbLoginInfo, Some(dbPasswordInfo)) => updateAction(loginInfo, authInfo)
      case (dbLoginInfo, None) => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }

  /**
   * Удаляет информацию об авторизации для данной информации для входа
   *
   * @param loginInfo Информация для входа, для которой необходимо удалить информацию об авторизации
   * @return Объект Future
   */
  def remove(loginInfo: LoginInfo): Future[Unit] =
    db.run(passwordInfoSubQuery(loginInfo).delete).map(_ => ())
}
