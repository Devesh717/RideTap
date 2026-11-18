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
import {
  createBookingApi,
  cancelBookingApi,
  getUserBookingsApi,
  getBookingByIdApi,
} from "../../api/BookingApi";
import { fetchBalance } from "../../../wallet/state/Wallet/Action";
import { extractApiError } from "../../../config/apiConfig";

export const createBooking = (bookingRequest, onSuccess, onInsufficientBalance) => async (dispatch) => {
  dispatch({ type: CREATE_BOOKING_REQUEST });
  try {
    const bookingDTO = await createBookingApi(bookingRequest);
    dispatch({
      type: CREATE_BOOKING_SUCCESS,
      payload: bookingDTO,
    });
    // Sync wallet balance immediately after fare deduction
    dispatch(fetchBalance());
    if (onSuccess) onSuccess(bookingDTO);
  } catch (error) {
    const message = extractApiError(error, "Failed to book ride.");
    const isInsufficient = message.toLowerCase().includes("insufficient balance");
    dispatch({
      type: CREATE_BOOKING_FAILURE,
      payload: { message, isInsufficient },
    });
    if (isInsufficient && onInsufficientBalance) {
      onInsufficientBalance(message);
    }
  }
};

export const cancelBooking = (bookingId, onSuccess) => async (dispatch) => {
  dispatch({ type: CANCEL_BOOKING_REQUEST, payload: bookingId });
  try {
    const updatedBookingDTO = await cancelBookingApi(bookingId);
    dispatch({
      type: CANCEL_BOOKING_SUCCESS,
      payload: updatedBookingDTO,
    });
    // Sync wallet balance immediately after fare refund
    dispatch(fetchBalance());
    if (onSuccess) onSuccess(updatedBookingDTO);
  } catch (error) {
    dispatch({
      type: CANCEL_BOOKING_FAILURE,
      payload: extractApiError(error, "Unable to cancel booking."),
    });
  }
};

export const fetchUserBookings = (bookingStatus = null) => async (dispatch) => {
  dispatch({ type: FETCH_USER_BOOKINGS_REQUEST });
  try {
    const bookings = await getUserBookingsApi(bookingStatus);
    const sorted = Array.isArray(bookings)
      ? [...bookings].sort((a, b) => (b.id ?? 0) - (a.id ?? 0))
      : [];
    dispatch({
      type: FETCH_USER_BOOKINGS_SUCCESS,
      payload: sorted,
    });
  } catch (error) {
    dispatch({
      type: FETCH_USER_BOOKINGS_FAILURE,
      payload: extractApiError(error, "Failed to load booking history."),
    });
  }
};

export const fetchBookingById = (id) => async (dispatch) => {
  dispatch({ type: FETCH_BOOKING_BY_ID_REQUEST });
  try {
    const bookingDTO = await getBookingByIdApi(id);
    dispatch({
      type: FETCH_BOOKING_BY_ID_SUCCESS,
      payload: bookingDTO,
    });
  } catch (error) {
    dispatch({
      type: FETCH_BOOKING_BY_ID_FAILURE,
      payload: extractApiError(error, "Unable to fetch ticket details."),
    });
  }
};

export const clearBookingMessages = () => ({
  type: CLEAR_BOOKING_MESSAGES,
});
