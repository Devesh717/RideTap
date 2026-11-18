import React from "react";
import TopUpForm from "./TopUpForm";

const TopUpModal = ({ open, onClose }) => {
  if (!open) return null;

  return (
    <div
      className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-md bg-white rounded-2xl shadow-2xl overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          type="button"
          onClick={onClose}
          aria-label="Close recharge modal"
          className="absolute top-3.5 right-4 text-slate-400 hover:text-slate-700 text-2xl leading-none bg-transparent border-0 cursor-pointer z-10"
        >
          &times;
        </button>
        <TopUpForm onSuccessCallback={onClose} />
      </div>
    </div>
  );
};

export default TopUpModal;
