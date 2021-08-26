import '../App.css';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru';
import jsonServerProvider from 'ra-data-json-server';
import  { Admin, Resource} from "react-admin";
import  { UserList, UserEdit, UserCreate }  from '../users';
import  { PostList, PostEdit, PostCreate }  from '../posts';
import MyCalendar from '../MyCalendar';
import React, {Component} from 'react';

import authProvider from '../authProvider';

const dataProvider = jsonServerProvider('https://jsonplaceholder.typicode.com');

export default class BoardAdmin extends Component {

    componentDidMount() {

        document.title = 'Панель администратора';
    }


    render() {

        return

    }
}