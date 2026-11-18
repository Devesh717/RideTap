import React from "react";

const BookingStatusBadge = ({ status }) => {
  const normalized = String(status || "").toUpperCase();

  if (normalized === "CONFIRMED") {
    return (
      <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-emerald-50 text-emerald-700 border border-emerald-200">
        <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
        Confirmed
      </span>
    );
  }

  if (normalized === "CANCELLED") {
    return (
      <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-rose-50 text-rose-700 border border-rose-200">
        <span className="w-1.5 h-1.5 rounded-full bg-rose-500" />
        Cancelled
      </span>
    );
  }

  return (
    <span className="inline-flex items-center px-2.5 py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-slate-100 text-slate-700">
      {normalized || "UNKNOWN"}
    </span>
  );
};

export default BookingStatusBadge;
