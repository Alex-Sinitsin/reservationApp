import React, { useState, Component  } from 'react';
import ReactDOM from "react-dom";
import './App.css';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { Calendar, momentLocalizer } from 'react-big-calendar'
import moment from 'moment'
import 'moment/locale/ru'
import { Alert, useAlert } from 'react-alert'

function MyCalendar()  {

    const localizer = momentLocalizer(moment);
    const [eventsList, setEventsList] = useState([]);
    const [alarmsList, setAlarmsList] = useState([]);


    function handleSelect({ start, end }) {
        const title = window.prompt("New Event name");
        if (title) {
          var newEvent = {
            start: start,
            end: end,
            title: title,
            alarmTime1: '15:23:00',
          };
          setEventsList([...eventsList, newEvent]);
        }
      };

    return (
        <div>
        <Calendar
        messages={{
                  next: 'Следущий',
                  previous: 'Предыдущий',
                  today: 'Сегодня',
                  month: 'Месяц',
                  week: 'Неделя',
                  day: 'День',
                  yesterday: 'Вчера',
                  tomorrow: 'Завтра',
                  agenda: 'Мероприятия',
                  noEventsInRange: 'Не найдено никаких мероприятий в текущем периоде.',

                  showMore: function showMore(total) {
                    return '+' + total + 'событий';
                  }
        }}
        selectable

        defaultView="week"
        defaultDate={new Date()}
        localizer={localizer}
        events={eventsList}
        startAccessor="start"
        endAccessor="end"
        style={{ height: 500 }}
        onSelectSlot={handleSelect}
        />
        </div>
    )
}

class Alarm extends React.Component {
  constructor() {
    super();
    this.state = {
      currentTime: '',
      alarmTime: 'alarmTime1',
    };
  }

  componentDidMount(){
    this.clock = setInterval(() => this.setState({
                       currentTime: new Date().toLocaleTimeString('ru', { hour12: false })
                     }), 1000)
    this.interval = setInterval(
      () => this.checkAlarmClock(), 1000)
  }

  checkAlarmClock(){
      if(this.state.currentTime === this.state.alarmTime) {
        alert("its time!");
      } else {
        console.log("not yet");
      }
    }

  render() {
    return (
      <div>
        <h2>{this.alarmMessage}</h2>
      </div>
    );
  }
}
export {Alarm, MyCalendar};