import {
  FETCH_BALANCE_REQUEST,
  FETCH_BALANCE_SUCCESS,
  FETCH_BALANCE_FAILURE,
  ADD_BALANCE_REQUEST,
  ADD_BALANCE_SUCCESS,
  ADD_BALANCE_FAILURE,
  CLEAR_WALLET_MESSAGES,
} from "./ActionType";

const initialState = {
  balance: null,
  loading: false,
  recharging: false,
  error: null,
  successMessage: null,
  recentTopUps: [], // Local session log of recharges for immediate UX feedback
};

export const walletReducer = (state = initialState, action) => {
  switch (action.type) {
    case FETCH_BALANCE_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };

    case FETCH_BALANCE_SUCCESS:
      return {
        ...state,
        loading: false,
        balance: action.payload,
        error: null,
      };

    case FETCH_BALANCE_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    case ADD_BALANCE_REQUEST:
      return {
        ...state,
        recharging: true,
        error: null,
        successMessage: null,
      };

    case ADD_BALANCE_SUCCESS:
      return {
        ...state,
        recharging: false,
        successMessage: action.payload.message,
        error: null,
        recentTopUps: [
          {
            id: Date.now(),
            amount: action.payload.addedAmount,
            timestamp: new Date().toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            }),
          },
          ...state.recentTopUps,
        ],
      };

    case ADD_BALANCE_FAILURE:
      return {
        ...state,
        recharging: false,
        error: action.payload,
      };

    case CLEAR_WALLET_MESSAGES:
      return {
        ...state,
        error: null,
        successMessage: null,
      };

    default:
      return state;
  }
};
