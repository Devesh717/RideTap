import { api } from "../../config/apiConfig";

/**
 * GET /balance
 * Response: Double (current wallet balance)
 */
export const getBalanceApi = async () => {
  const response = await api.get("/balance");
  return response.data;
};
