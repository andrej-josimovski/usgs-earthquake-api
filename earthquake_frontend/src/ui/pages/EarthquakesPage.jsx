import useEarthquakes from "../../hooks/useEarthquakes";
import EarthquakeTable from "../components/EarthquakeTable";
import EarthquakeFilters from "../components/EarthquakeFilters";
import EarthquakeMap from "../components/EarthquakeMap.jsx";

export default function EarthquakesPage() {
    const { earthquakes, loading, onFilter, onFetchAndStore, onDelete } = useEarthquakes();

    return (
        <div className="page">
            <div className="toolbar">
                <div>
                    <h1>Earthquakes</h1>
                    <p className="subtitle">Live seismic data from USGS</p>
                </div>
                <button className="fetch-btn" onClick={onFetchAndStore} disabled={loading}>
                    {loading ? "Fetching…" : "Fetch latest"}
                </button>
            </div>
            <EarthquakeFilters onFilter={onFilter} />
            <EarthquakeTable earthquakes={earthquakes} onDelete={onDelete} />
            <EarthquakeMap earthquakes={earthquakes} />
        </div>
    );
}