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
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";
import AuthService from "./services/auth.service";
import Login from "./components/login.component";
import Register from "./components/register.component";
import Profile from "./components/profile.component";
import BoardUser from "./components/board-user.component";
import BoardAdmin from "./components/board-admin.component";


class App extends Component {
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

    render() {
        const { currentUser, showAdminBoard } = this.state;

        return (
            <div>
                <nav className="navbar navbar-expand navbar-dark bg-dark">
                    <div className="navbar-nav mr-auto">

                        {showAdminBoard && (
                            <li className="nav-item">
                                <Link to={"/admin"} className="nav-link">
                                    Admin Board
                                </Link>
                            </li>
                        )}

                        {currentUser && (
                            <li className="nav-item">
                                <Link to={"/user"} className="nav-link">
                                    User
                                </Link>
                            </li>
                        )}
                    </div>

                    {currentUser ? (
                        <div className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <Link to={"/profile"} className="nav-link">
                                    {currentUser.email}
                                </Link>
                            </li>
                            <li className="nav-item">
                                <a href="/" className="nav-link" onClick={this.logOut}>
                                    Выйти
                                </a>
                            </li>
                        </div>
                    ) : (
                        <div className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <Link to={"/"} className="nav-link">
                                    Войти
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


                <div className="container mt-3">
                    <Switch>
                        <Route exact path="/" component={Login} />
                        <Route exact path="/register" component={Register} />
                        <Route exact path="/profile" component={Profile} />
                        <Route path="/user" component={BoardUser} />
                        <Route path="/admin" component={BoardAdmin} />
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
                               <Alarm/>
              </Admin>
              <Home/>
              <Alarm/>

        </div>
    );
}*/


export default App;
