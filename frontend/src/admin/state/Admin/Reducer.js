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

const initialState = {
  users: [],
  bookings: [],
  loadingUsers: false,
  loadingBookings: false,
  mutatingStation: false,
  deletingCardNumber: null,
  error: null,
  successMessage: null,
};

export const adminReducer = (state = initialState, action) => {
  switch (action.type) {
    case ADMIN_FETCH_USERS_REQUEST:
      return { ...state, loadingUsers: true, error: null };

    case ADMIN_FETCH_USERS_SUCCESS:
      return { ...state, loadingUsers: false, users: action.payload, error: null };

    case ADMIN_FETCH_USERS_FAILURE:
      return { ...state, loadingUsers: false, error: action.payload };

    case ADMIN_DELETE_USER_REQUEST:
      return {
        ...state,
        deletingCardNumber: action.payload,
        error: null,
        successMessage: null,
      };

    case ADMIN_DELETE_USER_SUCCESS:
      return {
        ...state,
        deletingCardNumber: null,
        users: action.payload.deleteAll
          ? []
          : state.users.filter((u) => u.metroCardNumber !== action.payload.metroCardNumber),
        successMessage: action.payload.message,
        error: null,
      };

    case ADMIN_DELETE_USER_FAILURE:
      return {
        ...state,
        deletingCardNumber: null,
        error: action.payload,
      };

    case ADMIN_FETCH_BOOKINGS_REQUEST:
      return { ...state, loadingBookings: true, error: null };

    case ADMIN_FETCH_BOOKINGS_SUCCESS:
      return { ...state, loadingBookings: false, bookings: action.payload, error: null };

    case ADMIN_FETCH_BOOKINGS_FAILURE:
      return { ...state, loadingBookings: false, error: action.payload };

    case ADMIN_STATION_MUTATION_REQUEST:
      return { ...state, mutatingStation: true, error: null, successMessage: null };

    case ADMIN_STATION_MUTATION_SUCCESS:
      return {
        ...state,
        mutatingStation: false,
        successMessage: action.payload,
        error: null,
      };

    case ADMIN_STATION_MUTATION_FAILURE:
      return {
        ...state,
        mutatingStation: false,
        error: action.payload,
      };

    case CLEAR_ADMIN_MESSAGES:
      return { ...state, error: null, successMessage: null };

    default:
      return state;
  }
};
