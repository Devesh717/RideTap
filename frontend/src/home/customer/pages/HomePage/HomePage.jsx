import React, { useEffect } from "react";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import HeroSection from "../../components/HomeHero/HeroSection";
import { fetchAllStations } from "../../../../station/state/Station/Action";

const HomePage = () => {
  const dispatch = useDispatch();
  const { stations } = useSelector((state) => state.station);

  useEffect(() => {
    if (stations.length === 0) {
      dispatch(fetchAllStations());
    }
  }, [dispatch, stations.length]);

  const features = [
    {
      title: "1. Top Up Metro Wallet",
      desc: "Add balance instantly to your RideTap Metro Card so you never run out at the gate.",
      link: "/wallet",
      cta: "Open Wallet",
    },
    {
      title: "2. Book & Get QR Pass",
      desc: "Choose your origin and destination stations to generate an instant scannable QR ticket.",
      link: "/book-ride",
      cta: "Book Now",
    },
    {
      title: "3. Ask AI Transit Concierge",
      desc: "Check fares, view active QR codes, or book tickets through natural conversation.",
      link: "/ai-assistant",
      cta: "Launch AI Chat",
    },
  ];

  return (
    <div>
      <HeroSection />

      {/* Metro Line Preview Strip */}
      {stations.length > 0 && (
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex items-center justify-between mb-5">
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-teal-600">
                Active Corridor
              </span>
              <h2 className="text-2xl font-extrabold text-slate-900">
                RideTap Metro Line ({stations.length} Stations)
              </h2>
            </div>
            <Link
              to="/stations"
              className="text-sm font-semibold text-teal-600 hover:text-teal-700 no-underline"
            >
              View Full Directory &rarr;
            </Link>
          </div>

          <div className="bg-white border border-slate-200 rounded-2xl p-6 overflow-x-auto">
            <div className="flex items-center gap-3 min-w-max">
              {stations.map((st, index) => (
                <React.Fragment key={st.id}>
                  <div className="flex flex-col items-center text-center px-3 py-2 rounded-xl bg-slate-50 border border-slate-200 min-w-[130px]">
                    <span className="text-[11px] font-bold text-teal-600">
                      Stop #{st.sequenceNumber}
                    </span>
                    <span className="text-sm font-bold text-slate-900 mt-0.5">{st.name}</span>
                  </div>
                  {index < stations.length - 1 && (
                    <div className="h-0.5 w-8 bg-teal-400 shrink-0" />
                  )}
                </React.Fragment>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Quick Feature Cards */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <h2 className="text-2xl font-extrabold text-slate-900 mb-6">How RideTap Works</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {features.map((item) => (
            <div
              key={item.title}
              className="bg-white border border-slate-200 rounded-2xl p-6 shadow-xs flex flex-col justify-between"
            >
              <div>
                <h3 className="text-lg font-bold text-slate-900">{item.title}</h3>
                <p className="text-sm text-slate-600 mt-2 leading-relaxed">{item.desc}</p>
              </div>
              <Link
                to={item.link}
                className="mt-5 inline-block text-xs font-bold text-teal-600 hover:text-teal-700 uppercase tracking-wider no-underline"
              >
                {item.cta} &rarr;
              </Link>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default HomePage;
