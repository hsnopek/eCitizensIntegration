import React, {useEffect, useState} from "react";
import axios from "../../utils/axios-instance";

function SimulateSendMessageToOkpPage(props) {

    const [response, setResponse] = useState('');
    const [oib, setOib] = useState('');

    const handleSendMessageToOkp = () => {
        axios.post("/okp/send-message/" + oib).then( res => {
            setResponse(res.data);
            alert('message successfully sent!');
        })
    }

    const handleInputChange = ({target: {name: name, value: value}}) => {
        setOib(value);
    }

    return (
        <div style={{display: "flex", flexDirection: "column", height: "100%", marginTop: "200px"}}>
            <input
                type={"text"}
                style={{width:"200px", alignSelf:"center"}}
                placeholder={"OIB"}
                id={"oib"}
                onChange={handleInputChange}
            />
            <button
                type="button"
                className="btn btn-primary"
                style={{width:"200px", alignSelf:"center", marginTop: '4px'}}
                onClick={() => handleSendMessageToOkp()}>Send message
            </button>

            <p>{response}</p>
        </div>
    );
}

export default SimulateSendMessageToOkpPage;