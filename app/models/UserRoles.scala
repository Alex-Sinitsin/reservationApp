package models

case class UserRoles(id: Int)

object UserRoles {
  def toHumanReadable(id: Int): String = {
    val roleName = id match {
      case 1 => "User"
      case 2 => "Admin"
      case _ => "N/a"
    }
    roleName
  }

  def toDBReadable(name: String): Int = {
    val roleId = name match {
      case "User" => 1
      case "Admin" => 2
      case _ => 0
    }
    roleId
  }
}
