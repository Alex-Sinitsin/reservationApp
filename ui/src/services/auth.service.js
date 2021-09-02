import axios from "axios";
import Cookies from 'js-cookie';
import authHeader from "./auth-header";

const API_URL = "http://localhost:3000/api/";

// Авторизация пользователя
const login = (email, password) => {
  return axios
    .post(API_URL + "signIn",
      {
        email,
        password
      },
      {
        headers: {
          'Csrf-Token': Cookies.get('csrfCookie'),
          'Content-Type': 'application/json'
        }
      })
    .then(
      response => {
        if (response.data.data.accessToken) {
          localStorage.setItem("user", JSON.stringify(response.data.data));
        }
        return response.data.data;
      })
}

const logout = () => {
  localStorage.removeItem("user");
}

// Регистрация пользователя
const register = (name, lastName, position, email, password, confirmPassword) => {
  return axios.put(API_URL + "signUp", {
      name,
      lastName,
      position,
      email,
      password,
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

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem('user'));
}

export default {
  login,
  register,
  logout,
  getCurrentUser,
}