import { api } from "../../config/apiConfig";

export const createRazorpayOrderApi = async ({
  purpose,
  amount = null,
  originStation = null,
  destinationStation = null,
}) => {
  const response = await api.post("/api/payments/orders", {
    purpose,
    amount,
    originStation,
    destinationStation,
  });
  return response.data;
};

export const verifyRazorpayPaymentApi = async ({
  razorpayOrderId,
  razorpayPaymentId,
  razorpaySignature,
}) => {
  const response = await api.post("/api/payments/verify", {
    razorpayOrderId,
    razorpayPaymentId,
    razorpaySignature,
  });
  return response.data;
};

export const getMyPaymentsApi = async (page = 0, size = 10) => {
  const response = await api.get(`/api/payments?page=${page}&size=${size}`);
  return response.data;
};
