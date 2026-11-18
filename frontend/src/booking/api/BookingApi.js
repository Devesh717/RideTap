import { api, API_BASE_URL } from "../../config/apiConfig";

/**
 * POST /book
 * Request: BookingRequest { originStation, destinationStation }
 * Response: BookingDTO { id, originStation, destinationStation, dateTime, bookingStatus, user, fare, qrPath } (201 CREATED)
 */
export const createBookingApi = async (bookingRequest) => {
  const response = await api.post("/book", bookingRequest);
  return response.data;
};

/**
 * PUT /book/cancel/{id}
 * Response: BookingDTO (200 OK)
 */
export const cancelBookingApi = async (id) => {
  const response = await api.put(`/book/cancel/${id}`);
  return response.data;
};

/**
 * GET /booking-history?bookingStatus=1|0
 * bookingStatus: 1 -> CONFIRMED, 0 -> CANCELLED, undefined -> all user bookings
 * Response: List<BookingDTO>
 */
export const getUserBookingsApi = async (bookingStatus = null) => {
  const params = {};
  if (bookingStatus !== null && bookingStatus !== undefined && bookingStatus !== "") {
    params.bookingStatus = bookingStatus;
  }
  const response = await api.get("/booking-history", { params });
  return response.data;
};

/**
 * GET /booking-history/{id}
 * Response: BookingDTO
 */
export const getBookingByIdApi = async (id) => {
  const response = await api.get(`/booking-history/${id}`);
  return response.data;
};

/**
 * GET /booking-history/all?metroCardNumber=...&bookingStatus=1|0 (Admin)
 * Response: List<BookingDTO>
 */
export const getAllBookingsAdminApi = async ({ metroCardNumber, bookingStatus } = {}) => {
  const params = {};
  if (metroCardNumber) params.metroCardNumber = metroCardNumber;
  if (bookingStatus !== null && bookingStatus !== undefined && bookingStatus !== "") {
    params.bookingStatus = bookingStatus;
  }
  const response = await api.get("/booking-history/all", { params });
  return response.data;
};

/**
 * Helper to build resolvable QR image URL from BookingDTO.qrPath (e.g., "qrCodes/booking_12.png")
 */
export const resolveQrImageUrl = (qrPath) => {
  if (!qrPath) return null;
  if (qrPath.startsWith("http://") || qrPath.startsWith("https://") || qrPath.startsWith("data:")) {
    return qrPath;
  }
  const cleanPath = qrPath.startsWith("/") ? qrPath : `/${qrPath}`;
  return `${API_BASE_URL}${cleanPath}`;
};
