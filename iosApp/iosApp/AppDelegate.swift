//
//  AppDelegate.swift
//  iosApp
//
//  Created by Admin on 08/05/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import UIKit
import shared

class AppDelegate: NSObject, UIApplicationDelegate {
    let lifecycleManager = LifecycleManager()
    
    override init() {
        super.init()
#if DEBUG
        Main_iosKt.debugLogs()
#endif
    }
}
