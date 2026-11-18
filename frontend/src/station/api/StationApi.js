import { api } from "../../config/apiConfig";

export const getAllStationsApi = async () => {
  const response = await api.get("/stations");
  return response.data;
};

export const getStationByIdApi = async (id) => {
  const response = await api.get(`/stations/${id}`);
  return response.data;
};

export const searchStationsApi = async ({ query = "", line = "" } = {}) => {
  const params = {};
  if (query) params.query = query;
  if (line && line !== "ALL") params.line = line;
  const response = await api.get("/stations/search", { params });
  return response.data;
};

export const getShortestRouteApi = async (originStation, destinationStation) => {
  const response = await api.get("/stations/route", {
    params: { originStation, destinationStation },
  });
  return response.data;
};