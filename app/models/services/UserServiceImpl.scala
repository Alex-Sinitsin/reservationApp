package models.services

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.{LoginInfoDAO, UserDAO}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Обрабатывает действия для пользователей
 *
 * @param userDAO Объект доступа к Бд для работы с пользователями
 * @param loginInfoDAO Объект доступа к Бд для работы с данными для входа
 * @param ec Контекст выполнения
 */
class UserServiceImpl @Inject()(userDAO: UserDAO,
                                loginInfoDAO: LoginInfoDAO)
                               (implicit ec: ExecutionContext) extends UserService {


  /**
   * Извлекает список пользователей
   *
   * @return
   */
  override def retrieveAll: Future[Seq[User]] = userDAO.getAll

  /**
   * Извлекает информацию о пользователе, который удовлетворяет условию.
   *
   * @param loginInfo Информация для входа
   *  @return Полученный пользователь или None, если ни один пользователь не может быть получен для данной информации для входа
   */
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.findByLoginInfo(loginInfo)

  /**
   * Извлекает пару информации о пользователе и информации для входа по идентификатору пользователя и идентификатору поставщика информации для входа
   *
   * @param id         ID пользователя
   * @param providerID ID провайдера
   *  @return Полученный пользователь или None, если не удалось получить пользователя для данного идентификатора
   */
  override def retrieveUserLoginInfo(id: UUID, providerID: String): Future[Option[(User, LoginInfo)]] = {
    loginInfoDAO.find(id, providerID)
  }

  /**
   * Меняет роль пользователя
   *
   * @param userId ID пользователя
   * @param role   Новая роль, которую необходимо присвоить пользователю
   *  @return
   */
  override def changeUserRole(userId: UUID, role: String): Future[Boolean] = {
    userDAO.updateUserRole(userId, role)
  }

  /**
   * Создает или обновляет информацию о пользователе
   *
   * Если пользователь существует для данной информации для входа или электронной почты, обновляет пользователя, в противном случае создает нового пользователя с заданными данными
   *
   * @param loginInfo Данные для входа
   * @param email     Электронная почта пользователя
   * @param name      Имя пользователя
   * @param lastName  Фамилия пользователя
   * @param position  Должность пользователя в компании
   *  @return
   */
  override def createOrUpdate(loginInfo: LoginInfo, email: String, name: String, lastName: String, position: String): Future[User] = {

    Future.sequence(Seq(userDAO.findByLoginInfo(loginInfo),
      userDAO.findByEmail(email))).flatMap { users =>
      users.flatten.headOption match {
        case Some(user) =>
          userDAO.save(user.copy(
            name = name,
            lastName = lastName,
            position = position,
            email = email
          ))
        case None =>
          userDAO.save(User(
            id = UUID.randomUUID(),
            name = name,
            lastName = lastName,
            position = position,
            email = email,
            role = None
          ))
      }
    }
  }

  /**
   * Удаляет данные пользователя
   *
   * @param userID ID пользователя
   * @param email Email пользователя
   * @return
   */
   override def delete(userID: UUID, email: String): Future[Boolean] = {
     loginInfoDAO.deleteLoginInfo(email)
     userDAO.delete(userID)
   }
}
