import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController(rootComponent: appDelegate.lifecycleManager.root)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



