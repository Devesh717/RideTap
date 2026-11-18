package com.cfs.RideTap.balance.service.implementations;

import com.cfs.RideTap.balance.exception.InsufficientBalanceException;
import com.cfs.RideTap.balance.service.interfaces.BalanceServices;
import com.cfs.RideTap.user.model.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    if (money <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recharge amount must be greater than zero");
    }
    user.recharge(money);
  }
}