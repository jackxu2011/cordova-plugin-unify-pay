# 银联全民付 cordova 插件

> 支持ios, android

## 通道
- 支付定支付(Alipay)
- 微信支付(Wechatpay) (不支付，从代码上看，微信支付可以直接用微信支付插件，不需要在本插件中实现，微信还有分享等功能也依赖于微信sdk不想重复开发。等与银联联调后可证实是否可行)
- 银联全渠道(Uac)

## 安装

首先安装 cordova-plugin-wechat

```
cordova plugin add cordova-plugin-wechat  --variable wechatappid=YOUR_WECHAT_APPID
```

再安装本插件
uppayappid的值可以把wechatappid的前两位改为up

```
//本方式暂时不支付，因为没有发布
cordova plugin add cordova-plugin-unify-pay --variable UPPAYAPPID=[your uppay appId]

cordova plugin add https://github.com/jackxu2011/cordova-plugin-unify-pay.git --variable UPPAYAPPID=[your uppay appId]
```

ionic 3 在import之后
```
declare let Unifypay;
```
## 提供支付通道常量

```js
  channel: {
    UPPAY: '00',   //云闪付
    WEIXIN: '01',  //微信--微信直接通过微信直接插件发起支付
    ALIPAY: '02',  //支付宝
    UMSPAY: '03'  //银商钱包
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

>是否安装云闪付
```js
Unifypay.isUppayAppInstalled(
  (installed) => {
    console.log(installed);
  });

```

## 代码捐献

非常期待您的代码捐献。