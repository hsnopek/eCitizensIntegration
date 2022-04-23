import axios from 'axios';
import { API_URL, ERROR_CODE_ACCESS_TOKEN_INVALID, ERROR_CODE_REFRESH_TOKEN_INVALID } from "../constants/constants";
import Cookies from 'universal-cookie';
import { store, persistor } from "../store/store"
import { cleanStore as cleanNavigationBarData } from "../slices/navigationBarSlice";
import { cleanStore as cleanUserData } from "../slices/userDataSlice";

const cookies = new Cookies();

const axiosInstance = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
        //'User-Agent': navigator.userAgent.replaceAll(";", ' ')
    },
    withCredentials: true
})

axiosInstance.interceptors.request.use(
    config => {
        let accessToken = localStorage.getItem("accessToken");
        if (accessToken) {
            config.headers.Authorization = "Bearer " + accessToken;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);
axiosInstance.interceptors.response.use(
    config => {
        return Promise.resolve(config);
    },
    (error) => {
        console.log(error.response)
        if (error?.response?.status === 401 && (error?.response?.data?.errorCode === ERROR_CODE_ACCESS_TOKEN_INVALID)) {
            console.log('first request failed... trying to refresh token..')
            const originalRequest = error.config
            originalRequest._retry = true;
            return axiosInstance.post('/auth/refresh-token')
                .then(res => {
                    if (res.status === 200) {
                        localStorage.setItem("accessToken", res.data.accessToken);
                        axios.defaults.headers.common['Authorization'] = 'Bearer ' + localStorage.getItem("accessToken");
                        return axiosInstance(originalRequest);
                    }
                }).catch( error => {
                    console.log(error)
                    originalRequest._retry=false;
                })
        } else if( error?.response?.data?.errorCode === ERROR_CODE_REFRESH_TOKEN_INVALID
            || error?.response?.data?.errorCode === 0) {

            store.dispatch(cleanNavigationBarData());
            store.dispatch(cleanUserData());
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
