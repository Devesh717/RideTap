import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchAdminUsers,
  searchAdminUser,
  deleteAdminUser,
  deleteAllAdminUsers,
} from "../../state/Admin/Action";

const UserManagerTab = () => {
  const dispatch = useDispatch();
  const { users, loadingUsers, deletingCardNumber } = useSelector((state) => state.admin);

  const [searchMode, setSearchMode] = useState("card"); // "card" | "email"
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    dispatch(fetchAdminUsers());
  }, [dispatch]);

  const handleSearch = (e) => {
    e.preventDefault();
    dispatch(searchAdminUser(searchQuery, searchMode));
  };

  const handleReset = () => {
    setSearchQuery("");
    dispatch(fetchAdminUsers());
  };

  const handleDeleteUser = (cardNumber, holderName) => {
    if (window.confirm(`Delete user "${holderName}" (Card: ${cardNumber})?`)) {
      dispatch(deleteAdminUser(cardNumber));
    }
  };

  const handleDeleteAll = () => {
    if (
      window.confirm(
        "WARNING: Are you sure you want to permanently delete ALL users from the database?"
      )
    ) {
      dispatch(deleteAllAdminUsers());
    }
  };

  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
      {/* Search & Controls Toolbar */}
      <div className="p-5 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <form onSubmit={handleSearch} className="flex flex-wrap items-center gap-2 flex-1">
          <select
            value={searchMode}
            onChange={(e) => setSearchMode(e.target.value)}
            className="px-3 py-2 border border-slate-300 rounded-xl text-xs font-semibold bg-slate-50 text-slate-700"
          >
            <option value="card">By Metro Card #</option>
            <option value="email">By Email</option>
          </select>

          <input
            type="text"
            placeholder={
              searchMode === "card" ? "Enter Metro Card Number..." : "Enter User Email..."
            }
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-1 min-w-[200px] max-w-sm px-3.5 py-2 border border-slate-300 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-teal-600"
          />

          <button
            type="submit"
            className="px-4 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold cursor-pointer border-0"
          >
            Lookup
          </button>

          {searchQuery && (
            <button
              type="button"
              onClick={handleReset}
              className="px-3 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold cursor-pointer border-0"
            >
              Reset
            </button>
          )}
        </form>

        <button
          type="button"
          onClick={handleDeleteAll}
          disabled={users.length === 0 || deletingCardNumber === "ALL"}
          className="px-3.5 py-2 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 text-xs font-bold border border-rose-200 cursor-pointer disabled:opacity-50"
        >
          Delete All Users
        </button>
      </div>

      {loadingUsers ? (
        <div className="p-10 text-center text-sm text-slate-500">Loading registered users...</div>
      ) : users.length === 0 ? (
        <div className="p-10 text-center text-sm text-slate-500">No users found.</div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-sm">
            <thead>
              <tr className="bg-slate-50 text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                <th className="py-3 px-5">Metro Card #</th>
                <th className="py-3 px-5">Holder Name</th>
                <th className="py-3 px-5">Email &amp; Mobile</th>
                <th className="py-3 px-5">Balance</th>
                <th className="py-3 px-5">Rides</th>
                <th className="py-3 px-5 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {users.map((u) => (
                <tr key={u.metroCardNumber} className="hover:bg-slate-50/80">
                  <td className="py-3.5 px-5 font-mono text-xs font-bold text-teal-700">
                    {u.metroCardNumber}
                  </td>
                  <td className="py-3.5 px-5 font-semibold text-slate-900">{u.holderName}</td>
                  <td className="py-3.5 px-5 text-xs text-slate-600">
                    <div>{u.email}</div>
                    <div className="text-slate-400">{u.mobileNumber}</div>
                  </td>
                  <td className="py-3.5 px-5 font-bold text-slate-900">
                    &#8377;{Number(u.balance ?? 0).toFixed(2)}
                  </td>
                  <td className="py-3.5 px-5 text-xs font-semibold text-slate-600">
                    {Array.isArray(u.bookings) ? u.bookings.length : 0}
                  </td>
                  <td className="py-3.5 px-5 text-right">
                    <button
                      type="button"
                      disabled={deletingCardNumber === u.metroCardNumber}
                      onClick={() => handleDeleteUser(u.metroCardNumber, u.holderName)}
                      className="px-3 py-1.5 rounded-lg bg-rose-50 hover:bg-rose-100 text-rose-700 text-xs font-semibold border border-rose-200 cursor-pointer disabled:opacity-50"
                    >
                      {deletingCardNumber === u.metroCardNumber ? "Deleting..." : "Delete"}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default UserManagerTab;
