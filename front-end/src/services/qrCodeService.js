import { fetchWithAuth } from "./apiService";
const API_URL_USER = "http://localhost:8080/api/user";

export const getQrCode = async (orderId) => {
  const res = await fetchWithAuth(
    `${API_URL_USER}/orders/${orderId}/qr-url`
  );

  if (!res.ok) throw new Error("Không lấy được QR");

  return await res.text(); // vì backend trả string
};


export const getQrCodeForBooking = async (bookingId) => {
  const res = await fetchWithAuth(
    `${API_URL_USER}/bookings/${bookingId}/qr-url`
  );

  if (!res.ok) throw new Error("Không lấy được QR");

  return await res.text(); // vì backend trả string
};