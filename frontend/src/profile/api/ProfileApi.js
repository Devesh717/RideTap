import { api } from "../../config/apiConfig";

/**
 * GET /profile
 * Uses HttpSession attribute "email"
 * Response: UserDTO { metroCardNumber, holderName, email, mobileNumber, balance, createdAt, roles, bookings }
 */
export const getUserProfileApi = async () => {
  const response = await api.get("/profile");
  return response.data;
};
