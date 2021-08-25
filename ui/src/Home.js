import React, { Component } from 'react'
import './Home.css'
import Header from './Header'
import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru'
import  MyCalendar  from './MyCalendar';

export default class Home extends Component {
    componentDidMount() {
        document.title = 'Календарь';
    }

    render () {
        return (
            <div className='Home'>
                    <MyCalendar />
                </div>
        )
    }
}