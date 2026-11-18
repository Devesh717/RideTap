import {
  SEND_AI_MESSAGE_REQUEST,
  SEND_AI_MESSAGE_SUCCESS,
  SEND_AI_MESSAGE_FAILURE,
  CLEAR_AI_CHAT,
  TOGGLE_AI_WIDGET,
} from "./ActionType";

const WELCOME_MESSAGE = {
  id: "ai-welcome",
  sender: "ai",
  text: "Hi! I'm your RideTap AI Transit Concierge. Ask me to calculate fares between stations, check metro station info, book a ticket, or review your booking history!",
  timestamp: new Date().toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit",
  }),
};

const initialState = {
  messages: [WELCOME_MESSAGE],
  loading: false,
  error: null,
  widgetOpen: false,
};

export const aiReducer = (state = initialState, action) => {
  switch (action.type) {
    case SEND_AI_MESSAGE_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
        messages: [...state.messages, action.payload],
      };

    case SEND_AI_MESSAGE_SUCCESS:
      return {
        ...state,
        loading: false,
        messages: [...state.messages, action.payload],
        error: null,
      };

    case SEND_AI_MESSAGE_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };

    case CLEAR_AI_CHAT:
      return {
        ...state,
        messages: [
          {
            ...WELCOME_MESSAGE,
            id: `ai-welcome-${Date.now()}`,
            timestamp: new Date().toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            }),
          },
        ],
        error: null,
      };

    case TOGGLE_AI_WIDGET:
      return {
        ...state,
        widgetOpen:
          typeof action.payload === "boolean" ? action.payload : !state.widgetOpen,
      };

    default:
      return state;
  }
};
