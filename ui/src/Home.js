import React, {Component, useEffect} from 'react'
import './Home.css'
import Header from './Header'
import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru'
import  MyCalendar  from './MyCalendar';

const Home = () =>  {

    useEffect(() => {
    document.title = 'Календарь';
    });


        return (
            <div className="col-md-12">
                     <header><Header/></header>
                    <body className="container">
                        <MyCalendar/>
                    </body>
                </div>
        )
}

export default Home;