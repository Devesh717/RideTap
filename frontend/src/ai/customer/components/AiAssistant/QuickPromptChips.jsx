import React from "react";

const SUGGESTED_PROMPTS = [
  "Calculate fare between stations",
  "List all metro stations",
  "Check my wallet balance",
  "Show my recent booking history",
  "Help me book a metro ticket",
];

const QuickPromptChips = ({ onSelectPrompt, disabled, compact = false }) => {
  return (
    <div className="flex flex-wrap gap-2">
      {SUGGESTED_PROMPTS.map((prompt) => (
        <button
          key={prompt}
          type="button"
          disabled={disabled}
          onClick={() => onSelectPrompt(prompt)}
          className={`${
            compact ? "px-2.5 py-1 text-[11px]" : "px-3 py-1.5 text-xs"
          } rounded-full bg-teal-50 hover:bg-teal-100 text-teal-800 border border-teal-200 font-medium transition cursor-pointer disabled:opacity-50`}
        >
          {prompt}
        </button>
      ))}
    </div>
  );
};

export default QuickPromptChips;
