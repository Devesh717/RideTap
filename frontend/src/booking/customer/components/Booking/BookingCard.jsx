import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import BookingStatusBadge from "./BookingStatusBadge";
import { getShortestRouteApi } from "../../../../station/api/StationApi";
import LiveJourneyTracker from "../Booking/LiveJourneyTracker";

const BookingCard = ({ booking, onViewQr, onCancel, isCancelling }) => {
  const isConfirmed = String(booking.bookingStatus || "").toUpperCase() === "CONFIRMED";
  const formattedDate = booking.dateTime
    ? new Date(booking.dateTime).toLocaleString([], {
        dateStyle: "medium",
        timeStyle: "short",
      })
    : "N/A";

  const originName = booking.originStation?.name || "";
  const destinationName = booking.destinationStation?.name || "";

  const [journeyPath, setJourneyPath] = useState(
    Array.isArray(booking.path) ? booking.path : []
  );

  useEffect(() => {
    if (!isConfirmed || journeyPath.length > 2) return;
    if (!originName || !destinationName) return;

    let isMounted = true;
    getShortestRouteApi(originName, destinationName)
      .then((routeData) => {
        if (isMounted && Array.isArray(routeData?.path) && routeData.path.length >= 2) {
          setJourneyPath(routeData.path);
        }
      })
      .catch(() => {
        if (isMounted && journeyPath.length === 0) {
          setJourneyPath([
            {
              stationName: originName,
              line: booking.originStation?.lines?.[0] || "Metro Line",
            },
            {
              stationName: destinationName,
              line: booking.destinationStation?.lines?.[0] || "Metro Line",
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [isConfirmed, originName, destinationName, booking.originStation, booking.destinationStation, journeyPath.length]);

  return (
    <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-xs hover:shadow-md transition flex flex-col justify-between gap-4">
      <div>
        <div className="flex items-center justify-between gap-2 mb-3">
          <span className="text-xs font-bold text-slate-500">Ticket #{booking.id}</span>
          <BookingStatusBadge status={booking.bookingStatus} />
        </div>

        {/* Origin -> Destination */}
        <div className="flex items-center justify-between gap-2 bg-slate-50 rounded-xl p-3.5 border border-slate-100">
          <div className="min-w-0">
            <p className="text-[10px] font-bold uppercase tracking-wider text-slate-400">From</p>
            <p className="text-sm font-bold text-slate-900 truncate">
              {originName || "Origin"}
            </p>
          </div>
          <span className="text-teal-600 font-extrabold px-2">&rarr;</span>
          <div className="min-w-0 text-right">
            <p className="text-[10px] font-bold uppercase tracking-wider text-slate-400">To</p>
            <p className="text-sm font-bold text-slate-900 truncate">
              {destinationName || "Destination"}
            </p>
          </div>
        </div>

        <div className="flex items-center justify-between mt-3.5 text-xs text-slate-500">
          <span>{formattedDate}</span>
          <span className="text-base font-extrabold text-slate-900">
            &#8377;{Number(booking.fare ?? 0).toFixed(2)}
          </span>
        </div>
      </div>

      <div className="pt-3 border-t border-slate-100 space-y-3">
        <div className="flex items-center gap-2">
          {isConfirmed && onViewQr && (
            <button
              type="button"
              onClick={() => onViewQr(booking)}
              className="flex-1 py-2 px-3 rounded-xl bg-teal-600 hover:bg-teal-700 text-white text-xs font-semibold transition cursor-pointer border-0"
            >
              Show QR Pass
            </button>
          )}

          <Link
            to={`/bookings/${booking.id}`}
            className="py-2 px-3 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold transition no-underline text-center"
          >
            Details
          </Link>

          {isConfirmed && onCancel && (
            <button
              type="button"
              disabled={isCancelling}
              onClick={() => onCancel(booking.id)}
              className="py-2 px-3 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 text-xs font-semibold transition cursor-pointer border border-rose-200 disabled:opacity-50"
            >
              {isCancelling ? "Cancelling..." : "Cancel"}
            </button>
          )}
        </div>

        {/* Live In-Metro Stop Countdown & Exit Tracker for Confirmed Tickets */}
        {isConfirmed && journeyPath.length >= 2 && (
          <LiveJourneyTracker path={journeyPath} />
        )}
      </div>
    </div>
  );
};

export default BookingCard;