import React, { useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { fetchBalance, clearWalletMessages } from "../../../state/Wallet/Action";
import WalletBalanceCard from "../../components/Wallet/WalletBalanceCard";
import TopUpForm from "../../components/Wallet/TopUpForm";

const WalletPage = () => {
  const dispatch = useDispatch();
  const { recentTopUps } = useSelector((state) => state.wallet);
  const topUpSectionRef = useRef(null);

  useEffect(() => {
    dispatch(fetchBalance());
    return () => {
      dispatch(clearWalletMessages());
    };
  }, [dispatch]);

  const scrollToTopUp = () => {
    topUpSectionRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 mb-8">
        <div>
          <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
            Digital Transit Wallet
          </span>
          <h1 className="text-3xl font-extrabold text-slate-900 mt-1">
            Metro Card &amp; Balance
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Manage your RideTap prepaid balance for instant QR ticket purchases and gate tap-ins.
          </p>
        </div>

        <Link
          to="/book-ride"
          className="inline-flex items-center justify-center px-4 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold transition no-underline"
        >
          Book a Ride &rarr;
        </Link>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Left Column: Metro Card + Session Top-up History */}
        <div className="lg:col-span-7 flex flex-col gap-6">
          <WalletBalanceCard
            onRefresh={() => dispatch(fetchBalance())}
            onOpenTopUp={scrollToTopUp}
          />

          <div className="bg-white rounded-2xl border border-slate-200 p-6">
            <h3 className="text-base font-bold text-slate-900">Recent Session Recharges</h3>
            <p className="text-xs text-slate-500 mt-0.5 mb-4">
              Instant confirmation log for top-ups completed during this session.
            </p>

            {recentTopUps.length === 0 ? (
              <div className="text-center py-6 bg-slate-50 rounded-xl border border-dashed border-slate-200">
                <p className="text-xs font-medium text-slate-500">
                  No recharges made in this session yet.
                </p>
              </div>
            ) : (
              <div className="flex flex-col gap-2.5">
                {recentTopUps.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between px-4 py-3 rounded-xl bg-teal-50/60 border border-teal-100"
                  >
                    <div>
                      <p className="text-sm font-bold text-slate-900">
                        + &#8377;{Number(item.amount).toFixed(2)} Credited
                      </p>
                      <p className="text-[11px] text-slate-500">
                        Metro Card Wallet Top-Up &bull; {item.timestamp}
                      </p>
                    </div>
                    <span className="text-[11px] font-bold uppercase tracking-wider bg-emerald-100 text-emerald-800 px-2.5 py-1 rounded-full">
                      Accepted
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Top-Up Form */}
        <div ref={topUpSectionRef} className="lg:col-span-5">
          <TopUpForm />
        </div>
      </div>
    </div>
  );
};

export default WalletPage;
