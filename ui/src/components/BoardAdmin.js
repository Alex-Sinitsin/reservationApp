import React, {useEffect, useState} from "react";
import Header from "./Header";
import SideMenu from "./AdminNavigation/SideMenu";

import "../BoardAdmin.css"

import {
  Admin,
  Datagrid,
  EmailField,
  fetchUtils,
  List,
  Resource,
  TextField,
  FunctionField,
  Show,
  SimpleShowLayout
} from 'react-admin';

import polyglotI18nProvider from 'ra-i18n-polyglot';
import russianMessages from 'ra-language-russian';
import UserIcon from '@material-ui/icons/Group';

import jsonServerProvider from 'ra-data-json-server';
import Cookies from "js-cookie";
import authHeader from "../services/auth-header";

const usersList = props => (
  <List {...props}>
      <Datagrid rowClick='edit'>
        <TextField source='name' label="Имя" sortable={false}/>
        <TextField source='lastName' label="Фамилия" sortable={false}/>
        <TextField source='position' label="Должность" sortable={false}/>
        <EmailField source='email' label="Почта" sortable={false}/>
        <FunctionField
          label="Роль"
          render={record => record.role == "User" ? "Пользователь" : "Администратор"}/>
      </Datagrid>
  </List>
);

const BoardAdmin = () => {

  const [users, setUsers] = useState();

  const API_URL = "http://localhost:3000/api";
  const fetchJson = (url, options = {}) => {
    if (!options.headers) {
      options.headers = new Headers({Accept: 'application/json'});
    }
    options.headers.set('Content-Type', 'application/json');
    options.headers.set('Csrf-Token', Cookies.get('csrfCookie'));
    options.headers.set('X-Auth-Token', authHeader());
    return fetchUtils.fetchJson(url, options);
  };

  const dataProvider = jsonServerProvider(API_URL, fetchJson);
  const i18nProvider = polyglotI18nProvider(() => russianMessages, 'ru');

  useEffect(() => {
    document.title = 'Панель администратора';
  }, []);

  return (
    <Admin dataProvider={dataProvider} i18nProvider={i18nProvider} title="Список пользователей">
      <Resource name="users" label="Пользователи" list={usersList} icon={UserIcon}/>
    </Admin>
  )
};

export default BoardAdmin;