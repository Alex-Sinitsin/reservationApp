import React, {Component, useEffect} from 'react'
import './Home.css'
import Header from './Header'
import MyCalendar from '../MyCalendar';
import EventForm from "./EventForm";
import ChoiceForm from "./ChoiceForm";

const Home = () => {

  useEffect(() => {
    document.title = 'Календарь';
  });


  return (
    <div>
      <header><Header/></header>

      <div className="container">
        <div className="row">
          <div className="col-md-3 eventForm"><EventForm/></div>
          <div className="calendar col-md-9"><MyCalendar/></div>
            <div className="w-100"></div>
            <div className="col"></div>
            <div className="col-md-1 itemForm"><ChoiceForm/></div>
        </div>
      </div>


    </div>
  )
}

export default Home;