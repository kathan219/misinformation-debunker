import axios from 'axios';

const API_BASE_URL = "http://localhost:8080/api"; // Update if needed

export async function submitFactCheck(claim: string) {
    try {
        const response = await axios.get(API_BASE_URL, {
            params: { claim }
        });

        return response.data;
    } catch (error) {
        console.error("Error submitting fact check:", error);
        throw error;
    }
}

// export const getFactCheckById = async (id: string): Promise<FactCheckResult> => {
//   const { data } = await api.get(`/factcheck/${id}`);
//   return data;
// };

// export const getTrends = async (): Promise<TrendData[]> => {
//   const { data } = await api.get('/trends');
//   return data;
// };