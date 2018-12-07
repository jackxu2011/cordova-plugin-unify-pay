#import "Unifypay.h"
#import "UMSPPPayUnifyPayPlugin.h"

@implementation Unifypay

#pragma mark "API"
- (void)pluginInitialize
{
    self.alipayAppId = [[self.commandDelegate settings] objectForKey:@"alipayappid"];
    NSLog(@"cordova-plugin-unify-pay has been initialized.");
}

- (void)pay:(CDVInvokedUrlCommand *)command {
    self.currentCallbackId = command.callbackId;
    // check arguments
    NSString *channel = [command.arguments objectAtIndex:0];
    NSString *payData = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:[command.arguments objectAtIndex:1] options:NSJSONWritingPrettyPrinted error:nil] encoding:NSUTF8StringEncoding];
    
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

- (void) sendPaymentRequest: (NSString *) channel payData: (NSString *) payData{
    
    [UMSPPPayUnifyPayPlugin payWithPayChannel:channel payData:payData callbackBlock:^(NSString *resultCode, NSString *resultInfo) {
        
        CDVPluginResult* pluginResult;
        
        if ([resultCode  isEqual: @"0000"]) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: resultInfo];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
        }
    }];
}

@end
