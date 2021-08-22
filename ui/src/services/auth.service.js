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
                        'Csrf-Token': Cookies.get('csrfCookie')
                    }
                })
            .then(
                response => {
                    console.log(response.data)
                if (response.headers["x-auth-token"]) {
                    localStorage.setItem("user", JSON.stringify(response.data).slice(0, -1) + ',"accessToken":"' + response.headers["x-auth-token"] + '"}');
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
                    'Csrf-Token': Cookies.get('csrfCookie')
                }
            }
            );
    }

    getCurrentUser() {
        console.log(JSON.parse(localStorage.getItem('user')));
        return JSON.parse(localStorage.getItem('user'));
    }
}

export default new AuthService();