//
//  IoTManager.h
//  mqtt road sign
//
//  Created by Justin Dickow on 12/15/15.
//  Copyright © 2015 Livio. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "IoTConstants.h"

@interface IoTManager : NSObject
@property (nonatomic, getter=isConnected, readonly) BOOL connected;
+ (instancetype)manager;
- (void)connect;
@end
