
# Keep classes in data package explicitly
-keep class tools.mo3ta.kam.data.** { *; }

# Keep UI state and result classes to avoid issues with reflection/serialization
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class tools.mo3ta.kam.**.*UiState { *; }
-keep class tools.mo3ta.kam.**.*Result { *; }
-keep class tools.mo3ta.kam.**.*Response { *; }
