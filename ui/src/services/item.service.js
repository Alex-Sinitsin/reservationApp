import axios from 'axios';
import authHeader from './auth-header';
import Cookies from "js-cookie";

const API_URL = 'http://localhost:3000/api/';

const getItems = () => {
    return axios.get(API_URL + 'items',
        {
            headers: {
              "Csrf-Token": Cookies.get("csrfCookie"),
              'Content-Type': 'application/json',
              'X-Auth-Token': authHeader(),
            }
        });
}

const getItemByID = (itemID) => {
  return axios.get(API_URL + 'items/' + itemID,
    {
      headers: {
        "Csrf-Token": Cookies.get("csrfCookie"),
        'Content-Type': 'application/json',
        'X-Auth-Token': authHeader(),
      }
    });
}

export default{
    getItems, getItemByID,
}