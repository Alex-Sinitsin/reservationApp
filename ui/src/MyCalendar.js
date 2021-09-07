import React, {useState, useEffect} from "react";

import FullCalendar from "@fullcalendar/react";

import ruLocale from '@fullcalendar/core/locales/ru';
import listPlugin from '@fullcalendar/list';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import bootstrapPlugin from '@fullcalendar/bootstrap'
import EventService from "./services/event.service";
import AuthService from "./services/auth.service";
import includes from "validator/es/lib/util/includes";


export default function MyCalendar() {
  const [eventList, setEventList] = useState([]);
  const [alarmTime2, setAlarm] = useState([]);
  const [seconds, setSeconds] = useState([]);

  const currentUser = AuthService.getCurrentUser();

  let state = {
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
            start: event.startDateTime,
            end: event.endDateTime,
            members: event.members.users,
            description: event.description,
            color: (event.members.users.find(user => user.id === currentUser.userInfo.id)) ? 'red' : {} //Изменяет цвет, если в событии участвует user
          }
        })

        setEventList(parsedList);
      } catch (err) {
        console.log(err, "API ERROR");
      }
    }

    getEventData();
  }, [eventList]);

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
      themeSystem="bootstrap"
      firstDay={1}
      businessHours={{
        daysOfWeek: [1, 2, 3, 4, 5],
      }}
      navLinks={true}
      nowIndicator={true}
      height={750}
      slotDuration={'00:30:00'}
      defaultView="dayGridMonth"
      headerToolbar={{
        left: 'prev,today,next',
        center: 'title',
        right: "dayGridMonth,timeGridWeek,timeGridDay,listMonth"
      }}
      buttonText={{
        prev: '<<',
        next: '>>',
        listMonth: 'Расписание'
      }}
      dayMaxEvents={true}
      footerToolbar={{
        right: "deleteEvent"
      }}
      locales={ruLocale}
      locale='ru'
      customButtons={{
        deleteEvent: {
          text: 'Удалить',
          click: function () {
            let event = eventList.id;
            alert("Вы действительно хотите удалить событие?");
            event.remove();
          }
        },
      }}

      plugins={[dayGridPlugin, listPlugin, timeGridPlugin, interactionPlugin, bootstrapPlugin]}
      events={eventList}
      editable={false}
      droppable={false}
      eventResizableFromStart={true}
      allDaySlot={false}
    />
  );
}