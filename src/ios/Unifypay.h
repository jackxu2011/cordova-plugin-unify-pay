#import <Cordova/CDVPlugin.h>

@interface Unifypay:CDVPlugin

@property (nonatomic, strong) NSString *currentCallbackId;
@property (nonatomic, strong) NSString *alipayAppId;

- (void)pay:(CDVInvokedUrlCommand *)command;

@end
