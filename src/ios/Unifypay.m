#import "Unifypay.h"
#import "UMSPPPayUnifyPayPlugin.h"
#import "UPPaymentControl.h"

@implementation Unifypay

#pragma mark "API"
- (void)pluginInitialize
{
    self.alipayAppId = [[self.commandDelegate settings] objectForKey:@"alipayappid"];
    self.uppayAppId = [[self.commandDelegate settings] objectForKey:@"uppayappId"];
    NSLog(@"cordova-plugin-unify-pay has been initialized.");
}

- (void)pay:(CDVInvokedUrlCommand *)command {
    self.currentCallbackId = command.callbackId;
    // check arguments
    NSString *channel = [command.arguments objectAtIndex:0];

    NSDictionary *payData = [command.arguments objectAtIndex:1];
    
    if (!channel || !payData)
    {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"参数格式错误"] callbackId:self.currentCallbackId];
        return ;
    }

    if ([channel isEqual: CHANNEL_WEIXIN]) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"本插件不支付微信，请使用微信插件支付"] callbackId:self.currentCallbackId];
        return ;
    }
    
    [self sendPaymentRequest: channel payData: payData];

}

- (void)isUppayAppInstalled:(CDVInvokedUrlCommand *)command {
    self.currentCallbackId = command.callbackId;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:[[UPPaymentControl defaultControl] isPaymentAppInstalled]]
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
}

- (void) sendPaymentRequest: (NSString *) channel payData: (NSDictionary *) payData{
    CDVPluginResult* pluginResult;

    if(channel isEqual:@"00") {
        if([[UPPaymentControl defaultControl] startPay:payData[@"tn"] fromScheme:self.uppayAppId mode:@"00" viewController:self.viewController]) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"调起云闪付失败"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
        }
    } else {
        [UMSPPPayUnifyPayPlugin payWithPayChannel:channel payData:[[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:payData options:NSJSONWritingPrettyPrinted error:nil] encoding:NSUTF8StringEncoding] 
                callbackBlock:^(NSString *resultCode, NSString *resultInfo) {
            if ([resultCode  isEqual: @"0000"]) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: resultInfo];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
            }
        }];
    }
    

    
}

@end
