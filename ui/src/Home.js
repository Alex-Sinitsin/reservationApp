import React, { Component } from 'react'
import './Home.css'
import Header from './Header'
import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru'
import { MyCalendar } from './MyCalendar';

const TITLE = 'Домашняя'
const userName = 'Иванов Иван Иванович'
const email = 'ivan@gmail.ru'
const post = 'Сотрудник'
const className = 'Home-Header'

export default class Home extends Component {
  render () {
    return (
      <div className='Home'>
        <Header
          title={TITLE}
          userName={userName}
          email={email}
          post={post}
          className={className}
        />
        <div>

        <MyCalendar />

        </div>
      </div>
    )
  }
}