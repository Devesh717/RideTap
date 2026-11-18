import React from "react";
import { Link } from "react-router-dom";
import FareCalculatorCard from "../../../../station/customer/components/Fare/FareCalculatorCard";

const HeroSection = () => {
  return (
    <section className="bg-gradient-to-br from-slate-900 via-slate-800 to-teal-900 text-white py-14 sm:py-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 items-center">
          {/* Left Copy */}
          <div className="lg:col-span-7">
            <span className="inline-flex items-center gap-2 bg-teal-500/20 border border-teal-400/30 text-teal-200 text-xs font-semibold px-3.5 py-1.5 rounded-full mb-5">
              Tap. Ride. Arrive Seamlessly.
            </span>
            <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight leading-tight">
              Your Smart Metro Card &amp;{" "}
              <span className="text-teal-400">Instant QR Ticketing</span>
            </h1>
            <p className="text-slate-300 text-base sm:text-lg mt-4 max-w-2xl leading-relaxed">
              Skip ticket queues with RideTap. Check live station fares, top up your digital Metro
              Wallet, generate instant QR entry passes, or book rides hands-free using our AI Transit
              Concierge.
            </p>

            <div className="flex flex-wrap items-center gap-3.5 mt-8">
              <Link
                to="/book-ride"
                className="px-6 py-3.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-sm shadow-lg transition no-underline"
              >
                Book a QR Ticket
              </Link>
              <Link
                to="/stations"
                className="px-6 py-3.5 rounded-xl bg-white/10 hover:bg-white/15 border border-white/20 text-white font-semibold text-sm transition no-underline"
              >
                Explore All Stations
              </Link>
            </div>

            <div className="grid grid-cols-3 gap-4 mt-10 pt-8 border-t border-white/10 max-w-lg">
              <div>
                <p className="text-2xl font-extrabold text-white">Instant</p>
                <p className="text-xs text-slate-400 mt-0.5">QR Gate Passes</p>
              </div>
              <div>
                <p className="text-2xl font-extrabold text-teal-400">AI-Powered</p>
                <p className="text-xs text-slate-400 mt-0.5">Voice/Chat Booking</p>
              </div>
              <div>
                <p className="text-2xl font-extrabold text-white">Zero Queue</p>
                <p className="text-xs text-slate-400 mt-0.5">Digital Metro Wallet</p>
              </div>
            </div>
          </div>

          {/* Right Interactive Fare Widget */}
          <div className="lg:col-span-5 text-slate-900">
            <FareCalculatorCard />
          </div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
