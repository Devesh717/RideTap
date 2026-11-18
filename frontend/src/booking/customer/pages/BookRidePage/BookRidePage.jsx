import React, { useState, useEffect, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useSearchParams, Link } from "react-router-dom";
import {
  fetchAllStations,
  calculateFare,
  clearFare,
} from "../../../../station/state/Station/Action";
import { fetchBalance } from "../../../../wallet/state/Wallet/Action";
import { createBooking, clearBookingMessages, fetchUserBookings } from "../../../state/Booking/Action";
import { CREATE_BOOKING_SUCCESS } from "../../../state/Booking/ActionType";
import { initiateRazorpayPayment } from "../../../../payment/utils/openRazorpayCheckout";
import QRTicketModal from "../../components/Booking/QRTicketModal";

const CITIES = [
  { label: "Delhi NCR Metro", value: "Delhi NCR" },
  { label: "Mumbai Metro", value: "Mumbai" },
  { label: "Bengaluru Namma Metro", value: "Bengaluru" },
  { label: "Kolkata Metro", value: "Kolkata" },
  { label: "Chennai Metro", value: "Chennai" },
  { label: "Hyderabad Metro", value: "Hyderabad" },
  { label: "Pune Metro", value: "Pune" },
];

const BookRidePage = () => {
  const dispatch = useDispatch();
  const [searchParams] = useSearchParams();

  const { stations, fareResult, fareLoading } = useSelector((state) => state.station);
  const { balance } = useSelector((state) => state.wallet);
  const {
    bookingInProgress,
    error,
    insufficientBalanceError,
    successMessage,
    latestBookedTicket,
    bookings,
  } = useSelector((state) => state.booking);

  const [selectedCity, setSelectedCity] = useState(searchParams.get("city") || "Delhi NCR");
  const [originStation, setOriginStation] = useState(searchParams.get("origin") || "");
  const [destinationStation, setDestinationStation] = useState(
    searchParams.get("destination") || ""
  );
  const [paymentMethod, setPaymentMethod] = useState("METROCARD");
  const [razorpayLoading, setRazorpayLoading] = useState(false);
  const [localError, setLocalError] = useState("");
  const [qrModalOpen, setQrModalOpen] = useState(false);

  // Keep only stations belonging to the selected metro city.
  // The keyword fallback mirrors FareCalculatorCard for records without a city value.
  const cityStations = useMemo(() => {
    if (!Array.isArray(stations) || stations.length === 0) return [];

    const target = selectedCity.toLowerCase().trim();

    return stations.filter((st) => {
      const dbCity = (st.city || "").toLowerCase().trim();
      const linesText = (Array.isArray(st.lines) ? st.lines : []).join(" ").toLowerCase();
      const nameText = (st.name || "").toLowerCase();

      if (dbCity) {
        if (target === "delhi ncr") return dbCity.includes("delhi") || dbCity.includes("ncr");
        if (target === "mumbai") return dbCity.includes("mumbai");
        if (target === "bengaluru") return dbCity.includes("bengaluru") || dbCity.includes("bangalore");
        if (target === "kolkata") return dbCity.includes("kolkata");
        if (target === "chennai") return dbCity.includes("chennai");
        if (target === "hyderabad") return dbCity.includes("hyderabad");
        if (target === "pune") return dbCity.includes("pune");
        return dbCity === target;
      }

      if (target === "mumbai") {
        return linesText.includes("mumbai") || linesText.includes("monorail") || nameText.includes("versova") || nameText.includes("ghatkopar");
      }
      if (target === "bengaluru") {
        return linesText.includes("namma") || linesText.includes("purple") || linesText.includes("yellow") || nameText.includes("majestic");
      }
      if (target === "kolkata") {
        return linesText.includes("kolkata") || nameText.includes("howrah maidan");
      }
      if (target === "chennai") {
        return linesText.includes("chennai") || nameText.includes("wimco nagar");
      }
      if (target === "hyderabad") {
        return linesText.includes("hyderabad") || nameText.includes("miyapur") || nameText.includes("lb nagar");
      }
      if (target === "pune") {
        return linesText.includes("pune") || nameText.includes("vanaz") || nameText.includes("pcmc");
      }

      const isOtherCity = linesText.includes("mumbai") || linesText.includes("namma") ||
        linesText.includes("kolkata") || linesText.includes("chennai") ||
        linesText.includes("hyderabad") || linesText.includes("pune");
      return !isOtherCity;
    });
  }, [stations, selectedCity]);

  // Deduplicate station names and preserve their sequence order for the booking dropdowns.
  const sortedStations = useMemo(() => {
    const uniqueStations = new Map();

    cityStations.forEach((station) => {
      if (!station.name) return;
      const key = station.name.trim().toLowerCase();
      if (!uniqueStations.has(key)) {
        uniqueStations.set(key, station);
      }
    });

    return Array.from(uniqueStations.values()).sort(
      (a, b) => (a.sequenceNumber ?? 0) - (b.sequenceNumber ?? 0)
    );
  }, [cityStations]);

  // Keep selected stations valid when the city changes or stations finish loading.
  useEffect(() => {
    if (sortedStations.length === 0) {
      setOriginStation("");
      setDestinationStation("");
      dispatch(clearFare());
      return;
    }

    const originExists = sortedStations.some((station) => station.name === originStation);
    const destinationExists = sortedStations.some((station) => station.name === destinationStation);

    if (!originStation || !originExists) setOriginStation(sortedStations[0].name);
    if (!destinationStation || !destinationExists) {
      setDestinationStation(sortedStations[Math.min(1, sortedStations.length - 1)].name);
    }

    dispatch(clearFare());
    // Station selections are intentionally reconciled whenever the city-filtered list changes.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedCity, sortedStations, dispatch]);

  // Check if user currently has an active (CONFIRMED and not expired) ticket
  const activeTicket = useMemo(() => {
    if (!Array.isArray(bookings)) return null;
    return bookings.find(
      (b) =>
        String(b.bookingStatus || "").toUpperCase() === "CONFIRMED" && !b.expired
    );
  }, [bookings]);

  useEffect(() => {
    if (stations.length === 0) dispatch(fetchAllStations());
    dispatch(fetchBalance());
    dispatch(fetchUserBookings());
    return () => {
      dispatch(clearBookingMessages());
    };
  }, [dispatch, stations.length]);

  useEffect(() => {
    if (originStation && destinationStation && originStation !== destinationStation) {
      dispatch(calculateFare(originStation, destinationStation));
    } else {
      dispatch(clearFare());
    }
  }, [dispatch, originStation, destinationStation]);

  const handleSwap = () => {
    setOriginStation(destinationStation);
    setDestinationStation(originStation);
    setLocalError("");
  };

  const handleBookRide = async (e) => {
    e.preventDefault();
    setLocalError("");

    if (activeTicket) {
      setLocalError("Ticket is active now. You cannot book another.");
      return;
    }

    if (!originStation || !destinationStation) {
      setLocalError("Please select both origin and destination stations.");
      return;
    }
    if (originStation === destinationStation) {
      setLocalError("Origin and destination stations must be different.");
      return;
    }

    if (paymentMethod === "METROCARD") {
      dispatch(
        createBooking(
          { originStation, destinationStation, paymentMethod: "METROCARD" },
          () => {
            dispatch(fetchBalance());
            dispatch(fetchUserBookings());
            setQrModalOpen(true);
          }
        )
      );
      return;
    }

    setRazorpayLoading(true);
    await initiateRazorpayPayment({
      purpose: "TICKET_BOOKING",
      description: `Metro Ticket: ${originStation} to ${destinationStation}`,
      originStation,
      destinationStation,
      onSuccess: (paymentData) => {
        setRazorpayLoading(false);
        if (paymentData?.booking) {
          dispatch({
            type: CREATE_BOOKING_SUCCESS,
            payload: paymentData.booking,
          });
        }
        dispatch(fetchUserBookings());
        setQrModalOpen(true);
      },
      onError: (errMsg) => {
        setRazorpayLoading(false);
        setLocalError(errMsg);
      },
      onDismiss: () => {
        setRazorpayLoading(false);
      },
    });
  };

  const estimatedFare = fareResult?.fare ?? null;
  const hasEnoughBalance =
    balance !== null && estimatedFare !== null
      ? Number(balance) >= Number(estimatedFare)
      : true;

  const isSubmitting = bookingInProgress || razorpayLoading || !!activeTicket;

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 mb-8">
        <div>
          <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
            Instant Delhi Metro Ticketing
          </span>
          <h1 className="text-3xl font-extrabold text-slate-900 mt-1">Book a Metro Ride</h1>
          <p className="text-sm text-slate-600 mt-1">
            Pay using your RideTap Metro Card balance or pay directly online via Razorpay.
          </p>
        </div>

        <div className="flex items-center gap-3 bg-white border border-slate-200 rounded-2xl px-4 py-2.5 shadow-xs">
          <div>
            <p className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
              Card Balance
            </p>
            <p className="text-base font-extrabold text-slate-900">
              &#8377;{balance !== null ? Number(balance).toFixed(2) : "0.00"}
            </p>
          </div>
          <Link
            to="/recharge"
            className="px-3 py-1.5 rounded-xl bg-teal-50 hover:bg-teal-100 text-teal-700 text-xs font-bold transition no-underline border border-teal-200"
          >
            + Recharge Card
          </Link>
        </div>
      </div>

      {activeTicket && (
        <div className="bg-amber-50 border border-amber-200 text-amber-900 px-5 py-4 rounded-2xl mb-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-sm">
          <div>
            <span className="text-xs font-black uppercase tracking-wider px-2 py-0.5 rounded-full bg-amber-200/60 text-amber-900 inline-block mb-1">
              Active Ticket Exists
            </span>
            <p className="text-sm font-extrabold">
              Ticket is active now. You cannot book another. (#ID: {activeTicket.id})
            </p>
            <p className="text-xs text-amber-700 mt-0.5">
              Route: {activeTicket.originStation?.name} &rarr; {activeTicket.destinationStation?.name}
            </p>
          </div>
          <Link
            to="/bookings"
            className="px-4 py-2 rounded-xl bg-amber-600 hover:bg-amber-700 text-white font-bold text-xs no-underline shrink-0 text-center transition"
          >
            View Active Ticket &rarr;
          </Link>
        </div>
      )}

      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm p-6 sm:p-8">
        {(localError || error) && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-xs mb-5 flex items-center justify-between gap-3">
            <span>{localError || error}</span>
            {(insufficientBalanceError || !hasEnoughBalance) && (
              <div className="flex items-center gap-2 shrink-0">
                <button
                  type="button"
                  onClick={() => setPaymentMethod("RAZORPAY")}
                  className="px-3 py-1.5 rounded-lg bg-slate-900 text-white font-bold text-xs cursor-pointer border-0"
                >
                  Pay via Razorpay
                </button>
                <Link
                  to="/recharge"
                  className="px-3 py-1.5 rounded-lg bg-red-600 text-white font-bold text-xs no-underline"
                >
                  Recharge Card
                </Link>
              </div>
            )}
          </div>
        )}

        {successMessage && latestBookedTicket && (
          <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-xl text-xs mb-5 flex items-center justify-between gap-3">
            <span className="font-semibold">{successMessage}</span>
            <button
              type="button"
              onClick={() => setQrModalOpen(true)}
              className="px-3 py-1.5 rounded-lg bg-emerald-700 text-white font-bold text-xs shrink-0 cursor-pointer border-0"
            >
              View QR Pass
            </button>
          </div>
        )}

        <form onSubmit={handleBookRide} className="flex flex-col gap-5">
          <div className="flex flex-col gap-1.5">
            <label htmlFor="booking-city-select" className="text-xs font-semibold text-slate-700">
              Select Transit City
            </label>
            <select
              id="booking-city-select"
              value={selectedCity}
              onChange={(e) => setSelectedCity(e.target.value)}
              disabled={!!activeTicket}
              className="w-full px-3.5 py-3 border border-slate-300 rounded-xl text-sm font-bold text-slate-900 bg-slate-50 focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20 cursor-pointer disabled:bg-slate-100 disabled:text-slate-500"
            >
              {CITIES.map((city) => (
                <option key={city.value} value={city.value}>
                  {city.label}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-11 gap-4 items-end">
            <div className="md:col-span-5 flex flex-col gap-1.5">
              <label className="text-xs font-semibold text-slate-700">Origin Station</label>
              <select
                value={originStation}
                onChange={(e) => setOriginStation(e.target.value)}
                required
                disabled={!!activeTicket || sortedStations.length === 0}
                className="px-3.5 py-3 border border-slate-300 rounded-xl text-sm text-slate-900 bg-white focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20 disabled:bg-slate-100 disabled:text-slate-500"
              >
                <option value="">
                  {stations.length === 0 ? "Loading stations..." : "Select boarding station"}
                </option>
                {sortedStations.map((st) => (
                  <option key={st.id} value={st.name}>
                    #{st.sequenceNumber} — {st.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="md:col-span-1 flex justify-center">
              <button
                type="button"
                onClick={handleSwap}
                disabled={!!activeTicket}
                title="Swap Stations"
                className="w-10 h-10 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-sm flex items-center justify-center transition cursor-pointer border-0 disabled:opacity-40"
              >
                &#8646;
              </button>
            </div>

            <div className="md:col-span-5 flex flex-col gap-1.5">
              <label className="text-xs font-semibold text-slate-700">Destination Station</label>
              <select
                value={destinationStation}
                onChange={(e) => setDestinationStation(e.target.value)}
                required
                disabled={!!activeTicket || sortedStations.length === 0}
                className="px-3.5 py-3 border border-slate-300 rounded-xl text-sm text-slate-900 bg-white focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20 disabled:bg-slate-100 disabled:text-slate-500"
              >
                <option value="">
                  {stations.length === 0 ? "Loading stations..." : "Select alighting station"}
                </option>
                {sortedStations.map((st) => (
                  <option key={st.id} value={st.name}>
                    #{st.sequenceNumber} — {st.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-2">
              Choose Payment Method
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
              <button
                type="button"
                disabled={!!activeTicket}
                onClick={() => setPaymentMethod("METROCARD")}
                className={`text-left p-4 rounded-2xl border-2 transition cursor-pointer ${
                  paymentMethod === "METROCARD"
                    ? "border-teal-600 bg-teal-50/60"
                    : "border-slate-200 bg-white hover:border-slate-300"
                } ${activeTicket ? "opacity-60 cursor-not-allowed" : ""}`}
              >
                <div className="flex items-center justify-between">
                  <span className="text-sm font-extrabold text-slate-900">
                    RideTap Metro Card
                  </span>
                  <span
                    className={`w-4 h-4 rounded-full border-2 flex items-center justify-center ${
                      paymentMethod === "METROCARD" ? "border-teal-600" : "border-slate-300"
                    }`}
                  >
                    {paymentMethod === "METROCARD" && (
                      <span className="w-2 h-2 rounded-full bg-teal-600" />
                    )}
                  </span>
                </div>
                <p className="text-xs text-slate-600 mt-1">
                  Instant deduction • Balance:{" "}
                  <strong>&#8377;{balance !== null ? Number(balance).toFixed(2) : "0.00"}</strong>
                </p>
              </button>

              <button
                type="button"
                disabled={!!activeTicket}
                onClick={() => setPaymentMethod("RAZORPAY")}
                className={`text-left p-4 rounded-2xl border-2 transition cursor-pointer ${
                  paymentMethod === "RAZORPAY"
                    ? "border-teal-600 bg-teal-50/60"
                    : "border-slate-200 bg-white hover:border-slate-300"
                } ${activeTicket ? "opacity-60 cursor-not-allowed" : ""}`}
              >
                <div className="flex items-center justify-between">
                  <span className="text-sm font-extrabold text-slate-900">
                    Pay via Razorpay
                  </span>
                  <span
                    className={`w-4 h-4 rounded-full border-2 flex items-center justify-center ${
                      paymentMethod === "RAZORPAY" ? "border-teal-600" : "border-slate-300"
                    }`}
                  >
                    {paymentMethod === "RAZORPAY" && (
                      <span className="w-2 h-2 rounded-full bg-teal-600" />
                    )}
                  </span>
                </div>
                <p className="text-xs text-slate-600 mt-1">
                  UPI, Debit/Credit Card, NetBanking • No card balance needed
                </p>
              </button>
            </div>
          </div>

          <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <p className="text-xs font-semibold text-slate-500">Ticket Fare Summary</p>
              {fareLoading ? (
                <p className="text-sm font-semibold text-slate-600 mt-1">Calculating fare...</p>
              ) : estimatedFare !== null ? (
                <div className="flex items-baseline gap-2 mt-1">
                  <span className="text-3xl font-extrabold text-slate-900">
                    &#8377;{Number(estimatedFare).toFixed(2)}
                  </span>
                  <span className="text-xs text-slate-500">
                    Single Journey QR Pass ({paymentMethod === "RAZORPAY" ? "Razorpay" : "Metro Card"})
                  </span>
                </div>
              ) : (
                <p className="text-sm text-slate-400 mt-1">
                  Select origin and destination to preview fare
                </p>
              )}
            </div>

            {paymentMethod === "METROCARD" && !hasEnoughBalance && estimatedFare !== null && !activeTicket && (
              <div className="text-xs bg-amber-50 border border-amber-200 text-amber-800 px-3.5 py-2.5 rounded-xl">
                Balance is lower than ticket fare.{" "}
                <button
                  type="button"
                  onClick={() => setPaymentMethod("RAZORPAY")}
                  className="font-bold underline bg-transparent border-0 text-amber-900 cursor-pointer p-0 mr-2"
                >
                  Switch to Razorpay
                </button>
                or{" "}
                <Link to="/recharge" className="font-bold underline text-amber-900">
                  Recharge Card
                </Link>
              </div>
            )}
          </div>

          <div className="flex flex-col sm:flex-row items-center gap-3 pt-2">
            <button
              type="submit"
              disabled={isSubmitting}
              className={`w-full sm:flex-1 py-3.5 px-6 font-bold text-sm rounded-xl shadow-md transition cursor-pointer border-0 ${
                activeTicket
                  ? "bg-slate-300 text-slate-600 cursor-not-allowed"
                  : "bg-teal-600 hover:bg-teal-700 text-white"
              }`}
            >
              {activeTicket
                ? "Ticket is active now. You cannot book another."
                : isSubmitting
                ? paymentMethod === "RAZORPAY"
                  ? "Opening Razorpay..."
                  : "Issuing QR Ticket..."
                : paymentMethod === "RAZORPAY"
                ? `Pay ₹${estimatedFare !== null ? Number(estimatedFare).toFixed(2) : ""} via Razorpay`
                : "Confirm & Pay with Metro Card"}
            </button>

            <Link
              to="/bookings"
              className="w-full sm:w-auto text-center py-3.5 px-5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-sm transition no-underline"
            >
              View My Tickets
            </Link>
          </div>
        </form>
      </div>

      <QRTicketModal
        booking={latestBookedTicket}
        open={qrModalOpen}
        onClose={() => setQrModalOpen(false)}
      />
    </div>
  );
};

export default BookRidePage;