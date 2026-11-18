import {
  SEND_AI_MESSAGE_REQUEST,
  SEND_AI_MESSAGE_SUCCESS,
  SEND_AI_MESSAGE_FAILURE,
  CLEAR_AI_CHAT,
  TOGGLE_AI_WIDGET,
} from "./ActionType";
import { sendAiChatMessageApi } from "../../api/AiApi";
import { extractApiError } from "../../../config/apiConfig";

export const sendAiMessage = (messageText) => async (dispatch) => {
  const trimmed = messageText?.trim();
  if (!trimmed) return;

  const userMsg = {
    id: `user-${Date.now()}`,
    sender: "user",
    text: trimmed,
    timestamp: new Date().toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    }),
  };

  dispatch({
    type: SEND_AI_MESSAGE_REQUEST,
    payload: userMsg,
  });

  try {
    const data = await sendAiChatMessageApi(trimmed);
    const aiMsg = {
      id: `ai-${Date.now()}`,
      sender: "ai",
      text: data?.response || "No response received from RideTap AI.",
      timestamp: new Date().toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      }),
    };

    dispatch({
      type: SEND_AI_MESSAGE_SUCCESS,
      payload: aiMsg,
    });
  } catch (error) {
    dispatch({
      type: SEND_AI_MESSAGE_FAILURE,
      payload: extractApiError(error, "RideTap AI service is currently unavailable."),
    });
  }
};

export const clearAiChat = () => ({
  type: CLEAR_AI_CHAT,
});

export const toggleAiWidget = (isOpen) => ({
  type: TOGGLE_AI_WIDGET,
  payload: isOpen,
});
