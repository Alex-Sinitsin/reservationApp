import React, {useEffect, useState} from "react";
import Header from "./Header";
import SideMenu from "./AdminNavigation/SideMenu";

import "../BoardAdmin.css"

import {Admin, Datagrid, EmailField, fetchUtils, List, Resource, TextField} from 'react-admin';
import simpleRestProvider from 'ra-data-simple-rest';
import Cookies from "js-cookie";
import authHeader from "../services/auth-header";

const usersList = props => (
  <List {...props}>
    <Datagrid rowClick='edit'>
      <TextField source='id' />
      <TextField source='name' />
      <TextField source='postname' />
      <EmailField source='email' />
    </Datagrid>
  </List>
);

const BoardAdmin = () => {

  const [users, setUsers] = useState();

  const API_URL = "http://localhost:3000/api";
  const fetchJson = (url, options = {}) => {
    if (!options.headers) {
      options.headers = new Headers({ Accept: 'application/json' });
    }
    options.headers.set('Content-Type', 'application/json');
    options.headers.set('Csrf-Token', Cookies.get('csrfCookie'));
    options.headers.set('X-Auth-Token', authHeader());
    return fetchUtils.fetchJson(url, options);
  };
  const dataProvider = simpleRestProvider(API_URL, fetchJson);

  useEffect(() => {
    document.title = 'Панель администратора';
  }, []);

  return (
    <div>
      <Header/>
      <div className="container contentWrapper">
        <div className="row">
          <div className="col-md-10">
            <Admin dataProvider={dataProvider}>
              <Resource name="users" list={usersList} />
            </Admin>
          </div>
          <div className="col-md-2">
            <SideMenu />
          </div>
        </div>
      </div>
    </div>
  )
};

export default BoardAdmin;