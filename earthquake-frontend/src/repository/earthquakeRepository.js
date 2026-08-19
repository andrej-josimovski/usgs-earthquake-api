import axiosInstance from "../axios/axios.js";

const earthquakeRepository = {

    findAll: async () => {
        return await axiosInstance.get("/earthquakes");
    },
    findFiltered: async (minMagnitude, after) => {
        return await axiosInstance.get("/earthquakes/filter", {
            params: {minMagnitude, after},
        });
    },
    fetchAndStore: async () => {
        return await axiosInstance.post("/earthquakes/fetch");
    },
    deleteById: async (id) => {
        return await axiosInstance.delete(`/earthquakes/${id}`);
    },
};

export default earthquakeRepository;