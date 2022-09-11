import React, {useEffect, useState} from 'react';
import './AuthorizationInfoPage.css'
import axios from "../../utils/axios-instance";

function AuthorizationInfoPage(props) {

    const [authorizationInfo, setAuthorizationInfo] = useState(null);

    useEffect(() => {
        axios.get("/user/get-authorization-info").then( res => {
            setAuthorizationInfo(res.data);
        })
    }, []);

   return (
        <div className={"authorization-info-page"}>
            <br/>
            <h2>Functions</h2><br/>
            <table className="table">
                <thead>
                <tr>
                    <th scope="col">Code</th>
                    <th scope="col">Name</th>
                    <th scope="col">Source</th>
                </tr>
                </thead>
                <tbody>
                { authorizationInfo?.functions?.function.map( tFunction => {
                            return <tr>
                                <td>{tFunction.code}</td>
                                <td>{tFunction.name}</td>
                                <td>{tFunction.source}</td>
                            </tr>
                        })
                }
                </tbody>
            </table>
            <br/><br/>
            <h2>Permissions</h2><br/>
            <table className="table">
                <thead>
                <tr>
                    <th scope="col">Key</th>
                    <th scope="col">Value</th>
                    <th scope="col">Description</th>
                </tr>
                </thead>
                <tbody>
                {
                    authorizationInfo?.permissions?.map( permission => {
                    return <tr>
                        <td>{permission.key}</td>
                        <td>{permission.value}</td>
                        <td>{permission.description}</td>
                    </tr>
                    })
                }

                </tbody>
            </table>
        </div>
    );
}

export default AuthorizationInfoPage;