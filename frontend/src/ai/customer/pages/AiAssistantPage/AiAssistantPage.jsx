import React from "react";
import ChatWindow from "../../components/AiAssistant/ChatWindow";

const AiAssistantPage = () => {
  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-6">
        <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
          Smart Transit Support
        </span>
        <h1 className="text-3xl font-extrabold text-slate-900 mt-1">
          RideTap AI Concierge
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Calculate fares, check station routes, book metro tickets, or review your booking history using natural language.
        </p>
      </div>

      <ChatWindow />
    </div>
  );
};

export default AiAssistantPage;
