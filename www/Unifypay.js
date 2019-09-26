// License: Apache License 2.0

'use strict';

module.exports = {
  channel: {
    UPPAY: '00',
    WEIXIN: '01',
    ALIPAY: '02',
    UMSPAY: '03'
  },
  //支付宝
  pay: function (channel, payData, onSuccess, onError) {
    cordova.exec(onSuccess, onError, "Unifypay", "pay", [channel, payData]);
  },
  isUppayAppInstalled: function(onSuccess, onError) {
    cordova.exec(onSuccess, onError, "Unifypay", "isUppayAppInstalled", [channel, payData]);
  }
};