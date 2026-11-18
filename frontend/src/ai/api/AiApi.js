import { api } from "../../config/apiConfig";

/**
 * POST /api/ai
 * Request: AiRequestDTO { message }
 * Response: AiResponseDTO { response } (200 OK)
 */
export const sendAiChatMessageApi = async (message) => {
  const response = await api.post("/api/ai", { message });
  return response.data;
};
