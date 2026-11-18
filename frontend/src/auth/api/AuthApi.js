import { api } from "../../config/apiConfig";

/**
 * POST /auth/register
 * Request: RegisterUserRequest { name, email, password, phoneNumber, ... }
 * Response: UserDTO (201 CREATED)
 */
export const registerUserApi = async (registerRequest) => {
  const response = await api.post("/auth/register", registerRequest);
  return response.data;
};

/**
 * POST /auth/login
 * Request: LoginRequest { email, password }
 * Response: LoginResponse { message, token } (201 CREATED)
 */
export const loginUserApi = async (loginRequest) => {
  const response = await api.post("/auth/login", loginRequest);
  return response.data;
};
