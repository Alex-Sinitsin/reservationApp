import React from "react";

import {Modal, ModalBody, ModalFooter} from "react-bootstrap";
import ModalHeader from "react-bootstrap/ModalHeader";

const CalendarEventModal = (props) => {

  const {isModalVisible, handleModalClose, handleDeleteEvent, isDeleteButtonVisible, eventData, itemData, orgUserData, deleteEvent} = props;

  return (
    <Modal
      show={isModalVisible}
      onHide={handleModalClose}
    >
      <ModalHeader>
        <h4 className="col-12 modal-title text-center">{eventData ? eventData.title : ""}</h4>
      </ModalHeader>
      <ModalBody>
        {eventData ? (
          <div>
            <p><b>Что занимается:</b> {itemData.name}</p>
            <p><b>Организатор:</b> {orgUserData.name} {orgUserData.lastName}</p>
            <p><b>Начало события:</b> {eventData.start.toString()}</p>
            <p><b>Конец события:</b> {eventData.end.toString()}</p>
            <p><b>Участники:</b></p>
            <ul>
              {eventData.members != [] ?
                eventData.members.map(member => {
                    return (
                      <li key={member.id}>{member.name} {member.lastName}</li>
                    )
                  }
                ) : null}
            </ul>
            <p><b>Описание:</b> {eventData.description}</p>
          </div>
        ) : null}
      </ModalBody>
      <ModalFooter>
        {isDeleteButtonVisible &&
        <button type="button" className="btn btn-danger" onClick={e => handleDeleteEvent(e, eventData.id)}>Удалить
          событие</button>}
        <button type="button" className="btn btn-primary" onClick={handleModalClose}>Закрыть</button>
      </ModalFooter>
    </Modal>
  );
}

export default CalendarEventModal;