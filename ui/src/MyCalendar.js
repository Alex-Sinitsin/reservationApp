import React, { useState, useEffect } from "react";

import FullCalendar from "@fullcalendar/react";

import ruLocale from '@fullcalendar/core/locales/ru';
import listPlugin from '@fullcalendar/list';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import bootstrapPlugin from '@fullcalendar/bootstrap'
import EventService from "./services/event.service";
import moment from 'moment';


export default function MyCalendar() {
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
                const parsedList = response.data && response.data.map((event) => {

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

  let counter = 0;
  while (counter != 100) {
    if (state.currentTime === alarmTime2[counter]) {
      alert("its time!");
    }
    ;
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
            height={800}
            slotDuration={'00:30:00'}
            themeSystem={'bootstrap'}
            defaultView="dayGridMonth"
            headerToolbar={{
                left: 'prev,today,next',
                center: 'title',
                right: "dayGridMonth,timeGridWeek,timeGridDay,listMonth"
            }}
            buttonText={{
                prev: '<<',
                next: '>>'
            }}
            dayMaxEvents = {true}
            footerToolbar={{
                right: "deleteEvent"
            }}
            locales = {ruLocale}
            locale = 'ru'
            customButtons={{
                deleteEvent: {
                    text: 'Удалить',
                    click: function() {
                        let event = eventList.event;
                        alert("Are You Remove Event "+event);
                        event.remove();
                    }
                },
            }}

            plugins={[dayGridPlugin, listPlugin, timeGridPlugin, interactionPlugin, bootstrapPlugin]}
            events={eventList}
            editable={true}
            droppable={true}
            eventResizableFromStart={true}
        />
    );
}