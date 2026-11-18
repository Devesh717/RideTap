import React, { useState } from "react";
import LoginForm from "./LoginForm";
import RegisterForm from "./RegisterForm";

const AuthModal = ({ open, handleClose, initialMode = "login" }) => {
  const [mode, setMode] = useState(initialMode);

  if (!open) return null;

  return (
    <div
      className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center z-50 p-4"
      onClick={handleClose}
    >
      <div
        className="relative w-full max-w-md bg-white rounded-2xl p-8 shadow-2xl"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          type="button"
          onClick={handleClose}
          aria-label="Close modal"
          className="absolute top-3.5 right-4 text-slate-400 hover:text-slate-700 text-2xl leading-none bg-transparent border-0 cursor-pointer"
        >
          &times;
        </button>

        {mode === "login" ? (
          <LoginForm
            onSwitchToRegister={() => setMode("register")}
            onSuccessCallback={handleClose}
          />
        ) : (
          <RegisterForm onSwitchToLogin={() => setMode("login")} />
        )}
      </div>
    </div>
  );
};

export default AuthModal;
