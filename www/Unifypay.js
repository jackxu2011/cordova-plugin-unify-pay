// License: Apache License 2.0

'use strict';

module.exports = {
  channel: {
    WEIXIN: '01',
    ALIPAY: '02',
    UMSPAY: '03'
  },
  //支付宝
  pay: function (channel, payData, onSuccess, onError) {
    cordova.exec(onSuccess, onError, "Unifypay", "pay", [channel, payData]);
  }
};