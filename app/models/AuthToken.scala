package models

import java.sql.Timestamp
import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

case class AuthToken(id: UUID, userID: UUID, expiry: ZonedDateTime)
case class DBAuthToken(id: UUID, userID: UUID, expiry: Timestamp)

object AuthToken {
  def toTimeStamp(date: ZonedDateTime): Timestamp = {
    Timestamp.valueOf(date.toLocalDateTime)
  }

  def toZoneDateTime(date: Timestamp): ZonedDateTime = {
    val localDateTimeNoTimeZone = date.toLocalDateTime
    localDateTimeNoTimeZone.atZone(ZoneId.systemDefault())
  }

  def toAuthToken(auth: DBAuthToken): AuthToken = AuthToken(auth.id, auth.userID, AuthToken.toZoneDateTime(auth.expiry))
  def fromAuthToken(auth: AuthToken): DBAuthToken = DBAuthToken(auth.id, auth.userID, AuthToken.toTimeStamp(auth.expiry))
}
