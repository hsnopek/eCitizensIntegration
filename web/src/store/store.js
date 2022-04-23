import { configureStore, getDefaultMiddleware } from '@reduxjs/toolkit'
import navigationBarReducer from '../slices/navigationBarSlice';
import userDataReducer from '../slices/userDataSlice';
import { combineReducers } from "redux"
import {
    persistStore,
    persistReducer,
    FLUSH,
    REHYDRATE,
    PAUSE,
    PERSIST,
    PURGE,
    REGISTER,
} from "redux-persist"
import storage from "redux-persist/lib/storage"

const rootReducer = combineReducers({
    navigationBar: navigationBarReducer,
    userData: userDataReducer
})

const persistConfig = {
    key: "root",
    version: 1,
    storage,
}
const persistedReducer = persistReducer(persistConfig, rootReducer);

const store = configureStore({
    reducer: persistedReducer,
    middleware: getDefaultMiddleware({
        serializableCheck: {
            ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
        },
    }),
})
let persistor = persistStore(store)

export { store, persistor }
