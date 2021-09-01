import React, {useState, useEffect} from "react";
import './App.css';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import {Calendar, momentLocalizer} from 'react-big-calendar'
import moment from 'moment'
import 'moment/locale/ru'
import EventService from "./services/event.service";





function MyCalendar() {
  const localizer = momentLocalizer(moment);
  const [eventList, setEventList] = useState([]);
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

// Получение данных о событиях
    useEffect(() => {
        async function getEventData() {
            try {
                const response = await EventService.getEvents();
                const parsedList = response.data.data && response.data.data.map((event) => {

                    return {
                        id: event.id,
                        title: event.title,
                        start: moment(event.startDateTime, 'YYYY-MM-DD HH:mm').toDate(),
                        end: moment(event.endDateTime, 'YYYY-MM-DD HH:mm').toDate(),
                        members: event.members.users,
                        desc: event.description


                    }
                })

                setEventList(parsedList);
            } catch (err) {
                console.log(err, "API ERROR");
            }
        }

        getEventData();
    }, []);



  function handleEvents(eventList) {
      setEventList(eventList);
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
                    date: 'Дата',
                    time: 'Время',
                    event: 'Событие',
                    noEventsInRange: 'Не найдено никаких мероприятий в текущем периоде.',
                    showMore: function showMore(total) {
                        return '+' + total + 'событий';
                    }

                }}
                selectable
                defaultView="month"
                defaultDate={new Date()}
                localizer={localizer}
                events={eventList}
                startAccessor="start"
                endAccessor="end"
                style={{height: 620}}
                // events={handleEvents}

            />
        </div>
    )

}

export default MyCalendar;