import { legacy_createStore, applyMiddleware, combineReducers } from "redux";
import { thunk } from "redux-thunk";
import { authReducer } from "./auth/state/Auth/Reducer";
import { stationReducer } from "./station/state/Station/Reducer";
import { walletReducer } from "./wallet/state/Wallet/Reducer";
import { bookingReducer } from "./booking/state/Booking/Reducer";
import { aiReducer } from "./ai/state/Ai/Reducer";
import { profileReducer } from "./profile/state/Profile/Reducer";
import { adminReducer } from "./admin/state/Admin/Reducer";

const rootReducers = combineReducers({
  auth: authReducer,
  station: stationReducer,
  wallet: walletReducer,
  booking: bookingReducer,
  ai: aiReducer,
  profile: profileReducer,
  admin: adminReducer,
});

export const store = legacy_createStore(rootReducers, applyMiddleware(thunk));