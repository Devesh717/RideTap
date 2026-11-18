import React, { useEffect, useState, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchAllStations } from "../../../state/Station/Action";
import StationCard from "../../components/Station/StationCard";
import FareCalculatorCard from "../../components/Fare/FareCalculatorCard";

const CITY_CONFIG = {
  "Delhi NCR": {
    label: "Delhi NCR Metro",
    subtitle: "Delhi NCR Rapid Transit Graph",
    placeholder: "Search station (e.g. Rajiv Chowk, Hauz Khas, Delta 1)...",
    lines: [
      { label: "All Lines", value: "ALL", color: "#0F172A" },
      { label: "Red Line", value: "Red Line", color: "#E53935" },
      { label: "Yellow Line", value: "Yellow Line", color: "#EAB308" },
      { label: "Blue Line", value: "Blue Line", color: "#1E88E5" },
      { label: "Green Line", value: "Green Line", color: "#43A047" },
      { label: "Violet Line", value: "Violet Line", color: "#8E24AA" },
      { label: "Pink Line", value: "Pink Line", color: "#EC407A" },
      { label: "Magenta Line", value: "Magenta Line", color: "#D81B60" },
      { label: "Airport Express", value: "Orange Line", color: "#FB8C00" },
      { label: "Grey Line", value: "Grey Line", color: "#78909C" },
      { label: "Aqua Line", value: "Aqua Line", color: "#00ACC1" },
      { label: "Rapid Metro", value: "Rapid Metro", color: "#26A69A" },
    ],
  },
  Mumbai: {
    label: "Mumbai Metro",
    subtitle: "MMRDA & Mumbai Monorail Network",
    placeholder: "Search station (e.g. Andheri, Ghatkopar, Churchgate)...",
    lines: [
      { label: "All Lines", value: "ALL", color: "#0F172A" },
      { label: "Line 1 (Blue)", value: "Mumbai Metro Line 1", color: "#EE3124" },
      { label: "Line 2A (Yellow)", value: "Mumbai Metro Line 2A", color: "#EAB308" },
      { label: "Line 3 (Aqua)", value: "Mumbai Metro Line 3", color: "#00A859" },
      { label: "Line 7 (Red)", value: "Mumbai Metro Line 7", color: "#E53935" },
      { label: "Monorail", value: "Mumbai Monorail", color: "#008000" },
    ],
  },
  Bengaluru: {
    label: "Bengaluru Namma Metro",
    subtitle: "BMRCL Rapid Transit Network",
    placeholder: "Search station (e.g. Majestic, Indiranagar, Whitefield)...",
    lines: [
      { label: "All Lines", value: "ALL", color: "#0F172A" },
      { label: "Purple Line", value: "Purple Line", color: "#68228B" },
      { label: "Green Line", value: "Green Line", color: "#008000" },
      { label: "Yellow Line", value: "Yellow Line", color: "#F58220" },
    ],
  },
  Kolkata: {
    label: "Kolkata Metro",
    subtitle: "India's First Rapid Transit Network",
    placeholder: "Search station (e.g. Esplanade, Howrah Maidan, Kavi Subhash)...",
    lines: [
      { label: "All Lines", value: "ALL", color: "#0F172A" },
      { label: "Line 1 (Blue)", value: "Line 1 (Blue)", color: "#00539F" },
      { label: "Line 2 (Green)", value: "Line 2 (Green)", color: "#00A859" },
      { label: "Line 6 (Orange)", value: "Line 6 (Orange)", color: "#FF8C00" },
    ],
  },
  Chennai: {
    label: "Chennai Metro",
    subtitle: "CMRL Rapid Transit Network",
    placeholder: "Search station (e.g. Alandur, Chennai Central, Guindy)...",
    lines: [
      { label: "All Lines", value: "ALL", color: "#0F172A" },
      { label: "Blue Line", value: "Chennai Metro Blue Line", color: "#00539F" },
      { label: "Green Line", value: "Chennai Metro Green Line", color: "#008000" },
    ],
  },
  Hyderabad: {
    label: "Hyderabad Metro",
    subtitle: "HMRL Elevated Rapid Transit",
    placeholder: "Search station (e.g. Ameerpet, MGBS, Hitec City)...",
    lines: [
      { label: "All Lines", value: "ALL", color: "#0F172A" },
      { label: "Red Line", value: "Hyderabad Metro Red Line", color: "#E53935" },
      { label: "Blue Line", value: "Hyderabad Metro Blue Line", color: "#1E88E5" },
      { label: "Green Line", value: "Hyderabad Metro Green Line", color: "#43A047" },
    ],
  },
  Pune: {
    label: "Pune Metro",
    subtitle: "Maha-Metro Pune Network",
    placeholder: "Search station (e.g. Civil Court, Vanaz, Swargate)...",
    lines: [
      { label: "All Lines", value: "ALL", color: "#0F172A" },
      { label: "Aqua Line", value: "Pune Metro Aqua Line", color: "#00ACC1" },
      { label: "Purple Line", value: "Pune Metro Purple Line", color: "#8E24AA" },
    ],
  },
};

const CITY_KEYS = Object.keys(CITY_CONFIG);

const StationsPage = () => {
  const dispatch = useDispatch();
  const { stations = [], loading, error } = useSelector((state) => state.station);

  const [selectedCity, setSelectedCity] = useState("Delhi NCR");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedLine, setSelectedLine] = useState("ALL");
  const [onlyInterchanges, setOnlyInterchanges] = useState(false);
  const [selectedOrigin, setSelectedOrigin] = useState("");
  const [selectedDest, setSelectedDest] = useState("");

  useEffect(() => {
    dispatch(fetchAllStations());
  }, [dispatch]);

  const handleCityChange = (cityKey) => {
    setSelectedCity(cityKey);
    setSelectedLine("ALL");
    setSearchQuery("");
    setSelectedOrigin("");
    setSelectedDest("");
  };

  // Filter stations belonging to the currently selected city
  const cityStations = useMemo(() => {
    const target = selectedCity.toLowerCase().trim();
    return stations.filter((st) => {
      const dbCity = (st.city || "Delhi NCR").toLowerCase().trim();
      if (target === "delhi ncr") return dbCity.includes("delhi") || dbCity.includes("ncr");
      if (target === "bengaluru") return dbCity.includes("bengaluru") || dbCity.includes("bangalore");
      return dbCity === target;
    });
  }, [stations, selectedCity]);

  const filteredStations = useMemo(() => {
    return cityStations.filter((st) => {
      const matchesName = st.name
        ?.toLowerCase()
        .includes(searchQuery.trim().toLowerCase());
      const matchesLine =
        selectedLine === "ALL" ||
        (Array.isArray(st.lines) &&
          st.lines.some((l) =>
            l.toLowerCase().includes(selectedLine.toLowerCase())
          ));
      const matchesInterchange = !onlyInterchanges || Boolean(st.interchange);
      return matchesName && matchesLine && matchesInterchange;
    });
  }, [cityStations, searchQuery, selectedLine, onlyInterchanges]);

  const currentCityMeta = CITY_CONFIG[selectedCity] || CITY_CONFIG["Delhi NCR"];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Dynamic Header */}
      <div className="mb-6">
        <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
          {currentCityMeta.subtitle}
        </span>
        <h1 className="text-3xl font-extrabold text-slate-900 mt-1">
          {currentCityMeta.label} Stations &amp; Routes
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Explore <strong className="text-slate-900">{cityStations.length}</strong> stations in{" "}
          {currentCityMeta.label} ({stations.length} total across {CITY_KEYS.length} cities) and compute optimal routes with interchanges.
        </p>
      </div>

      {/* Multi-City Selector Tabs */}
      <div className="flex flex-wrap items-center gap-2 mb-4 pb-4 border-b border-slate-200">
        {CITY_KEYS.map((cityKey) => {
          const isSelected = selectedCity === cityKey;
          return (
            <button
              key={cityKey}
              type="button"
              onClick={() => handleCityChange(cityKey)}
              className={`px-4 py-2 rounded-xl text-xs font-extrabold transition cursor-pointer border ${
                isSelected
                  ? "bg-slate-900 text-white border-slate-900 shadow-sm"
                  : "bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100"
              }`}
            >
              {CITY_CONFIG[cityKey].label}
            </button>
          );
        })}
      </div>

      {/* City-Specific Color Line Filter Chips */}
      <div className="flex flex-wrap items-center gap-2 mb-6">
        {currentCityMeta.lines.map((lf) => {
          const active = selectedLine === lf.value;
          return (
            <button
              key={lf.value}
              type="button"
              onClick={() => setSelectedLine(lf.value)}
              className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-xs font-bold transition cursor-pointer border ${
                active
                  ? "text-white border-transparent shadow-xs"
                  : "bg-white text-slate-700 border-slate-200 hover:bg-slate-100"
              }`}
              style={active ? { backgroundColor: lf.color } : undefined}
            >
              <span
                className="w-2.5 h-2.5 rounded-full shrink-0"
                style={{ backgroundColor: active ? "#FFFFFF" : lf.color }}
              />
              {lf.label}
            </button>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-7 items-start">
        <div className="lg:col-span-5 lg:sticky lg:top-20 lg:max-h-[calc(100vh-6rem)] lg:overflow-y-auto lg:pr-1">
          <FareCalculatorCard
            stations={stations}
            externalOrigin={selectedOrigin}
            externalDestination={selectedDest}
          />
        </div>

        {/* Right: Searchable Station Directory */}
        <div className="lg:col-span-7 space-y-4">
          <div className="bg-white rounded-2xl border border-slate-200 p-4 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <input
              type="text"
              placeholder={currentCityMeta.placeholder}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="flex-1 px-3.5 py-2 border border-slate-300 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-teal-600"
            />

            <label className="inline-flex items-center gap-2 text-xs font-semibold text-slate-700 cursor-pointer select-none">
              <input
                type="checkbox"
                checked={onlyInterchanges}
                onChange={(e) => setOnlyInterchanges(e.target.checked)}
                className="rounded text-teal-600"
              />
              Interchanges Only
            </label>
          </div>

          <div className="flex items-center justify-between text-xs text-slate-500 px-1">
            <span>
              Showing <strong className="text-slate-900">{filteredStations.length}</strong> of{" "}
              {cityStations.length} stations in {currentCityMeta.label}
            </span>
            {(searchQuery || selectedLine !== "ALL" || onlyInterchanges) && (
              <button
                type="button"
                onClick={() => {
                  setSearchQuery("");
                  setSelectedLine("ALL");
                  setOnlyInterchanges(false);
                }}
                className="text-teal-600 font-bold bg-transparent border-0 cursor-pointer"
              >
                Reset Filters
              </button>
            )}
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-xl text-xs">
              {error}
            </div>
          )}

          {loading ? (
            <div className="bg-white rounded-2xl border border-slate-200 p-10 text-center text-sm text-slate-500">
              Loading {currentCityMeta.label} network...
            </div>
          ) : filteredStations.length === 0 ? (
            <div className="bg-white rounded-2xl border border-slate-200 p-10 text-center text-sm text-slate-500">
              No stations match your filter criteria in {currentCityMeta.label}.
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
              {filteredStations.map((station) => (
                <StationCard
                  key={station.id}
                  station={station}
                  onSelectOrigin={(name) => setSelectedOrigin(name)}
                  onSelectDestination={(name) => setSelectedDest(name)}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default StationsPage;