import {
  createRazorpayOrderApi,
  verifyRazorpayPaymentApi,
} from "../api/PaymentApi";
import { extractApiError } from "../../config/apiConfig";

const loadRazorpayScript = () =>
  new Promise((resolve) => {
    if (window.Razorpay) {
      resolve(true);
      return;
    }
    const script = document.createElement("script");
    script.src = "https://checkout.razorpay.com/v1/checkout.js";
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });

export const initiateRazorpayPayment = async ({
  purpose,
  amount = null,
  description,
  originStation = null,
  destinationStation = null,
  onSuccess,
  onError,
  onDismiss,
}) => {
  try {
    const sdkLoaded = await loadRazorpayScript();
    if (!sdkLoaded) {
      throw new Error("Razorpay SDK failed to load. Check your internet connection.");
    }

    const orderData = await createRazorpayOrderApi({
      purpose,
      amount,
      originStation,
      destinationStation,
    });

    const amountInPaise = Math.round(Number(orderData.amount) * 100);

    const options = {
      key: orderData.keyId,
      amount: amountInPaise,
      currency: orderData.currency || "INR",
      name: "RideTap Delhi Metro",
      description: description || "RideTap Metro Payment",
      order_id: orderData.razorpayOrderId,
      prefill: {
        name: orderData.customerName || "",
        email: orderData.customerEmail || "",
        contact: orderData.customerPhone || "",
      },
      notes: {
        referenceNumber: orderData.referenceNumber || "",
        metroCardNumber: orderData.metroCardNumber || "",
        purpose,
      },
      theme: {
        color: "#0D9488",
      },
      handler: async function (response) {
        try {
          const verifiedPayment = await verifyRazorpayPaymentApi({
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
          });
          if (onSuccess) onSuccess(verifiedPayment);
        } catch (verifyErr) {
          if (onError) {
            onError(extractApiError(verifyErr, "Payment signature verification failed."));
          }
        }
      },
      modal: {
        ondismiss: function () {
          if (onDismiss) onDismiss();
        },
      },
    };

    const rzp = new window.Razorpay(options);
    rzp.on("payment.failed", function (resp) {
      if (onError) {
        onError(resp?.error?.description || "Razorpay payment failed.");
      }
    });
    rzp.open();
  } catch (err) {
    if (onError) {
      onError(extractApiError(err, "Unable to initiate Razorpay payment."));
    }
  }
};
