import axios from "axios";

export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://ridetap.duckdns.org:8081";

export const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // Preserves HttpSession attributes (email, metroCardNumber, isAdmin) set in AuthController
  headers: {
    "Content-Type": "application/json",
  },
});

// Attach JWT Bearer token on every request if available
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("jwt");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Normalize Spring Boot GlobalExceptionHandler ErrorResponse payloads
export const extractApiError = (error, fallbackMessage = "Something went wrong") => {
  const data = error?.response?.data;
  if (!data) {
    return typeof error?.message === "string" ? error.message : fallbackMessage;
  }

  if (typeof data === "string") {
    return data;
  }

  // Unwrap nested { error: ... } or { message: ... }
  const payload = data.errorMap || data.errors || data.error || data.message || data;

  if (typeof payload === "string") {
    return payload;
  }

  if (typeof payload === "object" && payload !== null) {
    if (typeof payload.error === "string") {
      return payload.error;
    }
    if (typeof payload.message === "string") {
      return payload.message;
    }
    const fieldMessages = Object.entries(payload)
      .filter(([key]) => !["status", "statusCode", "timestamp", "path"].includes(key))
      .map(([field, msg]) =>
        typeof msg === "string" ? `${field}: ${msg}` : `${field}: ${JSON.stringify(msg)}`
      );
    if (fieldMessages.length > 0) {
      return fieldMessages.join(" | ");
    }
  }

  return fallbackMessage;
};
