import {
  CREATE_BOOKING_REQUEST,
  CREATE_BOOKING_SUCCESS,
  CREATE_BOOKING_FAILURE,
  CANCEL_BOOKING_REQUEST,
  CANCEL_BOOKING_SUCCESS,
  CANCEL_BOOKING_FAILURE,
  FETCH_USER_BOOKINGS_REQUEST,
  FETCH_USER_BOOKINGS_SUCCESS,
  FETCH_USER_BOOKINGS_FAILURE,
  FETCH_BOOKING_BY_ID_REQUEST,
  FETCH_BOOKING_BY_ID_SUCCESS,
  FETCH_BOOKING_BY_ID_FAILURE,
  CLEAR_BOOKING_MESSAGES,
} from "./ActionType";

const initialState = {
  bookings: [],
  currentBooking: null,
  latestBookedTicket: null,
  loading: false,
  bookingInProgress: false,
  cancellingId: null,
  error: null,
  insufficientBalanceError: false,
  successMessage: null,
};

export const bookingReducer = (state = initialState, action) => {
  switch (action.type) {
    case CREATE_BOOKING_REQUEST:
      return {
        ...state,
        bookingInProgress: true,
        error: null,
        insufficientBalanceError: false,
        successMessage: null,
      };

    case CREATE_BOOKING_SUCCESS:
      return {
        ...state,
        bookingInProgress: false,
        latestBookedTicket: action.payload,
        currentBooking: action.payload,
        bookings: [action.payload, ...state.bookings],
        error: null,
        insufficientBalanceError: false,
        successMessage: `Ticket #${action.payload.id} booked successfully!`,
      };

    case CREATE_BOOKING_FAILURE:
      return {
        ...state,
        bookingInProgress: false,
        error: action.payload.message,
        insufficientBalanceError: Boolean(action.payload.isInsufficient),
      };

    case CANCEL_BOOKING_REQUEST:
      return {
        ...state,
        cancellingId: action.payload,
        error: null,
        successMessage: null,
      };

    case CANCEL_BOOKING_SUCCESS:
      return {
        ...state,
        cancellingId: null,
        currentBooking:
          state.currentBooking?.id === action.payload.id
            ? action.payload
            : state.currentBooking,
        bookings: state.bookings.map((b) =>
          b.id === action.payload.id ? action.payload : b
        ),
        successMessage: `Booking #${action.payload.id} cancelled and fare refunded to your Metro Card.`,
        error: null,
      };

    case CANCEL_BOOKING_FAILURE:
      return {
        ...state,
        cancellingId: null,
        error: action.payload,
      };

    case FETCH_USER_BOOKINGS_REQUEST:
    case FETCH_BOOKING_BY_ID_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case FETCH_USER_BOOKINGS_SUCCESS:
      return {
        ...state,
        loading: false,
        bookings: action.payload,
        error: null,
      };

    case FETCH_BOOKING_BY_ID_SUCCESS:
      return {
        ...state,
        loading: false,
        currentBooking: action.payload,
        error: null,
      };

    case FETCH_USER_BOOKINGS_FAILURE:
    case FETCH_BOOKING_BY_ID_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    case CLEAR_BOOKING_MESSAGES:
      return {
        ...state,
        error: null,
        insufficientBalanceError: false,
        successMessage: null,
      };

    default:
      return state;
  }
};
