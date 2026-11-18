package com.cfs.RideTap.balance.service.interfaces;

import com.cfs.RideTap.balance.exception.InsufficientBalanceException;
import com.cfs.RideTap.user.model.UserEntity;

public interface BalanceServices {
  void deductFare(UserEntity user, double fare) throws InsufficientBalanceException;

  void addMoney(UserEntity user, double money);
}
