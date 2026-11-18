import React, { useState, useEffect, useRef, useMemo } from "react";

const getStepName = (step) => {
  if (!step) return "";
  if (typeof step === "string") return step;
  if (typeof step.stationName === "object") return step.stationName?.name || "";
  return step.stationName || step.name || "";
};

const LiveJourneyTracker = ({ path = [], onActiveStationChange }) => {
  const [isTracking, setIsTracking] = useState(false);
  const [currentIndex, setCurrentIndex] = useState(0);
  const lastNotifiedIndexRef = useRef(-1);

  useEffect(() => {
    if (onActiveStationChange) {
      onActiveStationChange(isTracking ? currentIndex : -1);
    }
  }, [isTracking, currentIndex, onActiveStationChange]);

  useEffect(() => {
    setIsTracking(false);
    setCurrentIndex(0);
    lastNotifiedIndexRef.current = -1;
  }, [path]);

  const stopTracking = () => {
    setIsTracking(false);
    setCurrentIndex(0);
    lastNotifiedIndexRef.current = -1;
  };

  const triggerAlertCue = (title, body) => {
    if ("vibrate" in navigator) {
      navigator.vibrate([200, 100, 200]);
    }
    if ("Notification" in window && Notification.permission === "granted") {
      try {
        new Notification(title, { body });
      } catch {
        // Ignore on browsers requiring service-worker notifications
      }
    }
  };

  const startTracking = () => {
    if (!path || path.length < 2) return;
    setIsTracking(true);
    setCurrentIndex(0);
    lastNotifiedIndexRef.current = 0;

    if ("Notification" in window && Notification.permission === "default") {
      Notification.requestPermission();
    }
  };

  const statusBanner = useMemo(() => {
    if (!path || path.length === 0) return null;

    const lastIdx = path.length - 1;
    const currentStep = path[currentIndex] || path[0];
    const nextStep = path[currentIndex + 1];
    const remainingTotalStops = lastIdx - currentIndex;

    const currentName = getStepName(currentStep);
    const nextName = getStepName(nextStep);
    const finalName = getStepName(path[lastIdx]);

    const nextInterchangeIdx = path.findIndex(
      (s, idx) => idx > currentIndex && s.interchangeHere
    );

    if (currentIndex >= lastIdx) {
      return {
        type: "arrived",
        badge: "DESTINATION REACHED",
        title: `Exit now at ${currentName}`,
        subtitle: "You have arrived at your destination station.",
        bg: "bg-emerald-600 text-white",
      };
    }

    if (remainingTotalStops === 1) {
      return {
        type: "exit_next",
        badge: "EXIT AT NEXT STOP",
        title: `Exit at next stop: ${nextName}`,
        subtitle: "Move toward the doors to exit at the upcoming station.",
        bg: "bg-rose-600 text-white",
      };
    }

    if (currentStep.interchangeHere && nextStep) {
      return {
        type: "interchange_now",
        badge: "INTERCHANGE HERE",
        title: `Exit & switch to ${nextStep.line}`,
        subtitle: `Board ${nextStep.line} (${remainingTotalStops} stops left to ${finalName})`,
        bg: "bg-amber-500 text-slate-950",
      };
    }

    if (nextInterchangeIdx === currentIndex + 1 && nextStep) {
      const targetLine = path[nextInterchangeIdx + 1]?.line || "connecting line";
      return {
        type: "interchange_next",
        badge: "EXIT AT NEXT STOP (INTERCHANGE)",
        title: `Exit at next stop: ${nextName}`,
        subtitle: `Change to ${targetLine} • ${remainingTotalStops} total stops left`,
        bg: "bg-amber-600 text-white",
      };
    }

    const stopsToNextAction =
      nextInterchangeIdx !== -1
        ? nextInterchangeIdx - currentIndex
        : remainingTotalStops;

    const actionTargetText =
      nextInterchangeIdx !== -1
        ? `to interchange at ${getStepName(path[nextInterchangeIdx])}`
        : `to exit at ${finalName}`;

    return {
      type: "en_route",
      badge: `${stopsToNextAction} MORE ${stopsToNextAction === 1 ? "STOP" : "STOPS"}`,
      title: `${stopsToNextAction} more ${stopsToNextAction === 1 ? "stop" : "stops"} ${actionTargetText}`,
      subtitle: `Current: ${currentName}${nextName ? ` • Next: ${nextName}` : ""}`,
      bg: "bg-teal-900 text-white border border-teal-500/40",
    };
  }, [path, currentIndex]);

  useEffect(() => {
    if (!isTracking || !statusBanner) return;
    if (lastNotifiedIndexRef.current === currentIndex) return;
    lastNotifiedIndexRef.current = currentIndex;

    if (
      statusBanner.type === "exit_next" ||
      statusBanner.type === "interchange_next" ||
      statusBanner.type === "arrived"
    ) {
      triggerAlertCue(statusBanner.badge, statusBanner.title);
    }
  }, [isTracking, currentIndex, statusBanner]);

  if (!path || path.length < 2) return null;

  return (
    <div className="rounded-2xl border border-slate-200 bg-slate-50 p-3.5 space-y-3">
      {!isTracking ? (
        <button
          type="button"
          onClick={startTracking}
          className="w-full py-2.5 px-4 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-extrabold flex items-center justify-center gap-2 border-0 cursor-pointer shadow-xs transition"
        >
          <span className="w-2 h-2 rounded-full bg-white animate-ping" />
          Stepped into Metro
        </button>
      ) : (
        <div className="space-y-2.5">
          {statusBanner && (
            <div className={`rounded-xl p-3.5 shadow-sm transition-all ${statusBanner.bg}`}>
              <div className="flex items-center justify-between gap-2 mb-1">
                <span className="text-[10px] font-black uppercase tracking-wider px-2 py-0.5 rounded-full bg-black/20">
                  {statusBanner.badge}
                </span>
                <span className="text-[10px] font-semibold opacity-90">
                  Stop {currentIndex + 1} of {path.length}
                </span>
              </div>
              <p className="text-sm font-extrabold leading-snug">
                {statusBanner.title}
              </p>
              <p className="text-[11px] opacity-90 mt-0.5">
                {statusBanner.subtitle}
              </p>
            </div>
          )}

          <div className="flex items-center justify-between gap-2">
            <button
              type="button"
              disabled={currentIndex >= path.length - 1}
              onClick={() =>
                setCurrentIndex((prev) => Math.min(path.length - 1, prev + 1))
              }
              className="flex-1 py-2 px-3 rounded-lg bg-slate-900 hover:bg-slate-800 disabled:opacity-40 text-white text-[11px] font-bold border-0 cursor-pointer transition"
            >
              Reached Next Stop &rarr;
            </button>

            <button
              type="button"
              onClick={stopTracking}
              className="py-2 px-3 rounded-lg bg-slate-200 hover:bg-slate-300 text-slate-700 text-[11px] font-bold border-0 cursor-pointer transition"
            >
              End Trip
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default LiveJourneyTracker;