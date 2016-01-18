//
//  ViewController.m
//  mqtt road sign
//
//  Created by Justin Dickow on 12/14/15.
//  Copyright © 2015 Livio. All rights reserved.
//

#import "ViewController.h"
#import <objc/message.h>
#import "IoTManager.h"
@interface ViewController ()
@property (weak, nonatomic) IBOutlet UIStackView *topLevelStackView;
@property (strong, nonatomic) NSMutableArray <NSArray <UIView *> *> *letters;
@property (strong, nonatomic) NSTimer *eventTimer;
@property (nonatomic, getter=isConnected) BOOL connected;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.letters = [[NSMutableArray alloc] init];
    
    // 5 Horizontal views
    for (int i = 0; i < 5; i++) {
        UIStackView *stack = [[UIStackView alloc] init];
        stack.axis = UILayoutConstraintAxisHorizontal;
        stack.distribution = UIStackViewDistributionFillEqually;
        stack.alignment = UIStackViewAlignmentFill;
        stack.spacing = [@(10) floatValue];
        
        for (int j = 0; j < 10; j++) {
            NSMutableArray *letterSegments = [[NSMutableArray alloc] init];
            
            UIStackView *stack2 = [[UIStackView alloc] init];
            stack2.axis = UILayoutConstraintAxisVertical;
            stack2.distribution = UIStackViewDistributionFillEqually;
            stack2.alignment = UIStackViewAlignmentFill;
            stack2.spacing = [@(1) floatValue];
            
            for (int k = 0; k < 7; k++) {
                UIStackView *stack3 = [[UIStackView alloc] init];
                stack3.axis = UILayoutConstraintAxisHorizontal;
                stack3.distribution = UIStackViewDistributionFillEqually;
                stack3.alignment = UIStackViewAlignmentFill;
                stack3.spacing = [@(1) floatValue];
                
                for (int l = 0; l < 5; l++) {
                    UIView *view = [[UIView alloc] init];
                    view.backgroundColor = [UIColor darkGrayColor];
                    [stack3 addArrangedSubview:view];
                    [letterSegments addObject:view];
                }
                
                [stack2 addArrangedSubview:stack3];
            }
            
            [stack addArrangedSubview:stack2];
            [self.letters addObject:letterSegments];
        }
        
        [self.topLevelStackView addArrangedSubview:stack];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    
    [[NSNotificationCenter defaultCenter] addObserverForName:AWSIoTEventNotification object:nil queue:nil usingBlock:^(NSNotification * _Nonnull note) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self displayText:@"            Hazard   Detected"];
            [self.eventTimer invalidate];
            self.eventTimer = [NSTimer scheduledTimerWithTimeInterval:4 target:self selector:@selector(timerFired:) userInfo:nil repeats:NO];
        });
    }];

    [[IoTManager manager] connect];
}


- (void)timerFired:(NSTimer *)timer {
    [self clearAll];
}

- (void)displayText:(NSString *)text {
    [self clearAll];
    NSString *lower = [text lowercaseString];
    for (int i = 0; i < lower.length && i < self.letters.count; i++) {
        NSString *letter = [lower substringWithRange:NSMakeRange(i, 1)];
        NSArray *segment = self.letters[i];
        SEL message = NSSelectorFromString([letter stringByAppendingString:@":"]);
        if ([self respondsToSelector:message]) {
            void (*objc_msgSendTyped)(id self, SEL _cmd, id views) = (void*)objc_msgSend;
            objc_msgSendTyped(self, message, segment);
        } else {
            // TODO: Implement letters that aren't supported OR do a switch to do some other fancy thing
            if ([letter isEqualToString:@" "]) {
                [self clear:segment];
            }
        }
    }
}

- (void)color:(NSArray <UIView *> *)views withSegments:(NSArray <NSNumber *> *)segments {
    NSAssert(segments.count == views.count, @"arrays must be equal length");
    for (int i = 0; i < views.count; i++) {
        if ([segments[i] boolValue]) {
            views[i].backgroundColor = [UIColor colorWithRed:255.0/255.0 green:103.0/255.0 blue:0 alpha:1];
        } else {
            views[i].backgroundColor = [UIColor darkGrayColor];
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Letter patterns

- (void)clearAll {
    for (int i = 0; i < self.letters.count; i++) {
        [self clear:self.letters[i]];
    }
}

- (void)clear:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(NO),@(NO),@(NO),@(NO),
                        @(NO),@(NO),@(NO),@(NO),@(NO),
                        @(NO),@(NO),@(NO),@(NO),@(NO),
                        @(NO),@(NO),@(NO),@(NO),@(NO),
                        @(NO),@(NO),@(NO),@(NO),@(NO),
                        @(NO),@(NO),@(NO),@(NO),@(NO),
                        @(NO),@(NO),@(NO),@(NO),@(NO)];
    [self color:views withSegments:letter];
}

- (void)a:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(YES),@(NO),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(YES),@(YES),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES)];
    [self color:views withSegments:letter];
}

- (void)b:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)c:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)d:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)e:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(YES),@(YES),@(YES),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(YES),@(YES),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(YES),@(YES),@(YES),@(YES)];
    [self color:views withSegments:letter];
}

- (void)f:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(YES),@(YES),@(YES),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(YES),@(YES),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO)];
    [self color:views withSegments:letter];
}

- (void)g:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(YES),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)h:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(YES),@(YES),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES)];
    [self color:views withSegments:letter];
}



- (void)i:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO)];
    [self color:views withSegments:letter];
}


- (void)j:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)k:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(YES),@(NO),
                        @(YES),@(NO),@(YES),@(NO),@(NO),
                        @(YES),@(YES),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(YES),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)l:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)m:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(NO),@(YES),@(YES),
                        @(YES),@(NO),@(YES),@(NO),@(YES),
                        @(YES),@(NO),@(YES),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES)];
    [self color:views withSegments:letter];
}


- (void)n:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(YES),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(YES),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES)];
    [self color:views withSegments:letter];
}

- (void)o:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)p:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO)];
    [self color:views withSegments:letter];
}

- (void)q:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(YES),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(YES),@(NO),
                        @(NO),@(YES),@(YES),@(NO),@(YES)];
    [self color:views withSegments:letter];
}

- (void)r:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(YES),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES)];
    [self color:views withSegments:letter];
}

- (void)s:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(NO),@(YES),@(YES),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(NO),@(YES),@(YES),@(YES),@(NO),
                        @(NO),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)t:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(YES),@(YES),@(YES),@(YES),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO)];
    [self color:views withSegments:letter];
}

- (void)u:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(YES),@(YES),@(NO)];
    [self color:views withSegments:letter];
}

- (void)v:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(NO),@(YES),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO)];
    [self color:views withSegments:letter];
}

- (void)w:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(YES),@(NO),@(YES),
                        @(YES),@(NO),@(YES),@(NO),@(YES),
                        @(YES),@(YES),@(NO),@(YES),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES)];
    [self color:views withSegments:letter];
}

- (void)x:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(NO),@(YES),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(YES),@(NO),@(YES),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES)];
    [self color:views withSegments:letter];
}

- (void)y:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(NO),@(NO),@(NO),@(YES),
                        @(YES),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(YES),@(NO),@(YES),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO)];
    [self color:views withSegments:letter];
}

- (void)z:(NSArray <UIView *>*)views {
    NSArray *letter = @[@(YES),@(YES),@(YES),@(YES),@(YES),
                        @(NO),@(NO),@(NO),@(NO),@(YES),
                        @(NO),@(NO),@(NO),@(YES),@(NO),
                        @(NO),@(NO),@(YES),@(NO),@(NO),
                        @(NO),@(YES),@(NO),@(NO),@(NO),
                        @(YES),@(NO),@(NO),@(NO),@(NO),
                        @(YES),@(YES),@(YES),@(YES),@(YES)];
    [self color:views withSegments:letter];
}

@end
