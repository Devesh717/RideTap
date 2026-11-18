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
import { loginUserApi, registerUserApi } from "../../api/AuthApi";
import { extractApiError } from "../../../config/apiConfig";

// Decode JWT payload safely to extract authorities/roles
const parseJwtPayload = (token) => {
  try {
    const base64Url = token.split(".")[1];
    if (!base64Url) return null;
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

export const register = (userData, onSuccess) => async (dispatch) => {
  dispatch({ type: REGISTER_REQUEST });
  try {
    const userDTO = await registerUserApi(userData);
    dispatch({ type: REGISTER_SUCCESS, payload: userDTO });
    if (onSuccess) onSuccess(userDTO);
  } catch (error) {
    const message = extractApiError(error, "Registration failed. Please check your details.");
    dispatch({ type: REGISTER_FAILURE, payload: message });
  }
};

export const login = (credentials, onSuccess) => async (dispatch) => {
  dispatch({ type: LOGIN_REQUEST });
  try {
    const data = await loginUserApi(credentials); // { message, token }
    if (data?.token) {
      localStorage.setItem("jwt", data.token);
      localStorage.setItem("userEmail", credentials.email);
    }

    const decoded = data?.token ? parseJwtPayload(data.token) : null;
    const roles = decoded?.authorities || decoded?.roles || decoded?.role || [];
    const isAdmin = Array.isArray(roles)
      ? roles.includes("ADMIN") || roles.includes("ROLE_ADMIN")
      : String(roles).includes("ADMIN");

    localStorage.setItem("isAdmin", String(isAdmin));

    dispatch({
      type: LOGIN_SUCCESS,
      payload: {
        jwt: data.token,
        message: data.message,
        email: credentials.email,
        isAdmin,
      },
    });

    if (onSuccess) onSuccess(data);
  } catch (error) {
    const message =
      error?.response?.status === 401 || error?.response?.status === 403
        ? "Invalid email or password."
        : extractApiError(error, "Login failed. Please try again.");
    dispatch({ type: LOGIN_FAILURE, payload: message });
  }
};

export const logout = () => (dispatch) => {
  localStorage.removeItem("jwt");
  localStorage.removeItem("userEmail");
  localStorage.removeItem("isAdmin");
  dispatch({ type: LOGOUT });
};

export const clearAuthError = () => ({
  type: CLEAR_AUTH_ERROR,
});
