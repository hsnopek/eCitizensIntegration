import React, {useEffect} from 'react';
import './RedirectUser.css'
import {useNavigate, useParams} from "react-router-dom";
import axios from './../../utils/axios-instance'
import {setAll as setNavigationBarData, setShowEntitySearchCount} from "../../slices/navigationBarSlice";
import {setAll as setUserData} from "../../slices/userDataSlice";
import {useDispatch} from 'react-redux'
import Cookies from 'universal-cookie';

const cookies = new Cookies();

const RedirectUser = (props) => {

    const { originCountry, destinationCountry, userTid } = useParams();
    let navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        localStorage.setItem("accessToken", cookies.get("accessToken"))
        cookies.remove("accessToken", { path: "/", domain: "localhost" })

        axios.get('/user/get-user-data').then( res => {
            dispatch(setUserData(res.data));
            axios.get('/nias/navigation-bar-data').then( res => {
                dispatch(setNavigationBarData(res.data));
                dispatch(setShowEntitySearchCount(1));
                navigate("/");
            })
        })
    }, [])

   return (
        <React.Fragment>
        </React.Fragment>
    );
}

export default RedirectUser;