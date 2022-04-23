import React, {useEffect, useState} from 'react'
import {API_URL} from "../constants/constants";
import {useDispatch} from "react-redux";

export const useNavbarEffect = ({   messageId,
                                    navToken,
                                    showPersons,
                                    showEntites,
                                    showEntitySearch,
                                    showEntitySearchCount,
                                    showVisionImpaired,
                                    showDyslexia,
                                    showFontResize,
                                    showLoginButton,
                                    forPersonOib,
                                    forLegalIps,
                                    forLegalIzvorReg,
                                    toLegalIps,
                                    toLegalIzvorReg}, {ident, sessionIndex, state}) => {

    const sessionIndexForeignIndicator = state === 'HR' ? '' : 'fgn';
    const loginUrl = API_URL + encodeURIComponent('/nias/sign-in')
    const logoutUrl = API_URL + encodeURIComponent('/nias/log-out?sessionIndex=' + sessionIndexForeignIndicator + sessionIndex)
    const changeEntityUrl = API_URL + encodeURIComponent("/authorization/actAs?ForPersonOib={ForPersonOib}&ForLegalIps={ForLegalIps}&ForLegalIzvor_reg={ForLegalIzvor_reg}&ToLegalIps={ToLegalIps}&ToLegalIzvor_reg={ToLegalIzvor_reg}&ident=" + ident);

    useEffect(() => {
        const script = document.createElement("script");
        let scriptUrl = "https://eusluge-nav-test.gov.hr/e_gradani.aspx?"

        scriptUrl += "messageId=" + messageId;
        scriptUrl += "&navToken=" + navToken;
        scriptUrl += "&login_url=" + loginUrl;
        scriptUrl += "&logout_url=" + logoutUrl;

        if (showEntitySearch && showEntitySearchCount === 0 ) {
            scriptUrl += "&show_entity_search=" + showEntitySearch;
        }
        if (changeEntityUrl)
            scriptUrl += "&change_entity_url=" + changeEntityUrl;
        if (showLoginButton)
            scriptUrl += "&show_login_button=" + showLoginButton;
        if (showPersons)
            scriptUrl += "&show_persons=" + showPersons
        if (showEntites)
            scriptUrl += "&show_entities=" + showEntites;
        if (showVisionImpaired)
            scriptUrl += "&show_vision_impaired=" + showVisionImpaired;
        if (showDyslexia)
            scriptUrl += "&show_dyslexia=" + showDyslexia;
        if (showFontResize)
            scriptUrl += "&show_font_resize=" + showFontResize;
        if (forPersonOib)
            scriptUrl += "&ForPersonOib=" + forPersonOib;
        if (forLegalIps)
            scriptUrl += "&ForLegalIps=" + forLegalIps;
        if (forLegalIzvorReg)
            scriptUrl += "&ForLegalIzvor_reg=" + forLegalIzvorReg;
        if (toLegalIps)
            scriptUrl += "&ToLegalIps=" + toLegalIps;
        if (toLegalIps)
            scriptUrl += "&ToLegalIzvor_reg=" + toLegalIzvorReg;

        script.src = scriptUrl;
        script.async = true;
        script.type = "text/javascript";

        let scriptTag = document.body.getElementsByTagName("script")[0];

        if (scriptTag) {
            scriptTag.remove();
        }
        document.body.appendChild(script);
    }, [messageId, navToken, forPersonOib, forLegalIps, forLegalIzvorReg, toLegalIps, toLegalIzvorReg, showEntitySearchCount, sessionIndex , ident])

}
