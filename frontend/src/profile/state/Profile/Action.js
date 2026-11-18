import {
  FETCH_PROFILE_REQUEST,
  FETCH_PROFILE_SUCCESS,
  FETCH_PROFILE_FAILURE,
  CLEAR_PROFILE_ERROR,
} from "./ActionType";
import { getUserProfileApi } from "../../api/ProfileApi";
import { extractApiError } from "../../../config/apiConfig";

export const fetchUserProfile = () => async (dispatch) => {
  dispatch({ type: FETCH_PROFILE_REQUEST });
  try {
    const userDTO = await getUserProfileApi();
    dispatch({
      type: FETCH_PROFILE_SUCCESS,
      payload: userDTO,
    });
  } catch (error) {
    dispatch({
      type: FETCH_PROFILE_FAILURE,
      payload: extractApiError(error, "Unable to load commuter profile."),
    });
  }
};

export const clearProfileError = () => ({
  type: CLEAR_PROFILE_ERROR,
});
