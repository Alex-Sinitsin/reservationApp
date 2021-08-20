package models.daos

import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

/**
 * Базовый трейт, содержащий настройки Slick и совмещенный с объектами доступа БД (DAOs)
 */
trait DatabaseDAO extends TableDefinitions with HasDatabaseConfigProvider[JdbcProfile]
