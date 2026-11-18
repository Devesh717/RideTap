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

const initialState = {
  stations: [],
  selectedStation: null,
  loading: false,
  error: null,
  routeResult: null,
  calculatingRoute: false,
  routeError: null,
  fareResult: null, // { fare, originStation, destinationStation }
  fareLoading: false,
  fareError: null,
};

export const stationReducer = (state = initialState, action) => {
  switch (action.type) {
    case FETCH_STATIONS_REQUEST:
    case FETCH_STATION_BY_ID_REQUEST:
      return { ...state, loading: true, error: null };

    case FETCH_STATIONS_SUCCESS:
      return { ...state, loading: false, stations: action.payload, error: null };

    case FETCH_STATION_BY_ID_SUCCESS:
      return { ...state, loading: false, selectedStation: action.payload, error: null };

    case FETCH_STATIONS_FAILURE:
    case FETCH_STATION_BY_ID_FAILURE:
      return { ...state, loading: false, error: action.payload };

    case FETCH_ROUTE_REQUEST:
      return { ...state, calculatingRoute: true, routeError: null };

    case FETCH_ROUTE_SUCCESS:
      return {
        ...state,
        calculatingRoute: false,
        routeResult: action.payload,
        routeError: null,
      };

    case FETCH_ROUTE_FAILURE:
      return {
        ...state,
        calculatingRoute: false,
        routeError: action.payload,
      };

    case CLEAR_ROUTE_RESULT:
      return { ...state, routeResult: null, routeError: null };

    case CALCULATE_FARE_REQUEST:
      return { ...state, fareLoading: true, fareError: null };

    case CALCULATE_FARE_SUCCESS:
      return {
        ...state,
        fareLoading: false,
        fareResult: action.payload,
        fareError: null,
      };

    case CALCULATE_FARE_FAILURE:
      return {
        ...state,
        fareLoading: false,
        fareResult: null,
        fareError: action.payload,
      };

    case CLEAR_FARE:
      return {
        ...state,
        fareResult: null,
        fareError: null,
      };

    default:
      return state;
  }
};