import {
  ADMIN_FETCH_USERS_REQUEST,
  ADMIN_FETCH_USERS_SUCCESS,
  ADMIN_FETCH_USERS_FAILURE,
  ADMIN_DELETE_USER_REQUEST,
  ADMIN_DELETE_USER_SUCCESS,
  ADMIN_DELETE_USER_FAILURE,
  ADMIN_FETCH_BOOKINGS_REQUEST,
  ADMIN_FETCH_BOOKINGS_SUCCESS,
  ADMIN_FETCH_BOOKINGS_FAILURE,
  ADMIN_STATION_MUTATION_REQUEST,
  ADMIN_STATION_MUTATION_SUCCESS,
  ADMIN_STATION_MUTATION_FAILURE,
  CLEAR_ADMIN_MESSAGES,
} from "./ActionType";
import {
  getAllUsersAdminApi,
  getUserByMetroCardAdminApi,
  getUserByEmailAdminApi,
  deleteUserByMetroCardAdminApi,
  deleteAllUsersAdminApi,
  getAllBookingsAdminApi,
  addStationAdminApi,
  updateStationAdminApi,
  deleteStationAdminApi,
} from "../../api/AdminApi";
import { fetchAllStations } from "../../../station/state/Station/Action";
import { extractApiError } from "../../../config/apiConfig";

// --- USERS ---
export const fetchAdminUsers = () => async (dispatch) => {
  dispatch({ type: ADMIN_FETCH_USERS_REQUEST });
  try {
    const users = await getAllUsersAdminApi();
    dispatch({
      type: ADMIN_FETCH_USERS_SUCCESS,
      payload: Array.isArray(users) ? users : [],
    });
  } catch (error) {
    dispatch({
      type: ADMIN_FETCH_USERS_FAILURE,
      payload: extractApiError(error, "Failed to fetch registered users."),
    });
  }
};

export const searchAdminUser = (query, mode = "card") => async (dispatch) => {
  const trimmed = query?.trim();
  if (!trimmed) {
    return dispatch(fetchAdminUsers());
  }

  dispatch({ type: ADMIN_FETCH_USERS_REQUEST });
  try {
    const user =
      mode === "email"
        ? await getUserByEmailAdminApi(trimmed)
        : await getUserByMetroCardAdminApi(trimmed);
    dispatch({
      type: ADMIN_FETCH_USERS_SUCCESS,
      payload: user ? [user] : [],
    });
  } catch (error) {
    dispatch({
      type: ADMIN_FETCH_USERS_FAILURE,
      payload: extractApiError(error, "No user found matching your search."),
    });
  }
};

export const deleteAdminUser = (metroCardNumber) => async (dispatch) => {
  dispatch({ type: ADMIN_DELETE_USER_REQUEST, payload: metroCardNumber });
  try {
    const msg = await deleteUserByMetroCardAdminApi(metroCardNumber);
    dispatch({
      type: ADMIN_DELETE_USER_SUCCESS,
      payload: {
        metroCardNumber,
        message: typeof msg === "string" ? msg : "Successfully deleted user",
      },
    });
  } catch (error) {
    dispatch({
      type: ADMIN_DELETE_USER_FAILURE,
      payload: extractApiError(error, "Failed to delete user."),
    });
  }
};

export const deleteAllAdminUsers = () => async (dispatch) => {
  dispatch({ type: ADMIN_DELETE_USER_REQUEST, payload: "ALL" });
  try {
    const msg = await deleteAllUsersAdminApi();
    dispatch({
      type: ADMIN_DELETE_USER_SUCCESS,
      payload: {
        deleteAll: true,
        message: typeof msg === "string" ? msg : "Successfully deleted all users",
      },
    });
  } catch (error) {
    dispatch({
      type: ADMIN_DELETE_USER_FAILURE,
      payload: extractApiError(error, "Failed to delete all users."),
    });
  }
};

// --- BOOKINGS ---
export const fetchAdminBookings = (filters = {}) => async (dispatch) => {
  dispatch({ type: ADMIN_FETCH_BOOKINGS_REQUEST });
  try {
    const bookings = await getAllBookingsAdminApi(filters);
    const sorted = Array.isArray(bookings)
      ? [...bookings].sort((a, b) => (b.id ?? 0) - (a.id ?? 0))
      : [];
    dispatch({
      type: ADMIN_FETCH_BOOKINGS_SUCCESS,
      payload: sorted,
    });
  } catch (error) {
    dispatch({
      type: ADMIN_FETCH_BOOKINGS_FAILURE,
      payload: extractApiError(error, "Failed to fetch system bookings."),
    });
  }
};

// --- STATIONS ---
export const addAdminStation = (stationRequest, onSuccess) => async (dispatch) => {
  dispatch({ type: ADMIN_STATION_MUTATION_REQUEST });
  try {
    const msg = await addStationAdminApi(stationRequest);
    dispatch({
      type: ADMIN_STATION_MUTATION_SUCCESS,
      payload: typeof msg === "string" ? msg : "Station added successfully.",
    });
    dispatch(fetchAllStations());
    if (onSuccess) onSuccess();
  } catch (error) {
    dispatch({
      type: ADMIN_STATION_MUTATION_FAILURE,
      payload: extractApiError(error, "Failed to add station."),
    });
  }
};

export const updateAdminStation = (stationRequest, onSuccess) => async (dispatch) => {
  dispatch({ type: ADMIN_STATION_MUTATION_REQUEST });
  try {
    const msg = await updateStationAdminApi(stationRequest);
    dispatch({
      type: ADMIN_STATION_MUTATION_SUCCESS,
      payload: typeof msg === "string" ? msg : "Station updated successfully.",
    });
    dispatch(fetchAllStations());
    if (onSuccess) onSuccess();
  } catch (error) {
    dispatch({
      type: ADMIN_STATION_MUTATION_FAILURE,
      payload: extractApiError(error, "Failed to update station."),
    });
  }
};

export const deleteAdminStation = (name) => async (dispatch) => {
  dispatch({ type: ADMIN_STATION_MUTATION_REQUEST });
  try {
    const msg = await deleteStationAdminApi(name);
    dispatch({
      type: ADMIN_STATION_MUTATION_SUCCESS,
      payload: typeof msg === "string" ? msg : `Deleted station "${name}".`,
    });
    dispatch(fetchAllStations());
  } catch (error) {
    dispatch({
      type: ADMIN_STATION_MUTATION_FAILURE,
      payload: extractApiError(error, "Failed to delete station."),
    });
  }
};

export const clearAdminMessages = () => ({
  type: CLEAR_ADMIN_MESSAGES,
});
