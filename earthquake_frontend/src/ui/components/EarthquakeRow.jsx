const magColor = (mag) => {
    if (mag >= 6) return "#e8543a";
    if (mag >= 4) return "#d97757";
    if (mag >= 2) return "#e0a458";
    return "#8ba888";
};

export default function EarthquakeRow({ earthquake, onDelete }) {
    const { id, magnitude, magType, place, time, depth } = earthquake;

    return (
        <div className="eq-card">
            <div className="eq-mag" style={{ background: magColor(magnitude) }}>
                {magnitude?.toFixed(1)}
            </div>
            <div className="eq-info">
                <div className="eq-place">{place}</div>
                <div className="eq-meta">
                    {new Date(time).toLocaleString()} · {magType} · {depth?.toFixed(1)} km deep
                </div>
            </div>
            <button className="eq-delete" onClick={() => onDelete(id)}>
                Remove
            </button>
        </div>
    );
}