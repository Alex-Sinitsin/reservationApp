package models.daos

import com.google.inject.Inject
import models.EventMember
import play.api.db.slick.DatabaseConfigProvider

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._

import scala.collection.mutable.ArrayBuffer

class EventMemberDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends DatabaseDAO {

  /**
   * Добавление участников события
   *
   * @param eventId ID события
   * @param members Список ID участников для добавления
   * @return
   */
  def create(eventId: Long, members: List[UUID]): Future[Option[Int]] = {
    val membersList: ArrayBuffer[EventMember] = ArrayBuffer.empty

    members.map{userId => membersList += EventMember(eventId, userId)}

    db.run(eventMembers ++= membersList)
  }

  /**
   * Получение списка участников
   *
   * @return
   */
  def getAllMembers: Future[Seq[EventMember]] =
    db.run(eventMembers.result)

  /**
   * Получение списка участников конкретного события по его ID
   *
   * @param eventId ID события
   * @return
   */
  def getMembersByEventID(eventId: Long): Future[Seq[EventMember]] =
    db.run(eventMembers.filter(_.eventId === eventId).result)
}
