import {
  REGISTER_REQUEST,
  REGISTER_SUCCESS,
  REGISTER_FAILURE,
  LOGIN_REQUEST,
  LOGIN_SUCCESS,
  LOGIN_FAILURE,
  LOGOUT,
  CLEAR_AUTH_ERROR,
} from "./ActionType";

const storedJwt = localStorage.getItem("jwt");
const storedEmail = localStorage.getItem("userEmail");
const storedIsAdmin = localStorage.getItem("isAdmin") === "true";

const initialState = {
  user: storedEmail ? { email: storedEmail } : null,
  jwt: storedJwt || null,
  isAdmin: storedIsAdmin,
  isAuthenticated: Boolean(storedJwt),
  loading: false,
  error: null,
  successMessage: null,
};

export const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case REGISTER_REQUEST:
    case LOGIN_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
        successMessage: null,
      };

    case REGISTER_SUCCESS:
      return {
        ...state,
        loading: false,
        user: action.payload,
        error: null,
        successMessage: "Account created! Please log in with your credentials.",
      };

    case LOGIN_SUCCESS:
      return {
        ...state,
        loading: false,
        jwt: action.payload.jwt,
        isAuthenticated: true,
        isAdmin: action.payload.isAdmin,
        user: {
          ...(state.user || {}),
          email: action.payload.email,
        },
        error: null,
        successMessage: action.payload.message || "User login successfully",
      };

    case REGISTER_FAILURE:
    case LOGIN_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    case LOGOUT:
      return {
        user: null,
        jwt: null,
        isAdmin: false,
        isAuthenticated: false,
        loading: false,
        error: null,
        successMessage: null,
      };

    case CLEAR_AUTH_ERROR:
      return {
        ...state,
        error: null,
        successMessage: null,
      };

    default:
      return state;
  }
};
