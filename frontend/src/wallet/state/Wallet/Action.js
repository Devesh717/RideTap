import {
  FETCH_BALANCE_REQUEST,
  FETCH_BALANCE_SUCCESS,
  FETCH_BALANCE_FAILURE,
  ADD_BALANCE_REQUEST,
  ADD_BALANCE_SUCCESS,
  ADD_BALANCE_FAILURE,
  CLEAR_WALLET_MESSAGES,
} from "./ActionType";
import { getBalanceApi } from "../../api/BalanceApi";
import { initiateRazorpayPayment } from "../../../payment/utils/openRazorpayCheckout";
import { extractApiError } from "../../../config/apiConfig";

export const fetchBalance = () => async (dispatch) => {
  dispatch({ type: FETCH_BALANCE_REQUEST });
  try {
    const balance = await getBalanceApi();
    dispatch({
      type: FETCH_BALANCE_SUCCESS,
      payload: Number(balance ?? 0),
    });
  } catch (error) {
    dispatch({
      type: FETCH_BALANCE_FAILURE,
      payload: extractApiError(error, "Unable to retrieve Metro Card balance."),
    });
  }
};

export const addBalance = (money, onSuccess) => async (dispatch) => {
  const numericAmount = Number(money);
  if (!numericAmount || numericAmount < 10) {
    dispatch({
      type: ADD_BALANCE_FAILURE,
      payload: "Minimum Metro Card recharge amount is ₹10.",
    });
    return;
  }

  dispatch({ type: ADD_BALANCE_REQUEST });

  await initiateRazorpayPayment({
    purpose: "METROCARD_RECHARGE",
    amount: numericAmount,
    description: `Metro Card Top-Up (₹${numericAmount.toFixed(2)})`,
    onSuccess: (paymentData) => {
      const creditedAmount = Number(paymentData?.amount ?? numericAmount);
      const ref = paymentData?.referenceNumber;
      const message = ref
        ? `₹${creditedAmount.toFixed(2)} credited via Razorpay (Ref: ${ref})`
        : `₹${creditedAmount.toFixed(2)} credited to your Metro Card`;

      dispatch({
        type: ADD_BALANCE_SUCCESS,
        payload: {
          message,
          addedAmount: creditedAmount,
          updatedBalance: paymentData?.updatedBalance,
        },
      });

      if (paymentData?.updatedBalance !== undefined && paymentData?.updatedBalance !== null) {
        dispatch({
          type: FETCH_BALANCE_SUCCESS,
          payload: Number(paymentData.updatedBalance),
        });
      } else {
        dispatch(fetchBalance());
      }

      if (onSuccess) onSuccess(paymentData);
    },
    onError: (errMessage) => {
      dispatch({
        type: ADD_BALANCE_FAILURE,
        payload:
          typeof errMessage === "string"
            ? errMessage
            : "Razorpay recharge failed. Please try again.",
      });
    },
    onDismiss: () => {
      dispatch({
        type: ADD_BALANCE_FAILURE,
        payload: null,
      });
    },
  });
};

export const clearWalletMessages = () => ({
  type: CLEAR_WALLET_MESSAGES,
});
