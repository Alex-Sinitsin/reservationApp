import React, { Component } from 'react'


import './Header.css'
import {Link} from "react-router-dom";
import AuthService from "./services/auth.service";



export default class Header extends Component {

  constructor(props) {
    super(props);
    this.logOut = this.logOut.bind(this);

    this.state = {
      showAdminBoard: false,
      currentUser: undefined,
    };
  }

  componentDidMount() {
    const user = AuthService.getCurrentUser();

    if (user) {
      this.setState({
        currentUser: user,
        showAdminBoard: user.userInfo.role.includes("Admin"),
      });
    }
  }

  logOut() {
    AuthService.logout();
  }


  render () {

    const {
      currentUser,
      showAdminBoard,
    } = this.state;

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



        {currentUser ? (

            <div className="navbar-nav ms-auto">

               <li className="navbar-item">
                 <img
                     src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
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
                <a href="/" className="btn btn-primary btn-block border-white" onClick={this.logOut}>
                  Выход
                </a>
              </li>
            </div>



        ) : (


            <div className="navbar-nav ms-auto">
              <li className="nav-item">
                <Link to={"/"} className="nav-link">
                  Вход
                </Link>
              </li>

              <li className="nav-item">
                <Link to={"/register"} className="nav-link">
                  Регистрация
                </Link>
              </li>
            </div>
        )}
      </nav>
      </div>
);
  }
}