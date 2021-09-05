import React, {Component, useEffect} from 'react'
import './Home.css'
import Header from './Header'
import 'react-big-calendar/lib/css/react-big-calendar.css';
import 'moment/locale/ru'
import MyCalendar from '../MyCalendar';
import EventForm from "./EventForm";

const Home = () => {

  useEffect(() => {
    document.title = 'Календарь';
  });


  return (
    <div>
      <header><Header/></header>

      <div className="container">
        <div className="row">
          <div className="col-md-4"><EventForm/></div>
          <div className="calendar col-md-8"><MyCalendar/></div>
        </div>
      </div>


    </div>
  )
}

export default Home;