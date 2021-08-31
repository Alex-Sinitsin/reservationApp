import axios from 'axios';
import authHeader from './auth-header';
import Cookies from "js-cookie";

const API_URL = 'http://localhost:3000/api/';

const getUsers = () => {
  return axios.get(API_URL + 'users',
    {
      headers: {
        "Csrf-Token": Cookies.get("csrfCookie"),
        'X-Auth-Token': authHeader(),
      },
    })
}

export default {
  getUsers,
}