import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { fetchBalance } from "../../../state/Wallet/Action";
import { initiateRazorpayPayment } from "../../../../payment/utils/openRazorpayCheckout";

const PRESET_AMOUNTS = [100, 200, 500, 1000, 2000];

const MetroCardRechargePage = () => {
  const dispatch = useDispatch();
  const { balance, metroCardNumber, loading } = useSelector((state) => state.wallet);
  const { user } = useSelector((state) => state.auth);

  const [amount, setAmount] = useState("200");
  const [processing, setProcessing] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  useEffect(() => {
    dispatch(fetchBalance());
  }, [dispatch]);

  const handleRechargeWithRazorpay = async (e) => {
    e.preventDefault();
    setErrorMsg("");
    setSuccessMsg("");

    const numericAmount = Number(amount);
    if (!numericAmount || numericAmount < 10) {
      setErrorMsg("Please enter a valid recharge amount of at least ₹10.");
      return;
    }

    setProcessing(true);
    await initiateRazorpayPayment({
      purpose: "METROCARD_RECHARGE",
      amount: numericAmount,
      description: `Metro Card Top-Up (₹${numericAmount})`,
      onSuccess: (paymentData) => {
        setProcessing(false);
        setSuccessMsg(
          `₹${Number(paymentData.amount).toFixed(2)} credited to your Metro Card! Ref: ${paymentData.referenceNumber}`
        );
        dispatch(fetchBalance());
      },
      onError: (err) => {
        setProcessing(false);
        setErrorMsg(err);
      },
      onDismiss: () => {
        setProcessing(false);
      },
    });
  };

  const cardNumber = metroCardNumber || user?.metroCardNumber || "RT-CARD";

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
          Smart Card Top-Up
        </span>
        <h1 className="text-3xl font-extrabold text-slate-900 mt-1">
          Recharge RideTap Metro Card
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Top up your digital Metro Card securely via Razorpay (UPI, Cards, NetBanking, Wallets).
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-7 items-start">
        <div className="lg:col-span-5">
          <div className="rounded-3xl bg-gradient-to-br from-slate-900 via-slate-800 to-teal-800 text-white p-6 shadow-lg">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold uppercase tracking-widest text-teal-300">
                RideTap Smart Card
              </span>
              <span className="px-2.5 py-0.5 rounded-full bg-white/15 text-[10px] font-bold">
                ACTIVE
              </span>
            </div>

            <div className="mt-8">
              <p className="text-[11px] uppercase tracking-wider text-slate-300">
                Available Card Balance
              </p>
              <p className="text-4xl font-extrabold mt-1">
                {loading
                  ? "..."
                  : `₹${balance !== null ? Number(balance).toFixed(2) : "0.00"}`}
              </p>
            </div>

            <div className="mt-8 pt-4 border-t border-white/15 flex items-center justify-between text-xs">
              <div>
                <p className="text-[10px] uppercase text-slate-400">Card Number</p>
                <p className="font-mono font-bold tracking-wider mt-0.5">{cardNumber}</p>
              </div>
              <div className="text-right">
                <p className="text-[10px] uppercase text-slate-400">Network</p>
                <p className="font-semibold mt-0.5">Delhi NCR Metro</p>
              </div>
            </div>
          </div>
        </div>

        <div className="lg:col-span-7 bg-white rounded-3xl border border-slate-200 p-6 sm:p-8 shadow-sm">
          <h2 className="text-lg font-extrabold text-slate-900 mb-1">
            Select Top-Up Amount
          </h2>
          <p className="text-xs text-slate-500 mb-5">
            Choose a quick preset or enter a custom amount (minimum ₹10).
          </p>

          {errorMsg && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-xs mb-4">
              {errorMsg}
            </div>
          )}

          {successMsg && (
            <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-xl text-xs mb-4 flex items-center justify-between gap-3">
              <span className="font-semibold">{successMsg}</span>
              <Link
                to="/book-ride"
                className="px-3 py-1.5 rounded-lg bg-emerald-700 text-white font-bold text-xs shrink-0 no-underline"
              >
                Book Ride &rarr;
              </Link>
            </div>
          )}

          <form onSubmit={handleRechargeWithRazorpay} className="space-y-5">
            <div className="grid grid-cols-3 sm:grid-cols-5 gap-2.5">
              {PRESET_AMOUNTS.map((preset) => {
                const selected = Number(amount) === preset;
                return (
                  <button
                    key={preset}
                    type="button"
                    onClick={() => setAmount(String(preset))}
                    className={`py-2.5 rounded-xl text-xs font-extrabold border transition cursor-pointer ${
                      selected
                        ? "bg-teal-600 text-white border-teal-600 shadow-xs"
                        : "bg-slate-50 hover:bg-slate-100 text-slate-800 border-slate-200"
                    }`}
                  >
                    +&#8377;{preset}
                  </button>
                );
              })}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1.5">
                Custom Recharge Amount (INR)
              </label>
              <div className="relative">
                <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 font-bold text-base">
                  &#8377;
                </span>
                <input
                  type="number"
                  min="10"
                  max="10000"
                  step="1"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  placeholder="Enter amount"
                  required
                  className="w-full pl-9 pr-4 py-3 border border-slate-300 rounded-xl text-base font-bold text-slate-900 focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={processing}
              className="w-full py-3.5 px-6 rounded-xl bg-gradient-to-r from-slate-900 to-teal-700 hover:from-slate-800 hover:to-teal-600 disabled:opacity-60 text-white font-bold text-sm shadow-md transition cursor-pointer border-0"
            >
              {processing
                ? "Opening Razorpay Checkout..."
                : `Pay ₹${Number(amount || 0).toFixed(2)} via Razorpay`}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default MetroCardRechargePage;
