import React, {useEffect, useState} from 'react';
import {useSearchParams} from "react-router-dom";
import {cleanStore as cleanNavigationBarData } from "../../slices/navigationBarSlice";
import {cleanStore as cleanUserData} from "../../slices/userDataSlice";
import {useDispatch} from 'react-redux'

const LogoutPage = (props) => {

    const dispatch = useDispatch();
    const [searchParams, setSearchParams] = useSearchParams();

    useEffect(() => {
        dispatch(cleanNavigationBarData());
        dispatch(cleanUserData());
        window.location = searchParams.get("redirectUrl")
    }, [])

    return (
        <React.Fragment>
        </React.Fragment>
    );
}

export default LogoutPage;