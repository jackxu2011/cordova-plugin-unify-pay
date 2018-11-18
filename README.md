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
  '{"qrCode":"https://qr.alipay.com/bax0254776flwtwg8l6w203d"}', //支付字符串，从银联下单结果中的appPayRequest
  () => {
    console.log('成功');
  }, 
  e => {
    console.error(e);
  });

```

## 代码捐献

非常期待您的代码捐献。