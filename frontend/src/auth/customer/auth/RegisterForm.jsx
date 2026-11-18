import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, Link } from "react-router-dom";
import { register, clearAuthError } from "../../state/Auth/Action";

const RegisterForm = ({ onSwitchToLogin }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { loading, error } = useSelector((state) => state.auth);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    phoneNumber: "",
  });

  useEffect(() => {
    return () => {
      dispatch(clearAuthError());
    };
  }, [dispatch]);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    dispatch(
      register(formData, () => {
        if (onSwitchToLogin) {
          onSwitchToLogin();
        } else {
          navigate("/login");
        }
      })
    );
  };

  const isModal = Boolean(onSwitchToLogin);

  const cardContent = (
    <div className="w-full max-w-md bg-white rounded-3xl border border-slate-200/80 shadow-xl shadow-slate-900/5 p-7 sm:p-9">
      {/* Brand Header */}
      <div className="flex items-center justify-between mb-6">
        <Link to="/" className="inline-flex items-center gap-2.5 no-underline">
          <span className="w-9 h-9 rounded-xl bg-teal-600 text-white font-extrabold text-base flex items-center justify-center shadow-xs">
            R
          </span>
          <div className="flex flex-col">
            <span className="text-base font-extrabold text-slate-900 leading-none">
              Ride<span className="text-teal-600">Tap</span>
            </span>
            <span className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mt-0.5">
              Smart Metro
            </span>
          </div>
        </Link>

        <span className="text-[11px] font-bold uppercase tracking-wider text-teal-700 bg-teal-50 border border-teal-200/70 px-2.5 py-1 rounded-full">
          Instant Metro Card
        </span>
      </div>

      <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
        Create Account
      </h2>
      <p className="text-xs sm:text-sm text-slate-500 mt-1 mb-6">
        Get your digital Metro Card and start booking in seconds
      </p>

      {error && (
  <div className="bg-red-50 border border-red-200 text-red-700 px-3.5 py-2.5 rounded-xl text-xs mb-4 font-medium">
    {typeof error === "string" ? error : error.error || error.message || JSON.stringify(error)}
  </div>
)}

      <form className="flex flex-col gap-3.5" onSubmit={handleSubmit}>
        <div className="flex flex-col gap-1.5">
          <label htmlFor="reg-name" className="text-xs font-semibold text-slate-700">
            Full Name
          </label>
          <input
            id="reg-name"
            type="text"
            name="name"
            placeholder="Devesh Kumar"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full px-3.5 py-2.5 bg-slate-50/70 focus:bg-white border border-slate-300 rounded-xl text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-3 focus:ring-teal-500/15 transition"
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <label htmlFor="reg-email" className="text-xs font-semibold text-slate-700">
            Email Address
          </label>
          <input
            id="reg-email"
            type="email"
            name="email"
            placeholder="commuter@ridetap.com"
            value={formData.email}
            onChange={handleChange}
            required
            className="w-full px-3.5 py-2.5 bg-slate-50/70 focus:bg-white border border-slate-300 rounded-xl text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-3 focus:ring-teal-500/15 transition"
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <label htmlFor="reg-phone" className="text-xs font-semibold text-slate-700">
            Phone Number
          </label>
          <input
            id="reg-phone"
            type="tel"
            name="phoneNumber"
            placeholder="9876543210"
            value={formData.phoneNumber}
            onChange={handleChange}
            required
            className="w-full px-3.5 py-2.5 bg-slate-50/70 focus:bg-white border border-slate-300 rounded-xl text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-3 focus:ring-teal-500/15 transition"
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <label htmlFor="reg-password" className="text-xs font-semibold text-slate-700">
            Password
          </label>
          <input
            id="reg-password"
            type="password"
            name="password"
            placeholder="Create a strong password"
            value={formData.password}
            onChange={handleChange}
            required
            className="w-full px-3.5 py-2.5 bg-slate-50/70 focus:bg-white border border-slate-300 rounded-xl text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-3 focus:ring-teal-500/15 transition"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="mt-2 w-full py-3 px-4 bg-gradient-to-r from-slate-900 to-teal-700 hover:from-slate-800 hover:to-teal-600 disabled:opacity-60 disabled:cursor-not-allowed text-white font-bold text-sm rounded-xl shadow-md shadow-teal-900/10 transition cursor-pointer border-0"
        >
          {loading ? "Creating Account..." : "Create Digital Metro Card"}
        </button>
      </form>

      <div className="mt-6 pt-5 border-t border-slate-100 text-center text-xs sm:text-sm text-slate-600">
        Already have an account?
        <button
          type="button"
          className="text-teal-600 hover:text-teal-700 font-bold ml-1.5 cursor-pointer bg-transparent border-0"
          onClick={() =>
            onSwitchToLogin ? onSwitchToLogin() : navigate("/login")
          }
        >
          Sign In &rarr;
        </button>
      </div>
    </div>
  );

  if (isModal) return cardContent;

  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-gradient-to-br from-slate-100 via-slate-50 to-teal-50/60 px-4 py-10">
      {cardContent}
    </div>
  );
};

export default RegisterForm;