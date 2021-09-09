import React, {useEffect} from 'react'
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
      <Header />
      <div className="container">
        <div className="row">
          <div className="col-md-3 eventForm"><EventForm/></div>
          <div className="calendar col-md-9 mb-5">
            <ChoiceForm/>
            <MyCalendar/>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Home;