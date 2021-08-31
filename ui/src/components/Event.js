import React, {useState, useRef, useEffect} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";

import AuthService from "../services/auth.service";
import EventService from "../services/event.service";
import "./Event.css";
import Select from 'react-select'

import User from "../services/user.service"
import Item from "../services/item.service"


// Требование заполненности поля
const required = value => {
    if (!value) {
        return (
            <div className="alert alert-danger" role="alert">
                Заполните поле
            </div>
        );
    }
};


const AddEvent = (props) => {

    const form = useRef();
    const checkBtn = useRef();
    const currentUser = AuthService.getCurrentUser();

    const [title, setTitle] = useState("");
    const [startDateTime, setStartDateTime] = useState("");
    const [endDateTime, setEndDateTime] = useState("");
    const [orgUserID] = useState(currentUser.userInfo.id);

    const [members, setMembers] = useState("");
    const [membersList, setMembersList] = useState("");

    const [itemID, setItemID] = useState(null);
    const [itemList, setItemList] = useState([]);

    const [description, setDescription] = useState("");

    const [successful, setSuccessful] = useState(false);
    const [message, setMessage] = useState("");


    const onChangeTitle = (e) => {
        const title = e.target.value;
        setTitle(title);
    };

    const onChangeStartDateTime = (e) => {
        const startDateTime = e.target.value;
        setStartDateTime(startDateTime);
    };

    const onChangeEndDateTime = (e) => {
        const endDateTime = e.target.value;
        setEndDateTime(endDateTime);
    };

    const onChangeDescription = (e) => {
        const description = e.target.value;
        setDescription(description);
    };


    const handleAddEvent = (e) => {
        e.preventDefault();

        setMessage("");
        setSuccessful(false);

        form.current.validateAll();

        if (checkBtn.current.context._errors.length === 0) {
            EventService.add(title, startDateTime.replace('T',' '), endDateTime.replace('T',' '), orgUserID, members.map(x => x.value), itemID.value, description).then(
                (response) => {
                    setMessage(response.data.message);
                    setSuccessful(true);
                },
                (error) => {
                    const resMessage =
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString();

                    setMessage(resMessage);
                    setSuccessful(false);
                }
            );
        }
    };

// Получение данных об Items в Select
    useEffect(() => {
        async function getItemData() {
            try {
                const response = await Item.getItems();
                const parsedList = response.data && response.data.map((item) => {

                    return{
                        value: item.id,
                        label: `${item.name}`
                    }
                })

                setItemList(parsedList);
            } catch (err) {
                console.log(err, "API ERROR");
            }
        }
        getItemData();
    }, []);


// Получение данных о Users в Select
    useEffect(() => {
        async function getMembersData() {
            try {
                const response = await User.getUsers()

                const parsedList = response.data && response.data.map((user) => {

                    return{
                        value: user.id,
                        label: `${user.name} ${user.lastName} - ${user.position}`
                    }
                })

                setMembersList(parsedList);
            } catch (err) {
                console.log(err, "API ERROR");
            }
        }
        getMembersData();
    }, []);




    function handleChangeItem(selectedItem) {
        setItemID(selectedItem);
    }

    function handleChangeMembers(selectedMembers) {
        setMembers(selectedMembers);
    }


    return (
            <div className="card card-container">

                <Form onSubmit={handleAddEvent} ref={form}>
                    {!successful && (
                        <div>
                            <div className="title-form">Создание события</div>

                            <div className="form-group">
                                <label htmlFor="itemID">Помещение/Предмет: *</label>
                                <Select
                                    value={itemID}
                                    onChange={handleChangeItem}
                                    options={itemList}
                                    placeholder={null}
                                    components={{IndicatorSeparator: () => null}}
                                    validations={[required]}
                                />
                            </div>


                            <div className="form-group">
                                <label htmlFor="title">Название события: *</label>
                                <Input
                                    type="text"
                                    className="form-control"
                                    name="title"
                                    value={title}
                                    onChange={onChangeTitle}
                                    validations={[required]}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="startDateTime">Время начала: *</label>
                                <Input
                                    type="datetime-local"
                                    className="form-control"
                                    name="startDateTime"
                                    value={startDateTime}
                                    onChange={onChangeStartDateTime}
                                    validations={[required]}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="endDateTime">Время окончания: *</label>
                                <Input
                                    type="datetime-local"
                                    className="form-control"
                                    name="endDateTime"
                                    value={endDateTime}
                                    onChange={onChangeEndDateTime}
                                    validations={[required]}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="members">Участники:</label>
                            <Select
                                isMulti
                                name="members"
                                value={members}
                                options={membersList}
                                placeholder={null}
                                onChange={handleChangeMembers}
                                components={{IndicatorSeparator: () => null}}
                                className="basic-multi-select"
                                classNamePrefix="select"
                            />
                            </div>

                            <div className="form-group">
                                <label htmlFor="description">Описание:</label>
                                <Input
                                    type="description"
                                    className="form-control"
                                    name="description"
                                    value={description}
                                    onChange={onChangeDescription}
                                />
                            </div>


                            <div className="form-group">
                                <div className="text-center">
                                    <button className="btn btn-primary btn-block">Создать</button>
                                </div>
                            </div>
                        </div>
                    )}

                    {message && (
                        <div className="form-group">
                            <div className={successful ? "alert alert-success" : "alert alert-danger"} role="alert">
                                {message}
                            </div>
                        </div>
                    )}
                    <CheckButton style={{ display: "none" }} ref={checkBtn} />
                </Form>
            </div>
    );
}

export default AddEvent;