import React, { useState, useEffect } from "react";

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
    <div>
      <nav className="navbar navbar-expand navbar-dark bg-primary">
        <div className="navbar-nav">
          <a className="navbar-brand ms-5">Бронирование</a>

          {showAdminBoard && (
              <li className="nav-item">
                <Link to={"/admin"} className="nav-link">
                  Панель администратора
                </Link>
              </li>
          )}

        </div>



        {currentUser && (

            <div className="navbar-nav ms-auto">
               <li className="navbar-item">
                 <img
                     src="/avatar.png"
                     alt="profile-img"
                     className="profile-img-card me-3"
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
      </nav>
      </div>
);
};

export default Header;