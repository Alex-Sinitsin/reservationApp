import React, {Component, useEffect} from 'react'
import './Home.css'
import Header from './Header'
import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru'
import  MyCalendar  from '../MyCalendar';
import Event from "./Event";

const Home = () =>  {

    useEffect(() => {
    document.title = 'Календарь';
    });


        return (
            <div>
                     <header><Header/></header>

                <div className="row">
                    <div className="col-md-4"><Event/></div>
                    <div className="col-md-7"><MyCalendar/></div>
                    <div className="col-md-1"></div>
                </div>






            </div>
        )
}

export default Home;