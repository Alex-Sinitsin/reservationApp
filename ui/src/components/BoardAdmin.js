import React, { useState, useEffect } from "react";

import UserService from "../services/user.service";


const BoardAdmin = () => {

    useEffect(() => {
        document.title = 'Панель администратора';
    });
};

export default BoardAdmin;