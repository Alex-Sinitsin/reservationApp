import React, {useEffect, useRef, useState} from "react";

import FullCalendar from "@fullcalendar/react";

import ruLocale from '@fullcalendar/core/locales/ru';
import listPlugin from '@fullcalendar/list';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import bootstrapPlugin from '@fullcalendar/bootstrap'
import EventService from "./services/event.service";
import AuthService from "./services/auth.service";
import {Modal, ModalBody, ModalFooter} from "react-bootstrap";
import ModalHeader from "react-bootstrap/ModalHeader";


const MyCalendar = () => {
  const [eventList, setEventList] = useState([]);

  const currentUser = AuthService.getCurrentUser();

  const [modal, setModal] = React.useState(false);
  const [event, setEvent] = React.useState({
    id: "",
    title: "",
    start: new Date(),
    end: new Date(),
    orgUserId: "",
    members: "",
    itemID: "",
    description: "",
  });

  const handleEventClick = (eventInfo) => {
    setModal(true);
    setEvent(eventInfo.event);
  };

  const handleModalClose = () => {
    setModal(false);
    setEvent(null);
  }

  const calendarRef = useRef(null);

  async function getEventData() {
    try {
      const response = await EventService.getEvents();
      const parsedList = response.data && response.data.map((event) => {
        return {
          id: event.id,
          title: event.title,
          start: event.startDateTime,
          end: event.endDateTime,
          orgUserID: event.orgUserId,
          members: event.members.users,
          itemID: event.itemID,
          description: event.description,
          color: (event.members.users.find(user => user.id === currentUser.userInfo.id)) ? 'red' : {} //Изменяет цвет, если в событии участвует user
        }
      })

      setEventList(parsedList);
    } catch (err) {
      console.log(err, "API ERROR");
    }
  }

  // Получение данных о событиях
  useEffect(() => {
    getEventData();
  }, [eventList]);

  return (
    <div id="calendar" className="container" ref={calendarRef}>
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
      {event && (
        <Modal
          show={modal}
          onHide={handleModalClose}
        >
          <ModalHeader>
            <h4 className="col-12 modal-title text-center">{event.title}</h4>

          </ModalHeader>
          <ModalBody>
            <div>

              <p><b>Что занимается:</b> {event.itemID}</p>
              <p><b>Организатор:</b> {event.orgUserId}</p>
              <p><b>Начало события:</b> {event.start.toISOString().replace('T', ' ').slice(0,-8)}</p>
              <p><b>Конец события:</b> {event.end.toISOString().replace('T', ' ').slice(0,-8)}</p>
              <p><b>Участники:</b> {event.members}</p>
              <p><b>Описание:</b> {event.description}</p>

            </div>
          </ModalBody>
          <ModalFooter>
            <button type="button" className="btn btn-danger" onClick={handleModalClose}>Удалить событие</button>
            <button type="button" className="btn btn-primary" onClick={handleModalClose}>Закрыть</button>
          </ModalFooter>
        </Modal>
      )};
    </div>
  );
}

export default MyCalendar