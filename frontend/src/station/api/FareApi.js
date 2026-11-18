import { api } from "../../config/apiConfig";

export const getFareApi = async (originStation, destinationStation) => {
  const response = await api.get("/fare", {
    params: { originStation, destinationStation },
  });
  return response.data;
};