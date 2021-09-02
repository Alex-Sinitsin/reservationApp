import React from "react";
import "./SideMenu.css"

const SideMenu = () => {
  return (
    <div>
      <nav className="nav nav-pills flex-column">
        <a className="nav-link active" aria-current="page" href="#">Пользователи</a>
        <a className="nav-link" href="#">Link</a>
        <a className="nav-link" href="#">Link</a>
        <a className="nav-link disabled" href="#" tabIndex="-1" aria-disabled="true">Disabled</a>
      </nav>
    </div>
  )
}

export default SideMenu