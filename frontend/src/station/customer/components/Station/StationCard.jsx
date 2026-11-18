import React from "react";

const StationCard = ({ station, onSelectOrigin, onSelectDestination }) => {
  const lines = Array.isArray(station.lines) ? station.lines : [];
  const colors = Array.isArray(station.lineColors) ? station.lineColors : [];

  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-4 shadow-xs hover:shadow-md transition flex flex-col justify-between gap-3">
      <div>
        <div className="flex items-start justify-between gap-2">
          <h3 className="text-sm font-extrabold text-slate-900 leading-snug">
            {station.name}
          </h3>
          {station.interchange && (
            <span className="shrink-0 px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-amber-100 text-amber-800 border border-amber-200">
              Interchange
            </span>
          )}
        </div>

        {/* Color Line Badges */}
        <div className="flex flex-wrap gap-1.5 mt-2.5">
          {lines.map((lineName, idx) => {
            const hex = colors[idx] || "#0D9488";
            return (
              <span
                key={`${lineName}-${idx}`}
                className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-bold text-white shadow-2xs"
                style={{ backgroundColor: hex }}
              >
                <span className="w-1.5 h-1.5 rounded-full bg-white/90" />
                {lineName}
              </span>
            );
          })}
        </div>
      </div>

      {/* Quick Route Actions */}
      <div className="pt-2.5 border-t border-slate-100 flex items-center gap-2">
        <button
          type="button"
          onClick={() => onSelectOrigin && onSelectOrigin(station.name)}
          className="flex-1 py-1.5 px-2.5 rounded-lg bg-slate-100 hover:bg-teal-50 hover:text-teal-700 text-slate-700 text-[11px] font-bold border-0 cursor-pointer transition"
        >
          Set Origin
        </button>
        <button
          type="button"
          onClick={() => onSelectDestination && onSelectDestination(station.name)}
          className="flex-1 py-1.5 px-2.5 rounded-lg bg-slate-100 hover:bg-slate-900 hover:text-white text-slate-700 text-[11px] font-bold border-0 cursor-pointer transition"
        >
          Set Dest
        </button>
      </div>
    </div>
  );
};

export default StationCard;
