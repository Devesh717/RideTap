package com.cfs.RideTap.services.interfaces;


import com.cfs.RideTap.exceptions.InsufficientBalanceException;
import com.cfs.RideTap.models.UserEntity;

public interface BalanceServices {
  public void deductFare(UserEntity user, double fare) throws InsufficientBalanceException;

  public void addMoney(UserEntity user, double money);
}
