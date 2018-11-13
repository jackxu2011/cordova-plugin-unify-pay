# 银联全民付 cordova 插件

> 支持ios, android

## 通道
- 支付定支付(Alipay)
- 微信支付(Wechatpay)
- 银联全渠道(Uac) (暂时不支持)

## 安装

```
cordova plugin add cordova-plugin-unify-pay --variable ALIPAYAPPID=[your alipay appId] --variable WECHATAPPID=[your wechat appId]
cordova plugin add https://github.com/jackxu2011/cordova-plugin-unify-pay.git --variable ALIPAYAPPID=[your alipay appId] --variable WECHATAPPID=[your wechat appId]
```

ionic 3 在import之后
```
declare let Unifypay;
```
## 提供支付通道常量

```js
  channel: {
    WEIXIN: '01',
    ALIPAY: '02',
    UMSPAY: '03'
  }
```

## 使用方法
>打开支付页面
```js
Unifypay.pay(
  Unifypay.channel.ALIPAY, // 通道
  '{"msgType":"trade.precreate","connectSys":"ALIPAY","msgSrc":"WWW.TEST.COM","merName":"仲晶晶二维码测试","mid":"898340149000005","msgId":"3194","appPayRequest":{"qrCode":"https://qr.alipay.com/bax0254776flwtwg8l6w203d"},"settleRefId":"00255100548N","tid":"88880001","srcReserve":"test001","totalAmount":1,"qrCode":"https://qr.alipay.com/bax0254776flwtwg8l6w203d","targetMid":"2015061000120322","responseTimestamp":"2018-06-25 21:18:02","errCode":"SUCCESS","targetStatus":"10000","seqId":"00255100548N","merOrderId":"3194676990","status":"NEW_ORDER","targetSys":"Alipay 2.0","sign":"E880CB77904BEC754AA508DD16AD88B5"}', //支付字符串，从银联下单结果中获取
  () => {
    console.log('成功');
  }, 
  e => {
    console.error(e);
  });

```

## 代码捐献

非常期待您的代码捐献。