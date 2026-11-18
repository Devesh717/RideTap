import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchAllStations } from "../../../station/state/Station/Action";
import {
  addAdminStation,
  updateAdminStation,
  deleteAdminStation,
} from "../../state/Admin/Action";

const StationManagerTab = () => {
  const dispatch = useDispatch();
  const { stations, loading } = useSelector((state) => state.station);
  const { mutatingStation } = useSelector((state) => state.admin);

  const [mode, setMode] = useState("add"); // "add" | "update"
  const [name, setName] = useState("");
  const [sequenceNumber, setSequenceNumber] = useState("");

  useEffect(() => {
    if (stations.length === 0) {
      dispatch(fetchAllStations());
    }
  }, [dispatch, stations.length]);

  const handleEditClick = (station) => {
    setMode("update");
    setName(station.name);
    setSequenceNumber(String(station.sequenceNumber));
  };

  const resetForm = () => {
    setMode("add");
    setName("");
    setSequenceNumber("");
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = {
      name: name.trim(),
      sequenceNumber: Number(sequenceNumber),
    };

    if (mode === "update") {
      dispatch(updateAdminStation(payload, resetForm));
    } else {
      dispatch(addAdminStation(payload, resetForm));
    }
  };

  const handleDelete = (stationName) => {
    if (window.confirm(`Delete metro station "${stationName}"?`)) {
      dispatch(deleteAdminStation(stationName));
    }
  };

  return (
    <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
      {/* Left: Add / Update Station Form */}
      <div className="lg:col-span-4 bg-white rounded-2xl border border-slate-200 p-6 shadow-xs">
        <div className="flex items-center justify-between mb-3">
          <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
            {mode === "update" ? "Modify Station" : "New Station"}
          </span>
          {mode === "update" && (
            <button
              type="button"
              onClick={resetForm}
              className="text-xs font-semibold text-slate-500 hover:text-slate-800 bg-transparent border-0 cursor-pointer"
            >
              + Switch to Add
            </button>
          )}
        </div>

        <h3 className="text-lg font-bold text-slate-900 mb-4">
          {mode === "update" ? "Update Station Sequence" : "Add Metro Station"}
        </h3>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div className="flex flex-col gap-1.5">
            <label className="text-xs font-semibold text-slate-700">Station Name</label>
            <input
              type="text"
              placeholder="e.g. Rajiv Chowk"
              value={name}
              onChange={(e) => setName(e.target.value)}
              readOnly={mode === "update"}
              required
              className={`px-3.5 py-2.5 border border-slate-300 rounded-xl text-sm text-slate-900 focus:outline-none focus:border-teal-600 ${
                mode === "update" ? "bg-slate-100 text-slate-600" : "bg-white"
              }`}
            />
          </div>

          <div className="flex flex-col gap-1.5">
            <label className="text-xs font-semibold text-slate-700">
              Route Sequence Number
            </label>
            <input
              type="number"
              min="1"
              placeholder="e.g. 5"
              value={sequenceNumber}
              onChange={(e) => setSequenceNumber(e.target.value)}
              required
              className="px-3.5 py-2.5 border border-slate-300 rounded-xl text-sm text-slate-900 focus:outline-none focus:border-teal-600"
            />
          </div>

          <button
            type="submit"
            disabled={mutatingStation}
            className="w-full py-2.5 px-4 rounded-xl bg-teal-600 hover:bg-teal-700 disabled:opacity-60 text-white font-semibold text-sm transition cursor-pointer border-0"
          >
            {mutatingStation
              ? "Saving..."
              : mode === "update"
              ? "Update Station"
              : "Add Station"}
          </button>
        </form>
      </div>

      {/* Right: Stations Table */}
      <div className="lg:col-span-8 bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-xs">
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 className="text-base font-bold text-slate-900">
            Active Line Stations ({stations.length})
          </h3>
          <button
            type="button"
            onClick={() => dispatch(fetchAllStations())}
            className="text-xs font-semibold text-teal-600 hover:text-teal-700 bg-transparent border-0 cursor-pointer"
          >
            Refresh
          </button>
        </div>

        {loading ? (
          <div className="p-8 text-center text-sm text-slate-500">Loading stations...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="bg-slate-50 text-slate-500 text-xs uppercase tracking-wider border-b border-slate-200">
                  <th className="py-3 px-5">Seq #</th>
                  <th className="py-3 px-5">Station Name</th>
                  <th className="py-3 px-5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {stations.map((st) => (
                  <tr key={st.id} className="hover:bg-slate-50/80">
                    <td className="py-3 px-5 font-bold text-teal-700">#{st.sequenceNumber}</td>
                    <td className="py-3 px-5 font-semibold text-slate-900">{st.name}</td>
                    <td className="py-3 px-5 text-right space-x-2">
                      <button
                        type="button"
                        onClick={() => handleEditClick(st)}
                        className="px-3 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold cursor-pointer border-0"
                      >
                        Edit Seq
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDelete(st.name)}
                        className="px-3 py-1 rounded-lg bg-rose-50 hover:bg-rose-100 text-rose-700 text-xs font-semibold cursor-pointer border border-rose-200"
                      >
                        Delete
                      </button>
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

export default StationManagerTab;
