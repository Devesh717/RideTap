import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { fetchBookingById, cancelBooking } from "../../../state/Booking/Action";
import { resolveQrImageUrl } from "../../../api/BookingApi";
import BookingStatusBadge from "../../components/Booking/BookingStatusBadge";

const BookingDetailsPage = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const { currentBooking, loading, cancellingId, error, successMessage } = useSelector(
    (state) => state.booking
  );
  const [imgError, setImgError] = useState(false);

  useEffect(() => {
    if (id) {
      setImgError(false);
      dispatch(fetchBookingById(id));
    }
  }, [dispatch, id]);

  if (loading) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-12 text-center text-sm text-slate-500">
        Loading ticket #{id}...
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-12">
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-xl text-sm">
          {error}
        </div>
        <Link to="/bookings" className="inline-block mt-4 text-xs font-bold text-teal-600">
          &larr; Back to My Tickets
        </Link>
      </div>
    );
  }

  if (!currentBooking) return null;

  const rawStatus = String(currentBooking.bookingStatus || "").toUpperCase();
  const expiresAtDate = currentBooking.expiresAt
    ? new Date(currentBooking.expiresAt)
    : currentBooking.dateTime
    ? new Date(new Date(currentBooking.dateTime).getTime() + 3 * 60 * 60 * 1000)
    : null;

  const isExpired =
    Boolean(currentBooking.expired) ||
    rawStatus === "EXPIRED" ||
    (rawStatus === "CONFIRMED" && expiresAtDate && new Date() > expiresAtDate);

  const isCancelled = rawStatus === "CANCELLED";
  const isUsed = rawStatus === "USED";
  const isConfirmed = rawStatus === "CONFIRMED" && !isExpired;
  const effectiveStatus = isExpired ? "EXPIRED" : rawStatus;

  const resolvedQrUrl = resolveQrImageUrl(currentBooking.qrPath || currentBooking.qrCodePath);
  const qrImageSrc =
    !imgError &&
    resolvedQrUrl &&
    (resolvedQrUrl.startsWith("data:image") || resolvedQrUrl.startsWith("http"))
      ? resolvedQrUrl
      : `https://api.qrserver.com/v1/create-qr-code/?size=220x220&margin=8&data=${encodeURIComponent(
          JSON.stringify({ bookingId: currentBooking.id })
        )}`;

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-8">
      <Link
        to="/bookings"
        className="inline-flex items-center text-xs font-bold text-slate-600 hover:text-teal-600 mb-5 no-underline"
      >
        &larr; Back to My Tickets
      </Link>

      {successMessage && (
        <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-xl text-xs mb-5 font-medium">
          {successMessage}
        </div>
      )}

      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="bg-slate-900 text-white px-6 py-5 flex items-center justify-between">
          <div>
            <span className="text-xs uppercase tracking-wider text-teal-400 font-bold">
              RideTap Digital Receipt &amp; Pass
            </span>
            <h1 className="text-2xl font-extrabold mt-0.5">Booking #{currentBooking.id}</h1>
          </div>
          <BookingStatusBadge status={effectiveStatus} />
        </div>

        <div className="p-6 sm:p-8 grid grid-cols-1 md:grid-cols-12 gap-8 items-center">
          <div className="md:col-span-7 flex flex-col gap-4">
            <div className="bg-slate-50 border border-slate-200 rounded-2xl p-4">
              <p className="text-[11px] uppercase tracking-wider text-slate-400">Origin Station</p>
              <p className="text-base font-bold text-slate-900">
                #{currentBooking.originStation?.sequenceNumber}{" "}
                {currentBooking.originStation?.name}
              </p>

              <div className="my-3 border-t border-dashed border-slate-300" />

              <p className="text-[11px] uppercase tracking-wider text-slate-400">
                Destination Station
              </p>
              <p className="text-base font-bold text-slate-900">
                #{currentBooking.destinationStation?.sequenceNumber}{" "}
                {currentBooking.destinationStation?.name}
              </p>
            </div>

            <div className="grid grid-cols-3 gap-3 text-xs">
              <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-100">
                <p className="text-slate-400">Fare Amount</p>
                <p className="text-base font-extrabold text-slate-900 mt-0.5">
                  &#8377;{Number(currentBooking.fare ?? 0).toFixed(2)}
                </p>
              </div>
              <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-100">
                <p className="text-slate-400">Booked On</p>
                <p className="text-xs font-bold text-slate-800 mt-0.5">
                  {currentBooking.dateTime
                    ? new Date(currentBooking.dateTime).toLocaleString()
                    : "N/A"}
                </p>
              </div>
              <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-100">
                <p className="text-slate-400">Valid Until</p>
                <p
                  className={`text-xs font-bold mt-0.5 ${
                    isExpired ? "text-rose-600" : "text-slate-800"
                  }`}
                >
                  {expiresAtDate ? expiresAtDate.toLocaleString() : "N/A"}
                </p>
              </div>
            </div>

            {isConfirmed && (
              <button
                type="button"
                disabled={cancellingId === currentBooking.id}
                onClick={() => dispatch(cancelBooking(currentBooking.id))}
                className="mt-2 py-3 px-4 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 font-bold text-xs border border-rose-200 transition cursor-pointer"
              >
                {cancellingId === currentBooking.id
                  ? "Cancelling & Refunding..."
                  : "Cancel Ticket & Refund to Metro Card"}
              </button>
            )}
          </div>

          <div className="md:col-span-5 flex flex-col items-center">
            <div
              className={`p-4 border-2 border-dashed rounded-2xl ${
                isConfirmed
                  ? "border-teal-300 bg-white"
                  : "border-rose-200 bg-rose-50/40"
              }`}
            >
              {isConfirmed ? (
                <img
                  src={qrImageSrc}
                  alt="Metro AFC Gate Pass"
                  onError={() => setImgError(true)}
                  className="w-48 h-48 object-contain"
                />
              ) : (
                <div className="w-48 h-48 rounded-xl flex flex-col items-center justify-center p-4 text-center">
                  <span className="px-3 py-1 rounded-full bg-rose-100 text-rose-700 text-[11px] font-extrabold uppercase tracking-wider">
                    {isCancelled ? "CANCELLED" : isExpired ? "EXPIRED" : "USED"}
                  </span>
                  <p className="text-xs font-bold text-slate-700 mt-3">
                    {isCancelled
                      ? "QR Pass Revoked"
                      : isExpired
                      ? "QR Pass Expired"
                      : "QR Pass Already Used"}
                  </p>
                  <p className="text-[11px] text-slate-500 mt-1">
                    {isCancelled
                      ? "Fare refunded to your Metro Card."
                      : "No longer valid at AFC entry gates."}
                  </p>
                </div>
              )}
            </div>
            <p className="text-[11px] text-slate-500 mt-2.5 text-center">
              {isConfirmed
                ? "Valid for Metro Gate Entry"
                : isCancelled
                ? "Ticket Voided / Cancelled"
                : isExpired
                ? "Ticket Expired"
                : "Journey Completed"}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookingDetailsPage;