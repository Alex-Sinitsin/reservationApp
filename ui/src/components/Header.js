import React, {useState, useEffect} from "react";

import './Header.css'
import {Link} from "react-router-dom";
import AuthService from "../services/auth.service";


const Header = () => {

  const [showAdminBoard, setShowAdminBoard] = useState(false);
  const [currentUser, setCurrentUser] = useState(undefined);

  useEffect(() => {
    const user = AuthService.getCurrentUser();

    if (user) {
      setCurrentUser(user);
      setShowAdminBoard(user.userInfo.role.includes("Admin"));
    }
  }, []);

  const logout = () => {
    AuthService.logout();
  };


  return (
    <header>
      <nav className="navbar navbar-expand navbar-dark bg-primary">
        <div className="container-fluid">
          <div className="navbar-nav">
            <a className="navbar-brand">Бронирование</a>

            {currentUser && (
                <li className="nav-item">
                  <Link to={"/home"} className="nav-link">
                    Календарь
                  </Link>
                </li>
            )}

            {currentUser && (
              <li className="nav-item">
                  <Link to={"/profile"} className="nav-link">
                    Профиль
                  </Link>
              </li>
            )}

            {showAdminBoard && (
                <li className="nav-item">
                  <Link to={"/admin"} className="nav-link">
                    Панель администратора
                  </Link>
                </li>
            )}

          </div>

          {currentUser && (

            <div className="navbar-nav">
              <li className="navbar-item">
                <img
                  src="/avatar.png"
                  alt="profile-img"
                  className="profile-img-card"
                />
              </li>

              <li className='flex-1 d-flex flex-row justify-content-end align-items-center me-4'>
                <div className='nav-item header-text text-white'>
                  {currentUser.userInfo.name + ' ' + currentUser.userInfo.lastName}
                  <div className='header-text text-white'>
                    {currentUser.userInfo.position}
                  </div>
                </div>
              </li>

              <li className="nav-item me-3">
                <a href="/" className="btn btn-primary btn-block border-white" onClick={logout}>
                  Выход
                </a>
              </li>
            </div>
          )}
        </div>
      </nav>
    </header>
  );
};

export default Header;