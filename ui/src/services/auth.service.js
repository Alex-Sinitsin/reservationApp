import axios from "axios";
import Cookies from 'js-cookie';

const API_URL = "http://localhost:3000/api/";


class AuthService {

    // Авторизация пользователя
    login(email, password) {
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
                if (response.data.accessToken) {
                    localStorage.setItem("user", JSON.stringify(response.data));
                }
                return response.data;
            })
    }

    logout() {
        localStorage.removeItem("user");
    }

    // Регистрация пользователя
    register(name, lastName, position, email, password, confirmPassword) {
        return axios.post(API_URL + "signUp", {
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
                    'Content-Type': 'application/json'
                }
            }
            );
    }
    getCurrentUser() {
        return JSON.parse(localStorage.getItem('user'));
    }
}

export default new AuthService();