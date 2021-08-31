import axios from "axios";
import Cookies from "js-cookie";
import authHeader from "./auth-header";

const API_URL = "http://localhost:3000/api/events/";

// Создание события
const add = (title, startDateTime, endDateTime, orgUserID, members, itemID, description) => {
    return axios.put(API_URL + "create",
        {
            title,
            startDateTime,
            endDateTime,
            orgUserID,
            members,
            itemID,
            description
        },
        {
            headers: {
                'Csrf-Token': Cookies.get('csrfCookie'),
                'Content-Type': 'application/json',
                'X-Auth-Token': authHeader(),
            }
        });
}

export default {
    add,
}