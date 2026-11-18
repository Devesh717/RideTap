import React from "react";
import { useSelector } from "react-redux";

const WalletBalanceCard = ({ onRefresh, onOpenTopUp }) => {
  const { balance, loading } = useSelector((state) => state.wallet);
  const { user } = useSelector((state) => state.auth);

  const isLowBalance = balance !== null && balance < 50;

  return (
    <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-slate-900 via-slate-800 to-teal-800 text-white p-7 shadow-xl">
      {/* Subtle decorative ring */}
      <div className="absolute -right-12 -top-12 w-48 h-48 rounded-full bg-teal-400/10 pointer-events-none" />

      <div className="flex items-start justify-between gap-4">
        <div>
          <span className="inline-flex items-center gap-1.5 text-[11px] font-bold uppercase tracking-widest text-teal-300 bg-teal-500/15 border border-teal-400/25 px-3 py-1 rounded-full">
            RideTap Smart Metro Card
          </span>
          <p className="text-xs text-slate-300 mt-3">Available Card Balance</p>

          <div className="flex items-baseline gap-2 mt-1">
            {loading && balance === null ? (
              <span className="text-3xl font-extrabold text-slate-300 animate-pulse">
                Loading...
              </span>
            ) : (
              <span className="text-4xl sm:text-5xl font-extrabold tracking-tight">
                &#8377;{balance !== null ? Number(balance).toFixed(2) : "0.00"}
              </span>
            )}
          </div>
        </div>

        <button
          type="button"
          onClick={onRefresh}
          disabled={loading}
          title="Sync Balance"
          className="px-3 py-1.5 rounded-xl bg-white/10 hover:bg-white/20 text-xs font-semibold text-white transition cursor-pointer border border-white/15 disabled:opacity-50"
        >
          {loading ? "Syncing..." : "Refresh"}
        </button>
      </div>

      {isLowBalance && (
        <div className="mt-4 px-3.5 py-2 rounded-xl bg-amber-500/20 border border-amber-400/30 text-amber-200 text-xs font-medium">
          Low balance alert: Recharge your Metro Card to avoid gate or booking interruptions.
        </div>
      )}

      <div className="mt-7 pt-5 border-t border-white/10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <p className="text-[11px] uppercase tracking-wider text-slate-400">Card Holder</p>
          <p className="text-sm font-semibold text-white truncate max-w-[240px]">
            {user?.email || "Active Commuter"}
          </p>
        </div>

        {onOpenTopUp && (
          <button
            type="button"
            onClick={onOpenTopUp}
            className="px-5 py-2.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-xs shadow-md transition cursor-pointer border-0"
          >
            + Quick Recharge
          </button>
        )}
      </div>
    </div>
  );
};

export default WalletBalanceCard;
