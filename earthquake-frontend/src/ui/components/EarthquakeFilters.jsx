import { useState } from "react";

export default function EarthquakeFilters({ onFilter }) {
    const [minMagnitude, setMinMagnitude] = useState("");
    const [after, setAfter] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        onFilter(minMagnitude || undefined, after ? new Date(after).toISOString() : undefined);
    };

    const handleReset = () => {
        setMinMagnitude("");
        setAfter("");
        onFilter(undefined, undefined);
    };

    return (
        <form className="filters" onSubmit={handleSubmit}>
            <label>
                Min magnitude
                <input
                    type="number"
                    step="0.1"
                    value={minMagnitude}
                    onChange={(e) => setMinMagnitude(e.target.value)}
                />
            </label>
            <label>
                After
                <input
                    type="datetime-local"
                    value={after}
                    onChange={(e) => setAfter(e.target.value)}
                />
            </label>
            <button type="submit">Apply</button>
            <button type="button" onClick={handleReset}>Reset</button>
        </form>
    );
}