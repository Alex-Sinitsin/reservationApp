import React, {useEffect, useState} from "react";
import {Admin, fetchUtils, Resource} from 'react-admin';
import jsonServerProvider from 'ra-data-json-server';
import Cookies from "js-cookie";
import authHeader from "../services/auth-header";
import polyglotI18nProvider from 'ra-i18n-polyglot';
import russianMessages from 'ra-language-russian';

import "./BoardAdmin.css"

import usersList from "./BoardAdmin/Users/usersList"
import usersCreate from "./BoardAdmin/Users/usersCreate"
import itemsCreate from "./BoardAdmin/Items/itemsCreate"
import itemsEdit from "./BoardAdmin/Items/itemsEdit"
import itemsList from "./BoardAdmin/Items/itemsList"

import UserIcon from '@material-ui/icons/GroupOutlined';
import ItemIcon from '@material-ui/icons/CategoryOutlined';


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
      <Resource name="users" options={{ label: 'Пользователи' }} list={usersList} create={usersCreate} icon={UserIcon}/>
      <Resource name="items" options={{ label: 'Объекты' }} list={itemsList} create={itemsCreate} edit={itemsEdit} icon={ItemIcon}/>
    </Admin>
  )
};

export default BoardAdmin;