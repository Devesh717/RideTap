import React from "react";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="bg-slate-900 text-slate-400 border-t border-slate-800 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <span className="w-7 h-7 rounded-lg bg-teal-600 text-white flex items-center justify-center font-bold text-sm">
              R
            </span>
            <span className="text-white font-bold text-base">RideTap Metro</span>
          </div>

          <div className="flex flex-wrap items-center gap-6 text-xs font-medium">
            <Link to="/" className="text-slate-400 hover:text-white no-underline">
              Home
            </Link>
            <Link to="/stations" className="text-slate-400 hover:text-white no-underline">
              Stations &amp; Fare
            </Link>
            <Link to="/book-ride" className="text-slate-400 hover:text-white no-underline">
              Book Ride
            </Link>
            <Link to="/ai-assistant" className="text-slate-400 hover:text-white no-underline">
              AI Concierge
            </Link>
          </div>

          <p className="text-xs text-slate-500">
            &copy; {new Date().getFullYear()} RideTap Transit Systems.
          </p>
        </div>

        {/* Educational & Non-Affiliation Disclaimer */}
        <div className="mt-6 pt-6 border-t border-slate-800/80 text-center">
          <p className="text-xs font-bold text-slate-300">
            Personal educational project for route simulation only. Not affiliated with DMRC, MMRDA, BMRCL, or any official metro transit authority.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;