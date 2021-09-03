import React, {useState, useRef, useEffect} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";

import AuthService from "../services/auth.service";
import EventService from "../services/event.service";
import "./EventForm.css";
import Select from 'react-select'

import UserService from "../services/user.service"
import ItemService from "../services/item.service"


// Требование заполненности поля
const required = value => {
    if (!value) {
        return (
            <div className="invalid-feedback d-block">
                Заполните поле
            </div>
        );
    }
};

const vpassword = value => {
    if (value.length < 8) {
        return (
            <div className="invalid-feedback d-block">
                Пароль должен содержать минимум 8 символов
            </div>
        );
    }
};


const ChangePassword = (props) => {

    const form = useRef();
    const checkBtn = useRef();

    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const [successful, setSuccessful] = useState(false);
    const [message, setMessage] = useState("");


    const onChangeOldPassword = (e) => {
        const oldPassword = e.target.value;
        setOldPassword(oldPassword);
    };

    const onChangeNewPassword = (e) => {
        const newPassword = e.target.value;
        setNewPassword(newPassword);
    };

    const onChangeConfirmPassword = (e) => {
        const confirmPassword = e.target.value;
        setConfirmPassword(confirmPassword);
    };


    const handleChangePassword = (e) => {
        e.preventDefault();

        setMessage("");
        setSuccessful(false);

        form.current.validateAll();

        if (checkBtn.current.context._errors.length === 0) {
            AuthService.register(oldPassword, newPassword, confirmPassword).then(
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



    return (
        <div className="card card-container">
            <Form onSubmit={handleChangePassword} ref={form}>
                <div>
                    <div className="title-form">Изменение пароля</div>

                    <div className="form-group">
                        <label htmlFor="password">Старый пароль:</label>
                        <Input
                            type="password"
                            className="form-control"
                            name="oldPassword"
                            value={oldPassword}
                            onChange={onChangeOldPassword}
                            validations={[required, vpassword]}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Новый пароль:</label>
                        <Input
                            type="password"
                            className="form-control"
                            name="newPassword"
                            value={newPassword}
                            onChange={onChangeNewPassword}
                            validations={[required, vpassword]}
                        />
                    </div>


                    <div className="form-group">
                        <label htmlFor="password">Подтвердите пароль:</label>
                        <Input
                            type="password"
                            className="form-control"
                            name="password"
                            value={confirmPassword}
                            onChange={onChangeConfirmPassword}
                            validations={[required, vpassword]}
                        />
                    </div>


                    <div className="form-group">
                        <button className="btn eventSubmitBtn btn-block btn-primary">Изменить</button>
                    </div>
                </div>

                <CheckButton style={{display: "none"}} ref={checkBtn}/>

                {message && (
                    <div className="form-group">
                        <div className={successful ? "alert alert-success" : "alert alert-danger"} role="alert">
                            {message}
                        </div>
                    </div>
                )}


            </Form>
        </div>
    );
}

export default ChangePassword;