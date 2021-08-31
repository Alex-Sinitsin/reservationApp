import React, {useState, Component, useEffect, setState} from "react";
import ReactDOM from "react-dom";
import './App.css';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import {Calendar, momentLocalizer} from 'react-big-calendar'
import moment from 'moment'
import 'moment/locale/ru'
import {Alert, useAlert} from 'react-alert'
import {userName} from './components/Home';
import Clock from 'react-clock';

function MyCalendar() {
  const localizer = momentLocalizer(moment);
  const [eventsList, setEventsList] = useState([]);
  const [alarmTime2, setAlarm] = useState([]);
  const [seconds, setSeconds] = useState([]);

  var state = {
    currentTime: new Date().toLocaleTimeString('ru', {hour12: false}),
  }

  useEffect(() => {
    const timer = setInterval(() => {
      setSeconds(
        state.currentTime)
    }, 1000);
    return () => clearInterval(timer);
  });

  function handleSelect({start, end, userName}) {
    const title = window.prompt("New Event name");
    if (title) {
      var newEvent = {
        userName: {userName},
        start: start,
        end: end,
        title: title,
        alarmTime1: start.toLocaleTimeString('ru', {hour12: false})
      };
      setEventsList([...eventsList, newEvent]);
      setAlarm([...alarmTime2, newEvent.alarmTime1]);
    }
  }

  var counter = 0;
  while (counter != 100) {
    if (state.currentTime === alarmTime2[counter]) {
      alert("its time!");
    }
    ;
    counter = counter + 1;
  }


  return (
    <div>
      <Calendar
        step={15}
        messages={{
          next: '>>',
          previous: '<<',
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
        style={{height: 620}}
        // onSelectSlot={handleSelect}

      />
    </div>
  )

}

export default MyCalendar;