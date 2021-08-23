package models.services

import models.{AuthToken, DBAuthToken}

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Handles actions to auth tokens.
 */
trait AuthTokenService {

  /**
   * Creates a new auth token and saves it in the backing store.
   *
   * @param userID The user ID for which the token should be created.
   * @param expiry The duration a token expires.
   * @return The saved auth token.
   */
  def create(userID: UUID, expiry: FiniteDuration = 5 minutes): Future[DBAuthToken]

  /**
   * Validates a token ID.
   *
   * @param id The token ID to validate.
   * @return The token if it's valid, None otherwise.
   */
  def validate(id: UUID): Future[Option[DBAuthToken]]

  /**
   * Cleans expired tokens.
   *
   * @return The list of deleted tokens.
   */
  def clean: Future[Seq[DBAuthToken]]
}
