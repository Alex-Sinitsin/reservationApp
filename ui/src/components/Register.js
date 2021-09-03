import React, {useState, useRef, useEffect} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import { isEmail } from "validator";

import AuthService from "../services/auth.service";
import Header from "../Header";


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

// Проверка на корректность e-mail
const correctEmail = value => {
    if (!isEmail(value)) {
        return (
            <div className="alert alert-danger" role="alert">
                Введите корректный e-mail
            </div>
        );
    }
};

// Позволяет устанавливать допустимые размер пароля
const vpassword = value => {
    if (value.length < 3 || value.length > 40) {
        return (
            <div className="alert alert-danger" role="alert">
                Пароль должен содержать от 3 до 40 символов
            </div>
        );
    }
};

const Register = (props) => {

    useEffect(() => {
        document.title = 'Регистрация';
    });

    const form = useRef();
    const checkBtn = useRef();

    const [name, setName] = useState("");
    const [lastName, setLastName] = useState("");
    const [position, setPosition] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const [successful, setSuccessful] = useState(false);
    const [message, setMessage] = useState("");


    const onChangeName = (e) => {
        const name = e.target.value;
        setName(name);
    };

    const onChangeLastName = (e) => {
        const lastName = e.target.value;
        setLastName(lastName);
    };

    const onChangePosition = (e) => {
        const position = e.target.value;
        setPosition(position);
    };

    const onChangeEmail = (e) => {
        const email = e.target.value;
        setEmail(email);
    };

    const onChangePassword = (e) => {
        const password = e.target.value;
        setPassword(password);
    };

    const onChangeConfirmPassword = (e) => {
        const confirmPassword = e.target.value;
        setConfirmPassword(confirmPassword);
    };

    const handleRegister = (e) => {
        e.preventDefault();

        setMessage("");
        setSuccessful(false);

        form.current.validateAll();

        if (checkBtn.current.context._errors.length === 0) {
            AuthService.register(name, lastName, position, email, password, confirmPassword).then(
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
            <div className="col-md-12">
                    <Header/>
                <div className="card card-container">
                    <img
                        src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
                        alt="profile-img"
                        className="profile-img-card"
                    />

                    <Form onSubmit={handleRegister} ref={form}>
                        {!successful && (
                            <div>
                                <div className="form-group">
                                    <label htmlFor="name">Имя:</label>
                                    <Input
                                        type="text"
                                        className="form-control"
                                        name="name"
                                        value={name}
                                        onChange={onChangeName}
                                        validations={[required]}
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="lastName">Фамилия:</label>
                                    <Input
                                        type="text"
                                        className="form-control"
                                        name="lastName"
                                        value={lastName}
                                        onChange={onChangeLastName}
                                        validations={[required]}
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="position">Должность:</label>
                                    <Input
                                        type="text"
                                        className="form-control"
                                        name="position"
                                        value={position}
                                        onChange={onChangePosition}
                                        validations={[required]}
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="email">E-mail:</label>
                                    <Input
                                        type="text"
                                        className="form-control"
                                        name="email"
                                        value={email}
                                        onChange={onChangeEmail}
                                        validations={[required, correctEmail]}
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="password">Пароль:</label>
                                    <Input
                                        type="password"
                                        className="form-control"
                                        name="password"
                                        value={password}
                                        onChange={onChangePassword}
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
                                        validations={[required]}
                                    />
                                </div>


                                <div className="form-group">
                                    <div className="text-center">
                                    <button className="btn btn-primary btn-block">Зарегистрироваться</button>
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
            </div>
        );
}

export default Register;