import { api } from "../../config/apiConfig";

/**
 * STATION ADMIN ENDPOINTS (/stations)
 * POST /stations/add  -> StationRequestDTO { name, sequenceNumber }
 * PUT /stations/update -> StationRequestDTO { name, sequenceNumber }
 * DELETE /stations/delete?name=...
 */
export const addStationAdminApi = async (stationRequest) => {
  const response = await api.post("/stations/add", stationRequest);
  return response.data;
};

export const updateStationAdminApi = async (stationRequest) => {
  const response = await api.put("/stations/update", stationRequest);
  return response.data;
};

export const deleteStationAdminApi = async (name) => {
  const response = await api.delete("/stations/delete", {
    params: { name },
  });
  return response.data;
};

/**
 * USER ADMIN ENDPOINTS (/users)
 * GET /users -> List<UserDTO>
 * GET /users/metro-card/{metroCardNumber} -> UserDTO
 * GET /users/email/{email} -> UserDTO
 * DELETE /users/{metroCardNumber} -> String
 * DELETE /users -> String
 */
export const getAllUsersAdminApi = async () => {
  const response = await api.get("/users");
  return response.data;
};

export const getUserByMetroCardAdminApi = async (metroCardNumber) => {
  const response = await api.get(`/users/metro-card/${encodeURIComponent(metroCardNumber)}`);
  return response.data;
};

export const getUserByEmailAdminApi = async (email) => {
  const response = await api.get(`/users/email/${encodeURIComponent(email)}`);
  return response.data;
};

export const deleteUserByMetroCardAdminApi = async (metroCardNumber) => {
  const response = await api.delete(`/users/${encodeURIComponent(metroCardNumber)}`);
  return response.data;
};

export const deleteAllUsersAdminApi = async () => {
  const response = await api.delete("/users");
  return response.data;
};

/**
 * BOOKING HISTORY ADMIN ENDPOINT (/booking-history/all)
 * GET /booking-history/all?metroCardNumber=...&bookingStatus=1|0
 */
export const getAllBookingsAdminApi = async ({ metroCardNumber, bookingStatus } = {}) => {
  const params = {};
  if (metroCardNumber) params.metroCardNumber = metroCardNumber;
  if (bookingStatus !== null && bookingStatus !== undefined && bookingStatus !== "") {
    params.bookingStatus = bookingStatus;
  }
  const response = await api.get("/booking-history/all", { params });
  return response.data;
};
