import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchUserBookings,
  cancelBooking,
  clearBookingMessages,
} from "../../../state/Booking/Action";
import BookingCard from "../../components/Booking/BookingCard";
import QRTicketModal from "../../components/Booking/QRTicketModal";

const MyBookingsPage = () => {
  const dispatch = useDispatch();
  const { bookings, loading, cancellingId, error, successMessage } = useSelector(
    (state) => state.booking
  );

  // Filter: "" = All, 1 = CONFIRMED, 0 = CANCELLED (matches BookingHistoryController @RequestParam Integer bookingStatus)
  const [statusFilter, setStatusFilter] = useState("");
  const [selectedQrBooking, setSelectedQrBooking] = useState(null);

  useEffect(() => {
    dispatch(fetchUserBookings(statusFilter === "" ? null : Number(statusFilter)));
    return () => {
      dispatch(clearBookingMessages());
    };
  }, [dispatch, statusFilter]);

  const handleCancelBooking = (bookingId) => {
    if (window.confirm(`Cancel Booking #${bookingId} and refund the fare to your Metro Card?`)) {
      dispatch(cancelBooking(bookingId));
    }
  };

  const tabs = [
    { label: "All Rides", value: "" },
    { label: "Confirmed (Active)", value: "1" },
    { label: "Cancelled", value: "0" },
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 mb-6">
        <div>
          <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
            Travel History &amp; Passes
          </span>
          <h1 className="text-3xl font-extrabold text-slate-900 mt-1">My Metro Tickets</h1>
          <p className="text-sm text-slate-600 mt-1">
            View active QR gate passes, past journeys, or cancel confirmed rides for an instant refund.
          </p>
        </div>

        <Link
          to="/book-ride"
          className="inline-flex items-center justify-center px-5 py-2.5 rounded-xl bg-teal-600 hover:bg-teal-700 text-white text-xs font-bold shadow-xs transition no-underline"
        >
          + Book New Ride
        </Link>
      </div>

      {/* Filter Tabs */}
      <div className="flex flex-wrap items-center gap-2 mb-6">
        {tabs.map((tab) => {
          const active = statusFilter === tab.value;
          return (
            <button
              key={tab.label}
              type="button"
              onClick={() => setStatusFilter(tab.value)}
              className={`px-4 py-2 rounded-xl text-xs font-semibold transition cursor-pointer border ${
                active
                  ? "bg-slate-900 text-white border-slate-900"
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

      {loading ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center text-sm text-slate-500">
          Loading your metro bookings...
        </div>
      ) : bookings.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center">
          <h3 className="text-lg font-bold text-slate-900">No tickets found</h3>
          <p className="text-xs text-slate-500 mt-1 mb-5">
            You don&apos;t have any bookings under this filter yet.
          </p>
          <Link
            to="/book-ride"
            className="inline-block px-5 py-2.5 rounded-xl bg-teal-600 text-white text-xs font-bold no-underline"
          >
            Book Your First Ride
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {bookings.map((booking) => (
            <BookingCard
              key={booking.id}
              booking={booking}
              isCancelling={cancellingId === booking.id}
              onViewQr={(b) => setSelectedQrBooking(b)}
              onCancel={handleCancelBooking}
            />
          ))}
        </div>
      )}

      <QRTicketModal
        booking={selectedQrBooking}
        open={Boolean(selectedQrBooking)}
        onClose={() => setSelectedQrBooking(null)}
      />
    </div>
  );
};

export default MyBookingsPage;
