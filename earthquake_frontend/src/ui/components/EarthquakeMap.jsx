import { MapContainer, TileLayer, CircleMarker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';

function magnitudeColor() {
    return '#e74c3c';
}

export default function EarthquakeMap({ earthquakes }) {
    const withCoordinates = earthquakes.filter(
        (eq) => eq.latitude != null && eq.longitude != null
    );

    return (
        <MapContainer
            center={[20, 0]}
            zoom={2}
            style={{ height: '500px', width: '100%', borderRadius: '8px' }}
        >
            <TileLayer
                attribution='&copy; OpenStreetMap contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            {withCoordinates.map((eq) => (
                <CircleMarker
                    key={eq.id}
                    center={[eq.latitude, eq.longitude]}
                    radius={Math.max((eq.magnitude ?? 1) * 3, 4)}
                    pathOptions={{
                        color: magnitudeColor(),
                        fillColor: magnitudeColor(),
                        fillOpacity: 0.6,
                    }}
                >
                    <Popup>
                        <strong>{eq.title}</strong>
                        <br />
                        Magnitude: {eq.magnitude}
                        <br />
                        Place: {eq.place}
                        <br />
                        Time: {new Date(eq.time).toLocaleString()}
                    </Popup>
                </CircleMarker>
            ))}
        </MapContainer>
    );
}