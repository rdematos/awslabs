//
//  IoTManager.m
//  mqtt road sign
//
//  Created by Justin Dickow on 12/15/15.
//  Copyright © 2015 Livio. All rights reserved.
//

#import "IoTManager.h"
#import <AWSIoT/AWSIoT.h>
#import <AWSCore/AWSCore.h>

typedef void (^AWSCallback)(AWSIoTMQTTStatus status);

@interface IoTManager ()
@property (strong, nonatomic) AWSIoTDataManager *iotDataManager;
@property (strong, nonatomic) AWSIoTData *iotData;
@property (strong, nonatomic) AWSIoTManager *iotManager;
@property (strong, nonatomic) AWSIoT *iot;
@property (nonatomic, getter=isConnected, readwrite) BOOL connected;
@end

@implementation IoTManager
+ (instancetype)manager {
    static IoTManager *manager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[self alloc] init];
    });
    return manager;
}

- (instancetype)init {
    self = [super init];
    if (!self) {
        return nil;
    }
    
    _connected = NO;
    
    AWSCognitoCredentialsProvider *credentialsProvider = [[AWSCognitoCredentialsProvider alloc] initWithRegionType:AWSIoTRegion
                                                                                                    identityPoolId:AWSIoTIdentityPoolId];
    AWSServiceConfiguration *configuration = [[AWSServiceConfiguration alloc] initWithRegion:AWSIoTRegion
                                                                         credentialsProvider:credentialsProvider];
    
    [AWSServiceManager defaultServiceManager].defaultServiceConfiguration = configuration;
    
    _iotManager = [AWSIoTManager defaultIoTManager];
    _iot = [AWSIoT defaultIoT];
    
    _iotDataManager = [AWSIoTDataManager defaultIoTDataManager];
    _iotData = [AWSIoTData defaultIoTData];
    
    return self;
}

- (void)connect {
    __block NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    __block NSString *certificateId = [defaults stringForKey:@"certificateId"];
    
    if (certificateId == nil) {
        NSDictionary *csrDict = @{
                                  @"commonName": AWSIoTCommonName,
                                  @"countryName": AWSIoTCountryName,
                                  @"organizationName": AWSIoTOrganizationName,
                                  @"organizationUnitName": AWSIoTOrganizationUnitName
                                  };
        
        [self.iotManager createKeysAndCertificateFromCsr:csrDict callback:^(AWSIoTCreateCertificateResponse *response) {
            [defaults setObject:response.certificateId forKey:@"certificateId"];
            [defaults setObject:response.certificateArn forKey:@"certificateArn"];
            certificateId = response.certificateId;
            __block NSUUID *uuid = [NSUUID UUID];
            AWSIoTAttachPrincipalPolicyRequest *attachPrincipalPolicyRequest = [[AWSIoTAttachPrincipalPolicyRequest alloc] init];
            attachPrincipalPolicyRequest.policyName = AWSIoTPolicyName;
            attachPrincipalPolicyRequest.principal = response.certificateArn;
            
            AWSTask *task = [self.iot attachPrincipalPolicy:attachPrincipalPolicyRequest];
            [task continueWithBlock:^id _Nullable(AWSTask * _Nonnull task) {
                if (task.error) {
                    NSLog(@"%@", task.error.localizedDescription);
                }
                NSLog(@"%@", task.result);
                
                if (task.exception == nil && task.error == nil) {
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [self.iotDataManager connectWithClientId:[uuid UUIDString] cleanSession:YES certificateId:certificateId statusCallback:[self awsCallback]];
                    });
                }
                
                return nil;
            }];
            
        }];
    } else {
        NSUUID *uuid = [NSUUID UUID];
        [self.iotDataManager connectWithClientId:[uuid UUIDString] cleanSession:YES certificateId:certificateId statusCallback:[self awsCallback]];
    }
}

- (AWSCallback)awsCallback {
    return ^void (AWSIoTMQTTStatus status) {
        dispatch_async(dispatch_get_main_queue(), ^{
            switch (status) {
                case AWSIoTMQTTStatusConnecting: {
                    NSLog(@"Connecting");
                } break;
                case AWSIoTMQTTStatusConnected: {
                    NSLog(@"Connected");
                    self.connected = YES;
                    [self.iotDataManager subscribeToTopic:@"notification/#" qos:0 messageCallback:^(NSData *data) {
                        [[NSNotificationCenter defaultCenter] postNotificationName:AWSIoTEventNotification object:self userInfo: nil];
                    }];
                } break;
                case AWSIoTMQTTStatusConnectionError: {
                    NSLog(@"Connection Error");
                } break;
                case AWSIoTMQTTStatusConnectionRefused: {
                    NSLog(@"Connection Refused");
                } break;
                case AWSIoTMQTTStatusDisconnected: {
                    NSLog(@"Disconnected");
                } break;
                case AWSIoTMQTTStatusProtocolError: {
                    NSLog(@"Protocol Error");
                } break;
                case AWSIoTMQTTStatusUnknown: {
                    NSLog(@"Unknown Status");
                }
                default:
                    break;
            }
        });
    };
}


@end
