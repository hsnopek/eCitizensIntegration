import React, {useEffect, useState} from 'react';
import './UserInfoPage.css'
import axios from "../../utils/axios-instance";

function UserInfoPage(props) {

    const [sessionList, setSessionList] = useState(null);

    useEffect(() => {
        axios.get("/user/get-user-session-data").then( res => {
            setSessionList(res.data);
        })
    }, []);

   return (
        <div className={"user-info-page"}>
            <table className="table">
                <thead>
                <tr>
                    <th scope="col">First name</th>
                    <th scope="col">Last name</th>
                    <th scope="col">Ident</th>
                    <th scope="col">State</th>
                    <th scope="col">DeviceId</th>
                    <th scope="col">NavToken</th>
                    <th scope="col">SessionIndex</th>
                    <th scope="col">SessionId</th>
                </tr>
                </thead>
                <tbody>
                {
                    sessionList && sessionList.map((session, index) => {
                        return <tr key={index}>
                            <td>{session?.firstName}</td>
                            <td>{session?.lastName}</td>
                            <td>{session?.ident}</td>
                            <td>{session?.state}</td>
                            <td>{session?.deviceId}</td>
                            <td>{session?.navToken}</td>
                            <td>{session?.sessionIndex}</td>
                            <td>{session?.sessionId}</td>
                        </tr>
                    })
                }
                </tbody>
            </table>
        </div>
    );
}

export default UserInfoPage;