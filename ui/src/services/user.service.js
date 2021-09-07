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

const changePassword = (currentPassword, newPassword, confirmPassword) => {
  return axios.put(API_URL + 'password/change', {
      currentPassword,
      newPassword,
      confirmPassword
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
  getUsers, changePassword
}