import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { toggleAiWidget } from "../../../state/Ai/Action";
import ChatWindow from "./ChatWindow";

const FloatingAiWidget = () => {
  const dispatch = useDispatch();
  const location = useLocation();
  const { widgetOpen } = useSelector((state) => state.ai);
  const { isAuthenticated } = useSelector((state) => state.auth);

  // Hide floating widget on full-screen AI page or unauthenticated auth pages
  if (!isAuthenticated || location.pathname === "/ai-assistant") {
    return null;
  }

  return (
    <div className="fixed bottom-5 right-5 z-40 flex flex-col items-end">
      {widgetOpen && (
        <div className="mb-3 w-[350px] sm:w-[390px]">
          <ChatWindow compact onClose={() => dispatch(toggleAiWidget(false))} />
        </div>
      )}

      <button
        type="button"
        onClick={() => dispatch(toggleAiWidget())}
        aria-label="Toggle RideTap AI Assistant"
        className="flex items-center gap-2 px-4 py-3 rounded-full bg-slate-900 hover:bg-teal-700 text-white font-bold text-xs shadow-xl border border-slate-700 transition cursor-pointer"
      >
        <span className="w-2.5 h-2.5 rounded-full bg-teal-400 animate-pulse" />
        <span>{widgetOpen ? "Close AI Concierge" : "Ask RideTap AI"}</span>
      </button>
    </div>
  );
};

export default FloatingAiWidget;
