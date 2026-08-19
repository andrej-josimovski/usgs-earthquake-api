import { useCallback, useEffect, useState } from "react";
import earthquakeRepository from "../repository/earthquakeRepository.js";

const initialState = {
    earthquakes: [],
    loading: true,
};

const useEarthquakes = () => {
    const [state, setState] = useState(initialState);

    const fetchEarthquakes = useCallback(() => {
        setState(initialState);
        earthquakeRepository
            .findAll()
            .then((response) => {
                setState({
                    "earthquakes": response.data,
                    "loading": false,
                });
            })
            .catch((error) => console.log(error));
    }, []);

    const onFilter = useCallback((minMagnitude, after) => {
        setState(initialState);
        earthquakeRepository
            .findFiltered(minMagnitude, after)
            .then((response) => {
                setState({
                    "earthquakes": response.data,
                    "loading": false,
                });
            })
            .catch((error) => console.log(error));
    }, []);

    const onFetchAndStore = useCallback(() => {
        earthquakeRepository
            .fetchAndStore()
            .then(() => {
                console.log("Successfully fetched and stored earthquakes from USGS.");
                fetchEarthquakes();
            })
            .catch((error) => console.log(error));
    }, [fetchEarthquakes]);

    const onDelete = useCallback((id) => {
        earthquakeRepository
            .deleteById(id)
            .then(() => {
                console.log(`Successfully deleted the earthquake with ID ${id}.`);
                fetchEarthquakes();
            })
            .catch((error) => console.log(error));
    }, [fetchEarthquakes]);

    useEffect(() => {
        fetchEarthquakes();
    }, [fetchEarthquakes]);

    return {...state, onFilter: onFilter, onFetchAndStore: onFetchAndStore, onDelete: onDelete};
};

export default useEarthquakes;