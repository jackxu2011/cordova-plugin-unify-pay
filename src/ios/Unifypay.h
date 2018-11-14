#import <Cordova/CDVPlugin.h>
#import "WXApi.h"
#import "WXApiObject.h"

@interface Unifypay:CDVPlugin <WXApiDelegate>

@property (nonatomic, strong) NSString *currentCallbackId;
@property (nonatomic, strong) NSString *wechatAppId;
@property (nonatomic, strong) NSString *alipayAppId;

- (void)pay:(CDVInvokedUrlCommand *)command;

@end
