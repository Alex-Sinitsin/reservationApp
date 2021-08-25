/*import './App.css';
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


*/

import "bootstrap/dist/css/bootstrap.min.css";
import React, { Component } from "react";
import { Switch, Route, Link } from "react-router-dom";
import "./App.css";
import AuthService from "./services/auth.service";
import Login from "./components/login.component";
import Register from "./components/register.component";
import Profile from "./components/profile.component";
import BoardUser from "./components/board-user.component";
import BoardAdmin from "./components/board-admin.component";
import Home from './Home';
import PageNotFound from './PageNotFound';


import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru';
import  { Admin, Resource} from "react-admin";
import  { UserList, UserEdit, UserCreate }  from './users';
import  { PostList, PostEdit, PostCreate }  from './posts';
import { MyCalendar, Alarm } from './MyCalendar';
import Header from "./Header";



class App extends Component {

    render() {

        return (

            <div>
                <Header/>
                <div className="container mt-3">
                    <Switch>
                        <Route exact path="/" component={Login} />
                        <Route exact path="/register" component={Register} />
                        <Route exact path="/profile" component={Profile} />
                        <Route path="/user" component={BoardUser} />
                        <Route path="/admin" component={BoardAdmin} />
                        <Route path="/home" component={Home} />


                        <Route component={PageNotFound} />

                    </Switch>
                </div>
            </div>
        );
    }
}











/*const dataProvider = jsonServerProvider('https://jsonplaceholder.typicode.com');

function App()   {

    return (
        <div>
              <Admin  authProvider={authProvider} dataProvider={dataProvider}>
                               <Resource name='users' list={UserList} edit={UserEdit} create={UserCreate} />
                               <Resource name='posts' list={PostList} edit={PostEdit} create={PostCreate} />
                               <Resource name='MyCalendar' list = {MyCalendar} />
              </Admin>
              <Home/>

        </div>
    );
}
}*/


export default App;
