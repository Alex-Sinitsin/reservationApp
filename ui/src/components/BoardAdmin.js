import React, { useState, useEffect } from "react";

import UserService from "../services/user.service";
import Header from "./Header";
import {Link} from "react-router-dom";
import Event from "./Event";
import MyCalendar from "../MyCalendar";

const BoardAdmin = () => {

    useEffect(() => {
        document.title = 'Панель администратора';
    });



    return (
        <div>
            <Header/>
    <div className="row">
        <div className="card card-container col-md-4">
            <div className="d-flex flex-column bd-highlight mb-3">
                <div className="p-2 bd-highlight">
                    <Link to={"/register"} className="link">
                        Регистрация пользователей
                    </Link>
                </div>
            </div>
        </div>
    </div>
        </div>
    )
};

export default BoardAdmin;