import React, { useState, useEffect } from "react";
import {userName} from './Home';
import Clock from 'react-clock';

import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from '@fullcalendar/interaction';

import "@fullcalendar/common/main.css";
import "@fullcalendar/daygrid/main.css";
import "@fullcalendar/timegrid/main.css";

import 'bootstrap/dist/css/bootstrap.css';
import '@fortawesome/fontawesome-free/css/all.css';

import bootstrapPlugin from '@fullcalendar/bootstrap';

export default function MyCalendar() {
    const [eventsList, setEventsList] = useState([]);
    const [alarmTime2, setAlarm] = useState([]);
    const [seconds, setSeconds] = useState([]);

        var state = {
           currentTime: new Date().toLocaleTimeString('ru', { hour12: false }),
           }

        useEffect(() => {
                 const timer = setInterval(() => {
                      setSeconds(
                      state.currentTime )
                 }, 1000);
                 return () => clearInterval(timer);
        });

        var counter = 0;
        while(counter != 100){
             if(state.currentTime === alarmTime2[counter]) {
                     alert("its time!");
                     };
              counter = counter + 1;
             }

  return (
      <FullCalendar
        firstDay={1}
        businessHours={{
          daysOfWeek: [ 1, 2, 3, 4, 5 ],
        }}
        navLinks={true}
        nowIndicator={true}
        themeSystem={'bootstrap'}
        height={750}
        slotDuration={'00:30:00'}
        defaultView="dayGridMonth"
                buttonText={{
                      today:    'сегодня',
                      month:    'месяц',
                      week:     'неделя',
                      day:      'день',
                }}
                headerToolbar={{
                  left: "prev today next",
                  center: "title myDeleteEvent",
                  right: "dayGridMonth,timeGridWeek,timeGridDay"
                }}
                footerToolbar={{
                    left: "myCustomButton"
                }}
                customButtons={{
                        myDeleteEvent: {
                            text: 'Удалить',
                               click: function() {
                                 var event = eventsList.event;
                                 alert("Are You Remove Event "+event);
                                 event.remove();
                                    }
                                    },
                        myCustomButton: {
                            text: 'Создать',
                                    click: function() {
                                      var startDateStr = prompt('Введите время начала события (ГГГГ-ММ-ДД ЧЧ:ММ):');
                                      var startDate = new Date(startDateStr);
                                      var endDateStr = prompt('Введите время конца события (ГГГГ-ММ-ДД ЧЧ:ММ):');
                                      var endDate = new Date(endDateStr);
                                      const title = window.prompt("Введите название события:");
                                      const members = window.prompt("Введите имена участников:");
                                      if (members) {
                                      if (title) {
                                      if (!isNaN(startDate.valueOf())) {
                                      if (!isNaN(endDate.valueOf())) {
                                        var addEvent ={
                                          title: title,
                                          start: startDate,
                                          end: endDate,
                                          userName: {userName},
                                          members: members,
                                          alarmTime1: startDate.toLocaleTimeString('ru', { hour12: false })
                                        };
                                        alert('Хорошо! Вы создали новое событие.');
                                        setEventsList([...eventsList, addEvent]);
                                        setAlarm([...alarmTime2, addEvent.alarmTime1]);
                                      } else {
                                        alert('Invalid date.');
                                        }
                                      }
                                      }
                                      }
                                    },
                        },
                    }}

        plugins={[dayGridPlugin, timeGridPlugin, bootstrapPlugin, interactionPlugin ]}
        events={eventsList}
        editable={true}
        droppable={true}
        eventResizableFromStart={true}
        locale="ru"

      />
  );
}