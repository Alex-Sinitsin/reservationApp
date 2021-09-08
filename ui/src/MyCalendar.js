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
import ChoiceForm from "./components/ChoiceForm";
import {Button, Modal, ModalBody, ModalFooter} from "react-bootstrap";
import ModalHeader from "react-bootstrap/ModalHeader";



export default function MyCalendar() {
  const [eventList, setEventList] = useState([]);
  const [alarmTime2, setAlarm] = useState([]);
  const [seconds, setSeconds] = useState([]);

  const currentUser = AuthService.getCurrentUser();


  const [modal, setModal] = React.useState(false);
  const [event, setEvent] = React.useState({
    title: "",
    start: new Date()
  });

  function toggle() {
    setModal({ modal: !modal });
  };

  function handleEventClick({ event, el }) {
    toggle();
    setEvent({ event });
  };


  // Получение данных о событиях
  useEffect(() => {
    async function getEventData() {
      try {
        const response = await EventService.getEvents;

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




  return (
      <div id="calendar" className="container" ref="calendar">
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
      eventClick={handleEventClick}
    />
        <Modal
            isOpen={modal}
            toggle={toggle}
        >
          <ModalHeader toggle={toggle}>
            {event.title}
          </ModalHeader>
          <ModalBody>
            <div>
              <p>{event.start.toISOString()}</p>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="primary">Do Something</Button>{" "}
            <Button color="secondary" onClick={toggle}>
              Cancel
            </Button>
          </ModalFooter>
        </Modal>
      </div>
  );
}