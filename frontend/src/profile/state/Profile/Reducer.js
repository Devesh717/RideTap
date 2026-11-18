import {
  FETCH_PROFILE_REQUEST,
  FETCH_PROFILE_SUCCESS,
  FETCH_PROFILE_FAILURE,
  CLEAR_PROFILE_ERROR,
} from "./ActionType";

const initialState = {
  profile: null,
  loading: false,
  error: null,
};

export const profileReducer = (state = initialState, action) => {
  switch (action.type) {
    case FETCH_PROFILE_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case FETCH_PROFILE_SUCCESS:
      return {
        ...state,
        loading: false,
        profile: action.payload,
        error: null,
      };

    case FETCH_PROFILE_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    case CLEAR_PROFILE_ERROR:
      return {
        ...state,
        error: null,
      };

    default:
      return state;
  }
};
