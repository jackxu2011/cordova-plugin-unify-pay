#import <Cordova/CDVPlugin.h>

@interface Unifypay:CDVPlugin

@property (nonatomic, strong) NSString *currentCallbackId;
@property (nonatomic, strong) NSString *alipayAppId;
@property (nonatomic, strong) NSString *uppayAppId;

- (void)pay:(CDVInvokedUrlCommand *)command;

@end
