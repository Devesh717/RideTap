import React, { useEffect } from "react";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { fetchUserProfile } from "../../../state/Profile/Action";

const ProfilePage = () => {
  const dispatch = useDispatch();
  const { profile, loading, error } = useSelector((state) => state.profile);

  useEffect(() => {
    dispatch(fetchUserProfile());
  }, [dispatch]);

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-center text-sm text-slate-500">
        Loading your commuter profile...
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12">
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-xl text-sm">
          {error}
        </div>
      </div>
    );
  }

  if (!profile) return null;

  const memberSince = profile.createdAt
    ? new Date(profile.createdAt).toLocaleDateString([], {
        year: "numeric",
        month: "long",
        day: "numeric",
      })
    : "N/A";

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
          Commuter Identity
        </span>
        <h1 className="text-3xl font-extrabold text-slate-900 mt-1">My RideTap Profile</h1>
        <p className="text-sm text-slate-600 mt-1">
          View your registered Metro Card credentials, account details, and ride count.
        </p>
      </div>

      {/* Digital Metro Card Header */}
      <div className="rounded-3xl bg-gradient-to-br from-slate-900 via-slate-800 to-teal-800 text-white p-7 shadow-xl mb-8">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <span className="inline-block text-[11px] font-bold uppercase tracking-widest text-teal-300 bg-teal-500/15 border border-teal-400/25 px-3 py-1 rounded-full">
              Official Smart Metro Card
            </span>
            <h2 className="text-2xl sm:text-3xl font-extrabold mt-3">
              {profile.holderName}
            </h2>
            <p className="font-mono text-sm tracking-widest text-teal-200 mt-1">
              CARD NO: {profile.metroCardNumber}
            </p>
          </div>

          <div className="bg-white/10 border border-white/15 rounded-2xl px-5 py-3.5 text-left sm:text-right">
            <p className="text-[11px] uppercase tracking-wider text-slate-300">Wallet Balance</p>
            <p className="text-2xl font-extrabold text-white mt-0.5">
              &#8377;{Number(profile.balance ?? 0).toFixed(2)}
            </p>
            <Link
              to="/wallet"
              className="inline-block mt-1.5 text-xs font-bold text-teal-300 hover:text-teal-200 no-underline"
            >
              Manage Balance &rarr;
            </Link>
          </div>
        </div>
      </div>

      {/* Account Information Grid */}
      <div className="bg-white rounded-3xl border border-slate-200 p-6 sm:p-8 shadow-xs">
        <h3 className="text-base font-bold text-slate-900 mb-5">Account Details</h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5 text-sm">
          <div className="bg-slate-50 border border-slate-100 rounded-2xl p-4">
            <p className="text-xs text-slate-400">Email Address</p>
            <p className="font-bold text-slate-900 mt-1">{profile.email}</p>
          </div>

          <div className="bg-slate-50 border border-slate-100 rounded-2xl p-4">
            <p className="text-xs text-slate-400">Mobile Number</p>
            <p className="font-bold text-slate-900 mt-1">{profile.mobileNumber}</p>
          </div>

          <div className="bg-slate-50 border border-slate-100 rounded-2xl p-4">
            <p className="text-xs text-slate-400">Member Since</p>
            <p className="font-bold text-slate-900 mt-1">{memberSince}</p>
          </div>

          <div className="bg-slate-50 border border-slate-100 rounded-2xl p-4">
            <p className="text-xs text-slate-400">Total Rides Booked</p>
            <div className="flex items-center justify-between mt-1">
              <p className="font-bold text-slate-900">
                {Array.isArray(profile.bookings) ? profile.bookings.length : 0} Journeys
              </p>
              <Link
                to="/bookings"
                className="text-xs font-bold text-teal-600 hover:text-teal-700 no-underline"
              >
                View History &rarr;
              </Link>
            </div>
          </div>
        </div>

        {/* Assigned Roles */}
        {Array.isArray(profile.roles) && profile.roles.length > 0 && (
          <div className="mt-6 pt-5 border-t border-slate-100 flex items-center justify-between flex-wrap gap-3">
            <span className="text-xs font-semibold text-slate-500">Assigned Roles</span>
            <div className="flex flex-wrap gap-2">
              {profile.roles.map((role, idx) => (
                <span
                  key={idx}
                  className="px-3 py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-slate-900 text-teal-300"
                >
                  {String(role)}
                </span>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;
