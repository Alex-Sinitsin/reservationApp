import './App.css';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru';
import jsonServerProvider from 'ra-data-json-server';
import  { Admin, Resource, EditGuesser } from "react-admin";
import  { UserList, UserEdit, UserCreate }  from './users';
import  { PostList, PostEdit, PostCreate }  from './posts';
import { MyCalendar, Alarm } from './MyCalendar';
import React, { Component, useEffect, useState } from "react";
import Home from './Home';
import authProvider from './authProvider';


const dataProvider = jsonServerProvider('https://jsonplaceholder.typicode.com');

function App()   {
    return (
        <div>
              <Admin  authProvider={authProvider} dataProvider={dataProvider}>
                               <Resource name='users' list={UserList} edit={UserEdit} create={UserCreate} />
                               <Resource name='posts' list={PostList} edit={PostEdit} create={PostCreate} />
                               <Resource name='MyCalendar' list = {MyCalendar} />
                               <Alarm/>
              </Admin>
              <Home/>
              <Alarm/>

        </div>
    );
}

export default App;
