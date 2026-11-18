import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { addBalance, clearWalletMessages } from "../../../state/Wallet/Action";

const QUICK_AMOUNTS = [100, 200, 500, 1000];

const TopUpForm = ({ onSuccessCallback }) => {
  const dispatch = useDispatch();
  const { recharging, error, successMessage } = useSelector((state) => state.wallet);

  const [amount, setAmount] = useState("");
  const [validationError, setValidationError] = useState("");

  const handleSelectPreset = (preset) => {
    setAmount(String(preset));
    setValidationError("");
    dispatch(clearWalletMessages());
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setValidationError("");

    const numericAmount = parseFloat(amount);
    if (isNaN(numericAmount) || numericAmount < 10) {
      setValidationError("Minimum Metro Card recharge amount is ₹10.");
      return;
    }

    dispatch(
      addBalance(numericAmount, () => {
        setAmount("");
        if (onSuccessCallback) onSuccessCallback();
      })
    );
  };

  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-xs">
      <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
        Instant Top-Up
      </span>
      <h2 className="text-xl font-bold text-slate-900 mt-0.5">
        Recharge Metro Card
      </h2>
      <p className="text-xs text-slate-500 mt-1 mb-5">
        Select a quick preset or enter a custom amount to credit your RideTap balance via Razorpay.
      </p>

      {/* Preset Chips */}
      <div className="grid grid-cols-4 gap-2.5 mb-5">
        {QUICK_AMOUNTS.map((preset) => {
          const active = Number(amount) === preset;
          return (
            <button
              key={preset}
              type="button"
              onClick={() => handleSelectPreset(preset)}
              className={`py-2.5 px-2 rounded-xl text-sm font-bold border transition cursor-pointer ${
                active
                  ? "bg-teal-600 text-white border-teal-600 shadow-xs"
                  : "bg-slate-50 hover:bg-teal-50 text-slate-800 border-slate-200 hover:border-teal-300"
              }`}
            >
              &#8377;{preset}
            </button>
          );
        })}
      </div>

      {(validationError || error) && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-3.5 py-2.5 rounded-xl text-xs mb-4">
          {validationError || error}
        </div>
      )}

      {successMessage && (
        <div className="bg-teal-50 border border-teal-200 text-teal-800 px-3.5 py-2.5 rounded-xl text-xs mb-4 font-medium">
          {successMessage}
        </div>
      )}

      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <div className="flex flex-col gap-1.5">
          <label htmlFor="recharge-amount" className="text-xs font-semibold text-slate-700">
            Custom Amount (&#8377;)
          </label>
          <input
            id="recharge-amount"
            type="number"
            step="1"
            min="10"
            placeholder="e.g. 250"
            value={amount}
            onChange={(e) => {
              setAmount(e.target.value);
              setValidationError("");
            }}
            required
            className="px-3.5 py-2.5 border border-slate-300 rounded-xl text-sm text-slate-900 focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20"
          />
        </div>

        <button
          type="submit"
          disabled={recharging}
          className="w-full py-3 px-4 bg-teal-600 hover:bg-teal-700 disabled:opacity-65 text-white font-semibold text-sm rounded-xl shadow-sm transition cursor-pointer border-0"
        >
          {recharging
            ? "Opening Razorpay Checkout..."
            : amount && Number(amount) >= 10
            ? `Pay ₹${Number(amount).toFixed(2)} via Razorpay`
            : "Recharge via Razorpay"}
        </button>
      </form>
    </div>
  );
};

export default TopUpForm;
