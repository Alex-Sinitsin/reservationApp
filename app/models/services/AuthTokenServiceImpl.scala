package models.services

import models.{AuthToken, DBAuthToken}

import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject
import models.daos.AuthTokenDAO

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Handles actions to auth tokens.
 *
 * @param authTokenDAO The auth token DAO implementation.
 * @param ex           The execution context.
 */
class AuthTokenServiceImpl @Inject()(authTokenDAO: AuthTokenDAO)(implicit ex: ExecutionContext) extends AuthTokenService {

  /**
   * Creates a new auth token and saves it in the backing store.
   *
   * @param userID The user ID for which the token should be created.
   * @param expiry The duration a token expires.
   * @return The saved auth token.
   */
  def create(userID: UUID, expiry: FiniteDuration = 3 hours): Future[DBAuthToken] = {
    val token = DBAuthToken(UUID.randomUUID(), userID, AuthToken.toTimeStamp(ZonedDateTime.now().plusSeconds(expiry.toSeconds)))
    authTokenDAO.save(token).map(_ => token)
  }

  /**
   * Validates a token ID.
   *
   * @param id The token ID to validate.
   * @return The token if it's valid, None otherwise.
   */
  def validate(id: UUID): Future[Option[DBAuthToken]] = authTokenDAO.find(id)

  /**
   * Cleans expired tokens.
   *
   * @return The list of deleted tokens.
   */
  def clean: Future[Seq[DBAuthToken]] = authTokenDAO.findExpired().flatMap { tokens =>
    Future.sequence(tokens.map { token =>
      authTokenDAO.remove(token.id).map(_ => token)
    })
  }
}
