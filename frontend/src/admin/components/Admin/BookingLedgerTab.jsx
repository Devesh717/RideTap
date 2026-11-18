import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchAdminBookings } from "../../state/Admin/Action";
import BookingStatusBadge from "../../../booking/customer/components/Booking/BookingStatusBadge";

const BookingLedgerTab = () => {
  const dispatch = useDispatch();
  const { bookings, loadingBookings } = useSelector((state) => state.admin);

  const [metroCardFilter, setMetroCardFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState(""); // "" | "1" | "0"

  useEffect(() => {
    dispatch(fetchAdminBookings());
  }, [dispatch]);

  const handleApplyFilters = (e) => {
    e.preventDefault();
    dispatch(
      fetchAdminBookings({
        metroCardNumber: metroCardFilter.trim(),
        bookingStatus: statusFilter === "" ? null : Number(statusFilter),
      })
    );
  };

  const handleReset = () => {
    setMetroCardFilter("");
    setStatusFilter("");
    dispatch(fetchAdminBookings());
  };

  const totalRevenue = bookings
    .filter((b) => String(b.bookingStatus || "").toUpperCase() === "CONFIRMED")
    .reduce((sum, b) => sum + Number(b.fare ?? 0), 0);

  return (
    <div className="space-y-5">
      {/* Filter Bar + Revenue Summary */}
      <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <form onSubmit={handleApplyFilters} className="flex flex-wrap items-center gap-2.5">
          <input
            type="text"
            placeholder="Filter by Metro Card #..."
            value={metroCardFilter}
            onChange={(e) => setMetroCardFilter(e.target.value)}
            className="px-3.5 py-2 border border-slate-300 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-teal-600"
          />

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3.5 py-2 border border-slate-300 rounded-xl text-xs font-semibold text-slate-700 bg-white"
          >
            <option value="">All Statuses</option>
            <option value="1">Confirmed Only</option>
            <option value="0">Cancelled Only</option>
          </select>

          <button
            type="submit"
            className="px-4 py-2 rounded-xl bg-teal-600 hover:bg-teal-700 text-white text-xs font-semibold cursor-pointer border-0"
          >
            Apply Filters
          </button>

          {(metroCardFilter || statusFilter !== "") && (
            <button
              type="button"
              onClick={handleReset}
              className="px-3 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold cursor-pointer border-0"
            >
              Reset
            </button>
          )}
        </form>

        <div className="bg-slate-900 text-white px-4 py-2.5 rounded-xl flex items-center gap-4 text-xs">
          <div>
            <span className="text-slate-400">Filtered Rides: </span>
            <strong className="text-white">{bookings.length}</strong>
          </div>
          <div className="h-4 w-px bg-slate-700" />
          <div>
            <span className="text-slate-400">Confirmed Fare Volume: </span>
            <strong className="text-teal-300">&#8377;{totalRevenue.toFixed(2)}</strong>
          </div>
        </div>
      </div>

      {/* Bookings Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        {loadingBookings ? (
          <div className="p-10 text-center text-sm text-slate-500">
            Loading system booking ledger...
          </div>
        ) : bookings.length === 0 ? (
          <div className="p-10 text-center text-sm text-slate-500">
            No bookings match the selected filters.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="bg-slate-50 text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                  <th className="py-3 px-5">Ticket ID</th>
                  <th className="py-3 px-5">Commuter / Card</th>
                  <th className="py-3 px-5">Route</th>
                  <th className="py-3 px-5">Fare</th>
                  <th className="py-3 px-5">Date &amp; Time</th>
                  <th className="py-3 px-5">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {bookings.map((b) => (
                  <tr key={b.id} className="hover:bg-slate-50/80">
                    <td className="py-3.5 px-5 font-bold text-slate-900">#{b.id}</td>
                    <td className="py-3.5 px-5 text-xs">
                      <div className="font-bold text-slate-900">
                        {b.user?.holderName || "Commuter"}
                      </div>
                      <div className="font-mono text-slate-500">
                        {b.user?.metroCardNumber || "N/A"}
                      </div>
                    </td>
                    <td className="py-3.5 px-5 text-xs font-semibold text-slate-800">
                      {b.originStation?.name} &rarr; {b.destinationStation?.name}
                    </td>
                    <td className="py-3.5 px-5 font-extrabold text-slate-900">
                      &#8377;{Number(b.fare ?? 0).toFixed(2)}
                    </td>
                    <td className="py-3.5 px-5 text-xs text-slate-500">
                      {b.dateTime ? new Date(b.dateTime).toLocaleString() : "N/A"}
                    </td>
                    <td className="py-3.5 px-5">
                      <BookingStatusBadge status={b.bookingStatus} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default BookingLedgerTab;
