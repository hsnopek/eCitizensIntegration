import { createSlice } from '@reduxjs/toolkit'
import {cleanCookies} from "universal-cookie/es6/utils";

export const navigationBarSlice = createSlice({
    name: 'navigationBar',
    initialState: {
        messageId: '',
        navToken: '',
        showEntitySearch: 'true',
        showPersons: 'true',
        showEntities: 'true',
        showVisionImpaired: 'true',
        showDyslexia: 'true',
        showFontResize: 'true',
        showLoginButton: 'true',
        forPersonOib: '',
        forLegalIps: '',
        forLegalIzvorReg: '',
        toLegalIps: '',
        toLegalIzvorReg: '',
        showEntitySearchCount: 0,
    },
    reducers: {
        // add your non-async reducers here
        setAll: (state, action) => {
            state.messageId = action.payload.messageId;
            state.navToken = action.payload.navToken;
            state.showEntitySearch = action.payload.showEntitySearch;
            state.showPersons = action.payload.showPersons;
            state.showEntities = action.payload.showEntities;
            state.showVisionImpaired = action.payload.showVisionImpaired ;
            state.showDyslexia = action.payload.showDyslexia;
            state.showFontResize = action.payload.showFontResize;
            state.showLoginButton = action.payload.showLoginButton;
            state.forPersonOib = action.payload.forPersonOib;
            state.forLegalIps = action.payload.forLegalIps;
            state.forLegalIzvorReg = action.payload.forLegalIzvorReg;
            state.toLegalIps = action.payload.toLegalIps;
            state.toLegalIzvorReg = action.payload.toLegalIzvorReg;
        },
        setSubject: (state, action) => {
            state.forPersonOib = action.payload.forPersonOib;
            state.forLegalIps = action.payload.forLegalIps;
            state.forLegalIzvorReg = action.payload.forLegalIzvorReg;
            state.toLegalIps = action.payload.toLegalIps;
            state.toLegalIzvorReg = action.payload.toLegalIzvorReg;
        },
        setShowEntitySearchCount: (state, action) => {
            console.log(state.showEntitySearchCount, 'showEntitySearchCount');
            console.log(action.payload, 'payload')
            state.showEntitySearchCount = action.payload;
        }, cleanStore(state, payload){
            cleanCookies();
            localStorage.removeItem("refreshToken");
            localStorage.removeItem("accessToken");
            state.messageId = null;
            state.navToken = null;
            state.showEntitySearch = null;
            state.showPersons = null;
            state.showEntities = null;
            state.showVisionImpaired = null ;
            state.showDyslexia = null;
            state.showFontResize = null;
            state.showLoginButton = null;
            state.forPersonOib = null;
            state.forLegalIps = null;
            state.forLegalIzvorReg = null;
            state.toLegalIps = null;
            state.toLegalIzvorReg = null;

            // zasto ne mogu settati initialState? state = {...this.initialState}
        }
    },
    extraReducers: {
        // add your async reducers here
    }
})

// Action creators
export const {
    setAll,
    setSubject,
    setShowEntitySearchCount,
    cleanStore
} = navigationBarSlice.actions;

export default navigationBarSlice.reducer;
