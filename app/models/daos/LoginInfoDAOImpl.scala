package models.daos

import com.mohiva.play.silhouette.api.LoginInfo

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import models.User

import java.util.UUID
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.Effect
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import slick.sql.FixedSqlAction

class LoginInfoDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends LoginInfoDAO with DatabaseDAO {

  /**
   * Сохраняет информацию для входа пользователя
   *
   * @param userID ID пользователя
   * @param loginInfo Данные для входа
   *  @return
   */
  override def saveUserLoginInfo(userID: UUID, loginInfo: LoginInfo): Future[Unit] = {
    val dbLoginInfo = DBLoginInfo(None, loginInfo.providerID, loginInfo.providerKey)
    val loginInfoAction = {
      val retrieveLoginInfo = loginInfos.filter(
        info => info.providerID === loginInfo.providerID &&
          info.providerKey === loginInfo.providerKey).result.headOption
      val insertLoginInfo = loginInfos.returning(loginInfos.map(_.id)).
        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo
      for {
        loginInfoOption <- retrieveLoginInfo
        dbLoginInfo <- loginInfoOption.map(DBIO.successful).getOrElse(insertLoginInfo)
      } yield dbLoginInfo
    }

    val actions = (for {
      dbLoginInfo <- loginInfoAction
      userLoginInfo = DBUserLoginInfo(userID, dbLoginInfo.id.get)
      exists <- existsUserLoginInfo(userLoginInfo)
      _ <- if (exists) DBIO.successful(()) else userLoginInfos += userLoginInfo
    } yield ()).transactionally

    db.run(actions)
  }

  /**
   * Проверяет наличие данных для входа у пользователя
   *
   * @param uli Пользовательские данные для входа
   * @return
   */
   def existsUserLoginInfo(uli: DBUserLoginInfo): FixedSqlAction[Boolean, PostgresProfile.api.NoStream, Effect.Read] = {
    userLoginInfos.filter(e => e.loginInfoId === uli.loginInfoId && e.userID === uli.userID).exists.result
  }

  /**
   * Ищет пару пользователя и его данные для входа по ID пользователя и ID провайдера
   *
   * @param userId     ID пользователя
   * @param providerId ID провайдера аутентификации
   * @return Some(User, LoginInfo), если пользователь с данным ID имеет метод аутентификации для данного провайдера по ID провайдера, иначе None
   */
  def find(userId: UUID, providerId: String): Future[Option[(User, LoginInfo)]] = {
    val action = for {
      ((_, li), u) <- userLoginInfos.filter(_.userID === userId)
        .join(loginInfos).on(_.loginInfoId === _.id)
        .join(users).on(_._1.userID === _.id)

      if li.providerID === providerId
    } yield (u, li)

    db.run(action.result.headOption).map(_.map{case (u, li) => (DBUser.toUser(u), DBLoginInfo.toLoginInfo(li))})
  }

  /**
   * Получает список провайдеров аутентификации пользователей
   *
   * @param email Электронная почта пользователя
   *  @return
   */
  override def getAuthenticationProviders(email: String): Future[Seq[String]] = {
    val action = for {
      ((_, _), li) <- users.filter(_.email === email)
        .join(userLoginInfos).on(_.id === _.userID)
        .join(loginInfos).on(_._2.loginInfoId === _.id)
    } yield li.providerID

    db.run(action.result)
  }
}
