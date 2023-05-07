//
//  LifecycleManager.swift
//  iosApp
//
//  Created by Admin on 07/05/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

@objc
class LifecycleManager: NSObject, ObservableObject {
    let lifecycle: LifecycleRegistry
    let root: DAGRootComponent
    
    override init() {
        lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        
        root = DefaultDAGRootComponent(
            componentContext: DecomposeDefaultComponentContext(lifecycle: lifecycle))
        LifecycleRegistryExtKt.create(lifecycle)
    }
    
    deinit {
        LifecycleRegistryExtKt.destroy(lifecycle)
    }
}
