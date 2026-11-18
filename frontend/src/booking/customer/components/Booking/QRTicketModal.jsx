import React from "react";

const QRTicketModal = ({ booking, open, onClose }) => {
  if (!open || !booking) return null;

  const bookingId = booking.id || booking.bookingId;
  const origin =
    booking.originStation?.name ||
    booking.originStation ||
    booking.originStationName ||
    "Origin";
  const destination =
    booking.destinationStation?.name ||
    booking.destinationStation ||
    booking.destinationStationName ||
    "Destination";

  const qrPayload = JSON.stringify({ bookingId });
  const rawQr = booking.qrCodePath || booking.qrPath || booking.qrCodeData || "";
  const qrImageSrc =
    rawQr.startsWith("data:image") || rawQr.startsWith("http")
      ? rawQr
      : `https://api.qrserver.com/v1/create-qr-code/?size=220x220&margin=8&data=${encodeURIComponent(
          qrPayload
        )}`;

  const expiresAtDate = booking.expiresAt
    ? new Date(booking.expiresAt)
    : booking.dateTime
    ? new Date(new Date(booking.dateTime).getTime() + 3 * 60 * 60 * 1000)
    : null;

  const isExpired =
    Boolean(booking.expired) ||
    booking.bookingStatus === "EXPIRED" ||
    (expiresAtDate && new Date() > expiresAtDate);

  const isCancelled = booking.bookingStatus === "CANCELLED";
  const isUsed = booking.bookingStatus === "USED";
  const isInactive = isExpired || isCancelled || isUsed;

  const effectiveStatus = isExpired
    ? "EXPIRED"
    : booking.bookingStatus || booking.status || "CONFIRMED";

  const formattedIssued = booking.dateTime
    ? new Date(booking.dateTime).toLocaleString("en-IN", {
        day: "numeric",
        month: "short",
        year: "numeric",
        hour: "numeric",
        minute: "2-digit",
        hour12: true,
      })
    : "";

  const formattedExpiry = expiresAtDate
    ? expiresAtDate.toLocaleString("en-IN", {
        day: "numeric",
        month: "short",
        hour: "numeric",
        minute: "2-digit",
        hour12: true,
      })
    : "";

  const badgeStyle = isExpired || isCancelled
    ? "bg-red-50 border-red-200 text-red-700"
    : isUsed
    ? "bg-slate-100 border-slate-300 text-slate-700"
    : "bg-emerald-50 border-emerald-200 text-emerald-700";

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-xs p-4"
      onClick={onClose}
    >
      <div
        className="w-full max-w-md rounded-3xl bg-white shadow-2xl overflow-hidden border border-slate-200"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div
          className={`px-6 py-5 text-white flex items-start justify-between ${
            isInactive
              ? "bg-gradient-to-r from-slate-900 via-slate-800 to-slate-700"
              : "bg-gradient-to-r from-teal-950 via-teal-900 to-teal-800"
          }`}
        >
          <div>
            <p className="text-[11px] font-bold uppercase tracking-widest text-teal-300">
              RideTap Metro Gate Pass
            </p>
            <h2 className="text-2xl font-extrabold mt-0.5">
              Ticket #{bookingId}
            </h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="text-white/80 hover:text-white text-xl leading-none bg-transparent border-0 cursor-pointer p-1"
            aria-label="Close modal"
          >
            &times;
          </button>
        </div>

        {/* Origin -> Destination */}
        <div className="px-6 pt-5">
          <div className="rounded-2xl border border-slate-200 bg-slate-50/70 px-4 py-3.5 flex items-center justify-between gap-3">
            <div className="min-w-0">
              <p className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                Origin
              </p>
              <p className="text-sm font-extrabold text-slate-900 truncate mt-0.5">
                {origin}
              </p>
            </div>

            <span className="text-teal-600 font-bold text-base shrink-0">
              &rarr;
            </span>

            <div className="min-w-0 text-right">
              <p className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                Destination
              </p>
              <p className="text-sm font-extrabold text-slate-900 truncate mt-0.5">
                {destination}
              </p>
            </div>
          </div>
        </div>

        {/* QR Code Display (no raw bookingId text) */}
        <div className="px-6 py-5 flex flex-col items-center">
          <div
            className={`relative p-4 rounded-2xl border-2 border-dashed flex flex-col items-center ${
              isInactive
                ? "border-red-200 bg-red-50/30"
                : "border-teal-300 bg-teal-50/30"
            }`}
          >
            <div className="bg-white p-3 rounded-xl shadow-xs border border-slate-100">
              <img
                src={qrImageSrc}
                alt="Metro AFC Gate QR Pass"
                className={`w-44 h-44 object-contain block transition ${
                  isInactive ? "opacity-25 grayscale" : ""
                }`}
              />
            </div>

            {isInactive && (
              <div className="absolute inset-0 flex items-center justify-center">
                <span className="px-4 py-1.5 rounded-xl bg-red-600 text-white font-extrabold text-xs tracking-widest uppercase shadow-md">
                  {effectiveStatus}
                </span>
              </div>
            )}
          </div>

          <p className="text-xs text-slate-500 mt-3 text-center">
            {isExpired
              ? "This pass has expired and can no longer be scanned at AFC gates."
              : isCancelled
              ? "This pass was cancelled and refunded."
              : isUsed
              ? "This pass has already been used for travel."
              : "Scan this QR code at the Metro AFC entry & exit gates."}
          </p>
        </div>

        {/* Footer Metadata */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-200 flex items-center justify-between gap-2">
          <div>
            <p className="text-[10px] font-semibold uppercase text-slate-400">
              Fare Paid
            </p>
            <p className="text-sm font-extrabold text-slate-900">
              &#8377;{Number(booking.fare || 0).toFixed(2)}
            </p>
          </div>

          <div className="text-center">
            <p className="text-[10px] font-semibold uppercase text-slate-400">
              {formattedExpiry ? "Valid Until" : "Issued At"}
            </p>
            <p
              className={`text-xs font-bold ${
                isExpired ? "text-red-600" : "text-slate-700"
              }`}
            >
              {formattedExpiry || formattedIssued}
            </p>
          </div>

          <span
            className={`px-3 py-1 rounded-full border text-[11px] font-extrabold ${badgeStyle}`}
          >
            &bull; {effectiveStatus}
          </span>
        </div>
      </div>
    </div>
  );
};

export default QRTicketModal;
