import './App.css';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru';
import jsonServerProvider from 'ra-data-json-server';
import  { Admin, Resource } from "react-admin";
import  { UserList, UserEdit, UserCreate }  from './users';
import  { PostList, PostEdit, PostCreate }  from './posts';
import MyCalendar from './MyCalendar';
import React from 'react';
import Home from './Home';
import authProvider from './authProvider';
import PostIcon from '@material-ui/icons/Book';
import UserIcon from '@material-ui/icons/Group';

const dataProvider = jsonServerProvider('https://jsonplaceholder.typicode.com');

function App()   {

    return (
        <div>
              <Admin  authProvider={authProvider} dataProvider={dataProvider}>
                               <Resource name='users' list={UserList} edit={UserEdit} create={UserCreate} icon={UserIcon}/>
                               <Resource name='posts' list={PostList} edit={PostEdit} create={PostCreate} icon={PostIcon}/>
                               <Resource name='MyCalendar' list = {MyCalendar} />
              </Admin>
              <Home/>

        </div>
    );
}

export default App;
