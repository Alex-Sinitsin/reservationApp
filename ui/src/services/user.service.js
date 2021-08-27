import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:3000/api/';

    const getUserBoard = () => {
        return axios.get(API_URL + "user", { headers: authHeader() });
    };

    const getAdminBoard = () => {
        return axios.get(API_URL + 'admin', { headers: authHeader() });
    }

export default{
        getUserBoard,
        getAdminBoard,
}