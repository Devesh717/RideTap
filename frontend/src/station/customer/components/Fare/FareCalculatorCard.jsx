import React, { useState, useEffect, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { calculateShortestRoute, clearRouteResult, fetchAllStations } from "../../../state/Station/Action";

const CITIES = [
  { label: "Delhi NCR Metro", value: "Delhi NCR" },
  { label: "Mumbai Metro", value: "Mumbai" },
  { label: "Bengaluru Namma Metro", value: "Bengaluru" },
  { label: "Kolkata Metro", value: "Kolkata" },
  { label: "Chennai Metro", value: "Chennai" },
  { label: "Hyderabad Metro", value: "Hyderabad" },
  { label: "Pune Metro", value: "Pune" },
];

const FareCalculatorCard = ({
  stations: propStations = [],
  externalOrigin = "",
  externalDestination = "",
}) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Pull stations and loading state from Redux store as a robust fallback
  const { stations: storeStations = [], loading: stationsLoading } = useSelector(
    (state) => state.station || {}
  );
  
  const stations = propStations.length > 0 ? propStations : storeStations;

  const { routeResult, calculatingRoute, routeError } = useSelector(
    (state) => state.station
  );

  const [selectedCity, setSelectedCity] = useState("Delhi NCR");
  const [origin, setOrigin] = useState("");
  const [destination, setDestination] = useState("");

  // Fetch stations on mount if Redux store is empty
  useEffect(() => {
    if ((!storeStations || storeStations.length === 0) && typeof fetchAllStations === "function") {
      dispatch(fetchAllStations());
    }
  }, [dispatch, storeStations]);

  // Filter stations strictly by selected city with smart keyword fallback matching
  const cityStations = useMemo(() => {
    if (!Array.isArray(stations) || stations.length === 0) return [];
    
    const target = selectedCity.toLowerCase().trim();

    return stations.filter((st) => {
      const dbCity = (st.city || "").toLowerCase().trim();
      const linesText = (Array.isArray(st.lines) ? st.lines : []).join(" ").toLowerCase();
      const nameText = (st.name || "").toLowerCase();

      // 1. Direct match if dbCity is explicitly populated
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

      // 2. Fallback keyword matching based on line names/colors if dbCity is empty
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

      // Default fallback for Delhi NCR
      const isOtherCity = linesText.includes("mumbai") || linesText.includes("namma") || linesText.includes("kolkata") || linesText.includes("chennai") || linesText.includes("hyderabad") || linesText.includes("pune");
      return !isOtherCity;
    });
  }, [stations, selectedCity]);

  // Sort city-filtered stations alphabetically for easy dropdown selection
  // Sort and deduplicate city-filtered stations by station name
  const sortedStations = useMemo(() => {
    const uniqueMap = new Map();

    cityStations.forEach((st) => {
      if (!st.name) return;
      const key = st.name.trim().toLowerCase();

      if (!uniqueMap.has(key)) {
        uniqueMap.set(key, {
          ...st,
          lines: Array.isArray(st.lines) ? [...st.lines] : [],
        });
      } else {
        // Merge any lines from duplicate DB rows so all interchange lines still show
        const existing = uniqueMap.get(key);
        const incomingLines = Array.isArray(st.lines) ? st.lines : [];
        incomingLines.forEach((line) => {
          if (!existing.lines.includes(line)) {
            existing.lines.push(line);
          }
        });
      }
    });

    return Array.from(uniqueMap.values()).sort((a, b) =>
      (a.name || "").localeCompare(b.name || "")
    );
  }, [cityStations]);

  // Reset origin/destination when city changes or set sensible defaults per city
  useEffect(() => {
    if (sortedStations.length > 0) {
      const originExists = sortedStations.some((s) => s.name === origin);
      const destExists = sortedStations.some((s) => s.name === destination);

      if (!origin || !originExists) {
        setOrigin(sortedStations[0].name);
      }
      if (!destination || !destExists) {
        setDestination(sortedStations[Math.min(1, sortedStations.length - 1)].name);
      }
    } else {
      setOrigin("");
      setDestination("");
    }
    dispatch(clearRouteResult());
  }, [selectedCity, sortedStations, dispatch]);

  useEffect(() => {
    if (externalOrigin) setOrigin(externalOrigin);
  }, [externalOrigin]);

  useEffect(() => {
    if (externalDestination) setDestination(externalDestination);
  }, [externalDestination]);

  const handleCalculate = (e) => {
    e.preventDefault();
    if (!origin || !destination) return;
    dispatch(calculateShortestRoute(origin, destination));
  };

  const handleSwap = () => {
    setOrigin(destination);
    setDestination(origin);
    dispatch(clearRouteResult());
  };

  return (
    <div className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm">
      <h2 className="text-xl font-extrabold text-slate-900">
        Route, Interchange &amp; Fare Finder
      </h2>
      <p className="text-xs text-slate-500 mt-1 mb-5">
        Finds the minimum-stop path across multi-city metro networks with interchange alerts.
      </p>

      <form onSubmit={handleCalculate} className="space-y-3.5">
        {/* City Selector Dropdown */}
        <div>
          <label
            htmlFor="transit-city-select"
            className="block text-xs font-semibold text-slate-700 mb-1"
          >
            Select Transit City
          </label>
          <select
            id="transit-city-select"
            value={selectedCity}
            onChange={(e) => setSelectedCity(e.target.value)}
            className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm font-bold text-slate-900 focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20 cursor-pointer"
          >
            {CITIES.map((c) => (
              <option key={c.value} value={c.value}>
                {c.label}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label
            htmlFor="route-origin-select"
            className="block text-xs font-semibold text-slate-700 mb-1"
          >
            Origin Station
          </label>
          <select
            id="route-origin-select"
            value={origin}
            onChange={(e) => setOrigin(e.target.value)}
            required
            disabled={stationsLoading || sortedStations.length === 0}
            className="w-full px-3.5 py-2.5 bg-white border border-slate-300 rounded-xl text-sm text-slate-900 focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20 cursor-pointer disabled:bg-slate-100"
          >
            <option value="">
              {stationsLoading ? "Loading stations..." : "-- Select Origin Station --"}
            </option>
            {sortedStations.map((st) => {
              const lineDisplay = Array.isArray(st.lines) && st.lines.length > 0
                ? st.lines.join(" / ")
                : Array.isArray(st.lineColors) && st.lineColors.length > 0
                  ? st.lineColors.join(" / ")
                  : "";
              return (
                <option key={st.id || st.name} value={st.name}>
                  {st.name} {lineDisplay ? `(${lineDisplay})` : ""}
                </option>
              );
            })}
          </select>
        </div>

        <div className="flex justify-center">
          <button
            type="button"
            onClick={handleSwap}
            className="px-3 py-1 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-bold border-0 cursor-pointer transition"
          >
            &#8645; Swap Stations
          </button>
        </div>

        <div>
          <label
            htmlFor="route-destination-select"
            className="block text-xs font-semibold text-slate-700 mb-1"
          >
            Destination Station
          </label>
          <select
            id="route-destination-select"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            required
            disabled={stationsLoading || sortedStations.length === 0}
            className="w-full px-3.5 py-2.5 bg-white border border-slate-300 rounded-xl text-sm text-slate-900 focus:outline-none focus:border-teal-600 focus:ring-2 focus:ring-teal-500/20 cursor-pointer disabled:bg-slate-100"
          >
            <option value="">
              {stationsLoading ? "Loading stations..." : "-- Select Destination Station --"}
            </option>
            {sortedStations.map((st) => {
              const lineDisplay = Array.isArray(st.lines) && st.lines.length > 0
                ? st.lines.join(" / ")
                : Array.isArray(st.lineColors) && st.lineColors.length > 0
                  ? st.lineColors.join(" / ")
                  : "";
              return (
                <option key={st.id || st.name} value={st.name}>
                  {st.name} {lineDisplay ? `(${lineDisplay})` : ""}
                </option>
              );
            })}
          </select>
        </div>

        <button
          type="submit"
          disabled={calculatingRoute || !origin || !destination || stationsLoading}
          className="w-full py-3 px-4 rounded-xl bg-gradient-to-r from-slate-900 to-teal-700 hover:from-slate-800 hover:to-teal-600 text-white font-bold text-sm border-0 cursor-pointer shadow-sm transition disabled:opacity-60"
        >
          {calculatingRoute ? "Computing Shortest Path..." : "Find Route & Fare"}
        </button>
      </form>

      {routeError && (
        <div className="mt-4 bg-red-50 border border-red-200 text-red-700 px-3.5 py-2.5 rounded-xl text-xs">
          {routeError}
        </div>
      )}

      {/* Dijkstra Shortest Route Output */}
      {routeResult && (
        <div className="mt-6 pt-5 border-t border-slate-200 space-y-4">
          {/* Summary Banner */}
          <div className="rounded-2xl bg-slate-900 text-white p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-[11px] uppercase tracking-wider text-teal-300 font-bold">
                  Optimal Fare
                </p>
                <p className="text-3xl font-extrabold mt-0.5">
                  &#8377;{Number(routeResult.fare ?? 0).toFixed(2)}
                </p>
              </div>

              <div className="text-right text-xs space-y-1">
                <p>
                  <strong className="text-teal-300">{routeResult.totalStops}</strong> Stops
                </p>
                <p>
                  <strong className="text-amber-300">{routeResult.interchanges}</strong>{" "}
                  {routeResult.interchanges === 1 ? "Interchange" : "Interchanges"}
                </p>
                <p className="text-slate-300">~{routeResult.estimatedMinutes} mins</p>
              </div>
            </div>

            {/* Lines Used Pills */}
            <div className="flex flex-wrap gap-1.5 mt-3 pt-3 border-t border-white/10">
              {(routeResult.linesUsed || []).map((line, i) => (
                <span
                  key={i}
                  className="px-2.5 py-0.5 rounded-full bg-white/15 text-[11px] font-semibold text-white"
                >
                  {line}
                </span>
              ))}
            </div>
          </div>

          {/* Step-by-Step Station Timeline */}
          <div>
            <p className="text-xs font-bold text-slate-700 mb-2.5">
              Station-by-Station Path ({routeResult.path?.length || 0} stations)
            </p>
            <div className="max-h-72 overflow-y-auto pr-1 space-y-0">
              {(routeResult.path || []).map((step, index) => {
                const isFirst = index === 0;
                const isLast = index === routeResult.path.length - 1;
                return (
                  <div key={`${step.stationName}-${index}`} className="flex items-stretch gap-3">
                    {/* Colored Line Track */}
                    <div className="flex flex-col items-center w-5 shrink-0">
                      <span
                        className={`w-3.5 h-3.5 rounded-full border-2 border-white shadow-xs mt-1 ${
                          isFirst || isLast ? "scale-125" : ""
                        }`}
                        style={{ backgroundColor: step.lineColor || "#0D9488" }}
                      />
                      {!isLast && (
                        <span
                          className="w-1 flex-1 my-0.5 rounded-full"
                          style={{ backgroundColor: step.lineColor || "#0D9488" }}
                        />
                      )}
                    </div>

                    {/* Station Info */}
                    <div className="pb-3 flex-1 min-w-0">
                      <div className="flex items-center justify-between gap-2">
                        <p
                          className={`text-xs truncate ${
                            isFirst || isLast || step.interchangeHere
                              ? "font-extrabold text-slate-900"
                              : "font-medium text-slate-700"
                          }`}
                        >
                          {step.stationName}
                        </p>
                        <span
                          className="text-[10px] font-bold px-2 py-0.5 rounded-full text-white shrink-0"
                          style={{ backgroundColor: step.lineColor || "#0D9488" }}
                        >
                          {step.line}
                        </span>
                      </div>

                      {step.interchangeHere && (
                        <p className="text-[11px] font-bold text-amber-700 bg-amber-50 border border-amber-200 rounded-lg px-2 py-1 mt-1">
                          &#8644; Change line here to {routeResult.path[index + 1]?.line}
                        </p>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <button
            type="button"
            onClick={() =>
              navigate(
                `/book-ride?origin=${encodeURIComponent(
                  routeResult.originStation
                )}&destination=${encodeURIComponent(routeResult.destinationStation)}`
              )
            }
            className="w-full py-2.5 px-4 rounded-xl bg-teal-600 hover:bg-teal-700 text-white text-xs font-bold border-0 cursor-pointer transition"
          >
            Book Ticket for This Route &rarr;
          </button>
        </div>
      )}
    </div>
  );
};

export default FareCalculatorCard;