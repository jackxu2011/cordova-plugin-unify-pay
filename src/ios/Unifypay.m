#import "Unifypay.h"
#import "UMSPPPayUnifyPayPlugin.h"

@implementation Unifypay

#pragma mark "API"
- (void)pluginInitialize
{
    self.wechatAppId = [[self.commandDelegate settings] objectForKey:@"wechatappid"];
    self.alipayAppId = [[self.commandDelegate settings] objectForKey:@"alipayappid"];
    [WXApi registerApp: self.wechatAppId];
    NSLog(@"cordova-plugin-unify-pay has been initialized. Weixin SDK Version: %@. APP_ID: %@.", [WXApi getApiVersion], self.wechatAppId);
}

- (void)pay:(CDVInvokedUrlCommand *)command {
    self.currentCallbackId = command.callbackId;
    // check arguments
    NSString *channel = [command.arguments objectAtIndex:0];
    NSString *payData = [command.arguments objectAtIndex:1];
    if (!channel)
    {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"参数格式错误"] callbackId:self.currentCallbackId];
        return ;
    }
    
    [self sendPaymentRequest: channel payData: payData];
    
}

- (void) sendPaymentRequest: (NSString *) channel payData: (NSString *) payData{
    
    [UMSPPPayUnifyPayPlugin payWithPayChannel:channel payData:payData callbackBlock:^(NSString *resultCode, NSString *resultInfo) {
        
        CDVPluginResult* pluginResult;
        
        NSDictionary *resultDic = @{@"resultCode": resultCode,@"resultInfo": resultInfo};
        
        if ([resultCode  isEqual: @"0000"]) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDic];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:resultDic];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
        }
    }];
}

#pragma mark "CDVPlugin Overrides"

- (void)handleOpenURL:(NSNotification *)notification
{
    NSURL* url = [notification object];
    
    if ([url isKindOfClass:[NSURL class]] && [url.scheme isEqualToString:self.wechatAppId])
    {
        [UMSPPPayUnifyPayPlugin handleOpenURL:url];
    }
}

@end
