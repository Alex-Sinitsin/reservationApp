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
        showAdminBoard: user.role.includes("Admin"),
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
        <div className="navbar-nav mr-auto">

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
              <li className="nav-item">
                <Link to={"/home"} className="nav-link">
                  Календарь
                </Link>
              </li>


              <li className='flex-1 d-flex flex-row justify-content-end align-items-center'>
                    <div className='header-name text-white'>
                      {currentUser.name + ' ' + currentUser.lastName}
                      <div className='header-email text-white'>
                        {currentUser.email}
                      </div>
                      <div className='header-position text-white'>
                        {currentUser.position}
                      </div>
                    </div>
              </li>


              <li className="btn btn-danger">
                <a href="/" className="nav-link" onClick={this.logOut}>
                  Выйти
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