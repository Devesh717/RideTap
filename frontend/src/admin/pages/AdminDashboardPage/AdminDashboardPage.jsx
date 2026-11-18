import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { clearAdminMessages } from "../../state/Admin/Action";
import StationManagerTab from "../../components/Admin/StationManagerTab";
import UserManagerTab from "../../components/Admin/UserManagerTab";
import BookingLedgerTab from "../../components/Admin/BookingLedgerTab";

const AdminDashboardPage = () => {
  const dispatch = useDispatch();
  const { error, successMessage } = useSelector((state) => state.admin);
  const [activeTab, setActiveTab] = useState("stations"); // "stations" | "users" | "bookings"

  useEffect(() => {
    dispatch(clearAdminMessages());
  }, [dispatch, activeTab]);

  const tabs = [
    { id: "stations", label: "Stations & Sequence" },
    { id: "users", label: "Commuter Users" },
    { id: "bookings", label: "All Bookings Ledger" },
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-6">
        <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
          System Operations Console
        </span>
        <h1 className="text-3xl font-extrabold text-slate-900 mt-1">
          RideTap Admin Dashboard
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Manage metro stations, inspect registered commuters, and audit system-wide ticket bookings.
        </p>
      </div>

      {/* Navigation Tabs */}
      <div className="flex flex-wrap gap-2 mb-6 border-b border-slate-200 pb-4">
        {tabs.map((tab) => {
          const active = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              type="button"
              onClick={() => setActiveTab(tab.id)}
              className={`px-5 py-2.5 rounded-xl text-xs font-bold transition cursor-pointer border ${
                active
                  ? "bg-slate-900 text-white border-slate-900 shadow-xs"
                  : "bg-white text-slate-600 border-slate-200 hover:bg-slate-100"
              }`}
            >
              {tab.label}
            </button>
          );
        })}
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-xs mb-5">
          {error}
        </div>
      )}

      {successMessage && (
        <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-xl text-xs mb-5 font-medium">
          {successMessage}
        </div>
      )}

      {activeTab === "stations" && <StationManagerTab />}
      {activeTab === "users" && <UserManagerTab />}
      {activeTab === "bookings" && <BookingLedgerTab />}
    </div>
  );
};

export default AdminDashboardPage;
