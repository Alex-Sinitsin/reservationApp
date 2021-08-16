package models

import com.mohiva.play.silhouette.api.Identity

import java.util.UUID

case class User(id: UUID, name: String, lastName: String, position: String, email: String, role: Option[String]) extends Identity
