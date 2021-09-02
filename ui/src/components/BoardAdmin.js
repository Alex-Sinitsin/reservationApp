import React, {useEffect, useState} from "react";
import Header from "./Header";
import SideMenu from "./AdminNavigation/SideMenu";

import "../BoardAdmin.css"

import {Admin, Resource} from 'react-admin';
import UserIcon from '@material-ui/icons/Group';

const BoardAdmin = () => {

  const [users, setUsers] = useState()

  useEffect(() => {
    document.title = 'Панель администратора';
  }, []);

  return (
    <div>
      <Header/>
      <div className="container contentWrapper">
        <div className="row">
          <div className="col-md-10">
            Контент
          </div>
          <div className="col-md-2">
            <SideMenu />
          </div>
        </div>
      </div>
      {/*<div className="row">
        <div className="card card-container col-md-4">
          <div className="d-flex flex-column bd-highlight mb-3">
            <div className="p-2 bd-highlight">
              <Link to={"/register"} className="link">
                Регистрация пользователей
              </Link>
            </div>
          </div>
        </div>
      </div>*/}
    </div>
  )
};

export default BoardAdmin;