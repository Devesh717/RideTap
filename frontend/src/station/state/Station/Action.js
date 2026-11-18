import {
  FETCH_STATIONS_REQUEST,
  FETCH_STATIONS_SUCCESS,
  FETCH_STATIONS_FAILURE,
  FETCH_STATION_BY_ID_REQUEST,
  FETCH_STATION_BY_ID_SUCCESS,
  FETCH_STATION_BY_ID_FAILURE,
  FETCH_ROUTE_REQUEST,
  FETCH_ROUTE_SUCCESS,
  FETCH_ROUTE_FAILURE,
  CLEAR_ROUTE_RESULT,
  CALCULATE_FARE_REQUEST,
  CALCULATE_FARE_SUCCESS,
  CALCULATE_FARE_FAILURE,
  CLEAR_FARE,
} from "./ActionType";
import {
  getAllStationsApi,
  getStationByIdApi,
  searchStationsApi,
  getShortestRouteApi,
} from "../../api/StationApi";
import { getFareApi } from "../../api/FareApi";
import { extractApiError } from "../../../config/apiConfig";

export const fetchAllStations = () => async (dispatch) => {
  dispatch({ type: FETCH_STATIONS_REQUEST });
  try {
    const stations = await getAllStationsApi();
    const sorted = Array.isArray(stations)
      ? [...stations].sort((a, b) => (a.sequenceNumber ?? 0) - (b.sequenceNumber ?? 0))
      : [];
    dispatch({
      type: FETCH_STATIONS_SUCCESS,
      payload: sorted,
    });
  } catch (error) {
    dispatch({
      type: FETCH_STATIONS_FAILURE,
      payload: extractApiError(error, "Unable to load Delhi Metro stations."),
    });
  }
};

export const fetchStationById = (id) => async (dispatch) => {
  dispatch({ type: FETCH_STATION_BY_ID_REQUEST });
  try {
    const station = await getStationByIdApi(id);
    dispatch({ type: FETCH_STATION_BY_ID_SUCCESS, payload: station });
  } catch (error) {
    dispatch({
      type: FETCH_STATION_BY_ID_FAILURE,
      payload: extractApiError(error, "Failed to load station details."),
    });
  }
};

export const searchMetroStations = (filters) => async (dispatch) => {
  dispatch({ type: FETCH_STATIONS_REQUEST });
  try {
    const stations = await searchStationsApi(filters);
    dispatch({
      type: FETCH_STATIONS_SUCCESS,
      payload: Array.isArray(stations) ? stations : [],
    });
  } catch (error) {
    dispatch({
      type: FETCH_STATIONS_FAILURE,
      payload: extractApiError(error, "Search failed."),
    });
  }
};

export const calculateShortestRoute = (originStation, destinationStation) => async (dispatch) => {
  if (!originStation || !destinationStation) return;
  dispatch({ type: FETCH_ROUTE_REQUEST });
  try {
    const routeResult = await getShortestRouteApi(originStation, destinationStation);
    dispatch({
      type: FETCH_ROUTE_SUCCESS,
      payload: routeResult,
    });
  } catch (error) {
    dispatch({
      type: FETCH_ROUTE_FAILURE,
      payload: extractApiError(error, "Unable to compute shortest metro route."),
    });
  }
};

export const clearRouteResult = () => ({
  type: CLEAR_ROUTE_RESULT,
});

export const calculateFare = (originStation, destinationStation) => async (dispatch) => {
  if (!originStation || !destinationStation) return;
  dispatch({ type: CALCULATE_FARE_REQUEST });
  try {
    const fare = await getFareApi(originStation, destinationStation);
    dispatch({
      type: CALCULATE_FARE_SUCCESS,
      payload: {
        fare,
        originStation,
        destinationStation,
      },
    });
  } catch (error) {
    dispatch({
      type: CALCULATE_FARE_FAILURE,
      payload: extractApiError(error, "Unable to calculate fare for selected route."),
    });
  }
};

export const clearFare = () => ({
  type: CLEAR_FARE,
});