package com.cfs.RideTap.services.implementations;

import com.cfs.RideTap.exceptions.InsufficientBalanceException;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.services.interfaces.BalanceServices;
import org.springframework.stereotype.Service;



@Service
public class BalanceServicesImpl implements BalanceServices {
  @Override
  public void deductFare(UserEntity user, double fare) throws InsufficientBalanceException {
    double userBalance = user.getBalance();

    if (userBalance < fare) {
      throw new InsufficientBalanceException();
    }

    user.deduct(fare);
  }

  @Override
  public void addMoney(UserEntity user, double money) {
    user.recharge(money);
  }
}
