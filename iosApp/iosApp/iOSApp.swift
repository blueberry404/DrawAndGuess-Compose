import SwiftUI
import shared

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    @Environment(\.scenePhase)
    var scenePhase: ScenePhase
    
    var lifecycleManager: LifecycleManager { appDelegate.lifecycleManager }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onChange(of: scenePhase, perform: { newPhase in
                    switch newPhase {
                    case .background: LifecycleRegistryExtKt.stop(lifecycleManager.lifecycle)
                    case .inactive: LifecycleRegistryExtKt.pause(lifecycleManager.lifecycle)
                    case .active: LifecycleRegistryExtKt.resume(lifecycleManager.lifecycle)
                    @unknown default: break
                    }
                })
        }
    }
}
