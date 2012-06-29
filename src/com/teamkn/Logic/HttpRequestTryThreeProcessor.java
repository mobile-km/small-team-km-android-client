package com.teamkn.Logic;

import com.teamkn.Logic.HttpApi.NetworkUnusableException;
import com.teamkn.Logic.HttpApi.ServerErrorException;
import com.teamkn.base.http.TeamknHttpRequest.ResponseNot200Exception;

import java.net.SocketException;

public abstract class HttpRequestTryThreeProcessor<E> {
  public abstract E callback() throws Exception,NetworkUnusableException,ServerErrorException;
  
  public E execute() throws Exception{
    int socket_exception_count = 0;
    int request_unsuccess_exception_count = 0;
    E res = null;
    while (true) {
      try {
        res = callback();
      } catch (SocketException e) {
        socket_exception_count += 1;
        request_unsuccess_exception_count = 0;
        if(socket_exception_count >= 3){
          throw new NetworkUnusableException();
        }
        continue;
      } catch (ResponseNot200Exception e) {
        request_unsuccess_exception_count += 1;
        socket_exception_count = 0;
        if(request_unsuccess_exception_count >= 3){
          throw new ServerErrorException();
        }
        continue;
      }
      break;
    }
    return res;
  }
}
