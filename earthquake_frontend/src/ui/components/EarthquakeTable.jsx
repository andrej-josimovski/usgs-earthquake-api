import EarthquakeRow from "./EarthquakeRow";

export default function EarthquakeTable({ earthquakes, onDelete }) {
    if (earthquakes.length === 0) {
        return <p className="empty-state">No earthquakes to show.</p>;
    }

    return (
        <div className="eq-list">
            {earthquakes.map((eq) => (
                <EarthquakeRow key={eq.id} earthquake={eq} onDelete={onDelete} />
            ))}
        </div>
    );
}