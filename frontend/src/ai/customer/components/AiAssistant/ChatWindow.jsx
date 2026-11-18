import React, { useState, useRef, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { sendAiMessage, clearAiChat } from "../../../state/Ai/Action";
import QuickPromptChips from "./QuickPromptChips";

const ChatWindow = ({ compact = false, onClose }) => {
  const dispatch = useDispatch();
  const { messages, loading, error } = useSelector((state) => state.ai);
  const [input, setInput] = useState("");
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  const handleSend = (e) => {
    e.preventDefault();
    if (!input.trim() || loading) return;
    dispatch(sendAiMessage(input));
    setInput("");
  };

  const handleQuickPrompt = (promptText) => {
    if (loading) return;
    dispatch(sendAiMessage(promptText));
  };

  return (
    <div
      className={`flex flex-col bg-white border border-slate-200 overflow-hidden ${
        compact ? "h-[500px] rounded-2xl shadow-2xl" : "h-[640px] rounded-3xl shadow-sm"
      }`}
    >
      {/* Header */}
      <div className="bg-gradient-to-r from-slate-900 to-teal-800 text-white px-5 py-4 flex items-center justify-between gap-3 shrink-0">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-xl bg-teal-500/20 border border-teal-400/30 flex items-center justify-center text-xs font-extrabold text-teal-300">
            AI
          </div>
          <div>
            <h3 className="text-sm font-bold leading-tight">RideTap AI Assistant</h3>
            <p className="text-[11px] text-teal-200">
              Online &bull; Fares, Stations &amp; Ticket Support
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => dispatch(clearAiChat())}
            title="Clear Conversation"
            className="px-2.5 py-1 rounded-lg bg-white/10 hover:bg-white/20 text-[11px] font-semibold text-white transition cursor-pointer border border-white/15"
          >
            Clear
          </button>
          {onClose && (
            <button
              type="button"
              onClick={onClose}
              aria-label="Minimize AI Chat"
              className="text-white/80 hover:text-white text-xl leading-none bg-transparent border-0 cursor-pointer px-1"
            >
              &times;
            </button>
          )}
        </div>
      </div>

      {/* Messages Feed */}
      <div className="flex-1 overflow-y-auto p-4 sm:p-5 space-y-4 bg-slate-50/70">
        {messages.map((msg) => {
          const isUser = msg.sender === "user";
          return (
            <div
              key={msg.id}
              className={`flex flex-col ${isUser ? "items-end" : "items-start"}`}
            >
              <div
                className={`max-w-[85%] rounded-2xl px-4 py-3 text-sm leading-relaxed whitespace-pre-wrap ${
                  isUser
                    ? "bg-teal-600 text-white rounded-br-sm shadow-xs"
                    : "bg-white text-slate-800 border border-slate-200 rounded-bl-sm shadow-2xs"
                }`}
              >
                {msg.text}
              </div>
              <span className="text-[10px] text-slate-400 mt-1 px-1">
                {isUser ? "You" : "RideTap AI"} &bull; {msg.timestamp}
              </span>
            </div>
          );
        })}

        {loading && (
          <div className="flex items-start">
            <div className="bg-white border border-slate-200 rounded-2xl rounded-bl-sm px-4 py-3 text-xs text-slate-500 flex items-center gap-1.5 shadow-2xs">
              <span className="w-2 h-2 rounded-full bg-teal-500 animate-bounce" />
              <span className="w-2 h-2 rounded-full bg-teal-500 animate-bounce [animation-delay:150ms]" />
              <span className="w-2 h-2 rounded-full bg-teal-500 animate-bounce [animation-delay:300ms]" />
              <span className="ml-1 font-medium">Thinking...</span>
            </div>
          </div>
        )}

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-3.5 py-2.5 rounded-xl text-xs">
            {error}
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Quick Prompts Bar */}
      <div className="px-4 py-2.5 bg-white border-t border-slate-100 shrink-0">
        <QuickPromptChips
          onSelectPrompt={handleQuickPrompt}
          disabled={loading}
          compact={compact}
        />
      </div>

      {/* Input Form */}
      <form
        onSubmit={handleSend}
        className="p-3.5 bg-white border-t border-slate-200 flex items-center gap-2 shrink-0"
      >
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Ask about fares, stations, or tickets..."
          disabled={loading}
          className="flex-1 px-3.5 py-2.5 border border-slate-300 rounded-xl text-sm text-slate-900 focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20 disabled:bg-slate-50"
        />
        <button
          type="submit"
          disabled={loading || !input.trim()}
          className="px-4 py-2.5 bg-teal-600 hover:bg-teal-700 disabled:opacity-50 text-white font-semibold text-sm rounded-xl transition cursor-pointer border-0 shrink-0"
        >
          Send
        </button>
      </form>
    </div>
  );
};

export default ChatWindow;
