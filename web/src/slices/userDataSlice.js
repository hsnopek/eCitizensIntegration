import { createSlice } from '@reduxjs/toolkit'
import {cleanCookies} from "universal-cookie/es6/utils";

export const userDataSlice = createSlice({
    name: 'userData',
    initialState: {
        firstName: '',
        lastName: '',
        ident: '',
        tid: '',
        deviceId: '',
        state: '',
        navToken: '',
        sessionIndex: '',
        sessionId: '',
        isLocalUser: true,
        roles: []
    },
    reducers: {
        // add your non-async reducers here
        setAll: (state, action) => {
            state.firstName = action.payload.firstName;
            state.lastName = action.payload.lastName;
            state.ident = action.payload.ident;
            state.tid = action.payload.tid;
            state.deviceId = action.payload.deviceId;
            state.state = action.payload.state;
            state.navToken = action.payload.navToken;
            state.sessionIndex = action.payload.sessionIndex;
            state.sessionId = action.payload.sessionId;
            state.isLocalUser = action.payload.isLocalUser;
            state.roles = action.payload.roles;
        }, cleanStore(state, payload){
            cleanCookies();
            localStorage.removeItem("refreshToken");
            localStorage.removeItem("accessToken");
            state.firstName = null;
            state.lastName = null;
            state.ident = null;
            state.tid = null;
            state.deviceId = null;
            state.state = null;
            state.navToken = null;
            state.sessionIndex = null;
            state.sessionId = null;
            state.isLocalUser = null;
            state.roles = null;
        }
    },
    extraReducers: {
        // add your async reducers here
    }
})

// Action creators
export const {
    setAll,
    cleanStore
} = userDataSlice.actions;

export default userDataSlice.reducer;
