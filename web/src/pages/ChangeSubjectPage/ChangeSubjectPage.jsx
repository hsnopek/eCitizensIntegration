import React, {useEffect} from 'react';
import './ChangeSubjectPage.css'
import {useNavigate, useSearchParams} from "react-router-dom";
import {
    setSubject,
} from "../../slices/navigationBarSlice";
import {useDispatch} from 'react-redux'
import Cookies from 'universal-cookie';

const cookies = new Cookies();

const ChangeSubjectPage = (props) => {

    const [searchParams, setSearchParams] = useSearchParams();
    let forPersonOib = searchParams.get("ForPersonOib");
    let forLegalIps = searchParams.get("ForLegalIps");
    let forLegalIzvorReg = searchParams.get("ForLegalIzvor_reg");
    let toLegalIps = searchParams.get("ToLegalIps");
    let toLegalIzvorReg = searchParams.get("ToLegalIzvor_reg");

    let navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        localStorage.setItem("accessToken", cookies.get("accessToken"))
        cookies.remove("accessToken", { path: "/", domain: "localhost" })
        dispatch(setSubject({forPersonOib, forLegalIps, forLegalIzvorReg, toLegalIps, toLegalIzvorReg}));
        navigate("/");
    }, []);

   return (
        <React.Fragment>
        </React.Fragment>
    );

}

export default ChangeSubjectPage;