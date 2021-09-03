import React, { useState, useEffect } from "react";

import UserService from "../services/user.service";


const BoardUser = () => {

    useEffect(() => {
        document.title = 'Пользователь';
    });
};

export default BoardUser;