package tools.mo3ta.kam.viewmodel

import androidx.lifecycle.ViewModel
import tools.mo3ta.kam.data.PreferenceManager

class OnboardingViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    fun completeOnboarding() {
        preferenceManager.isOnboarded = true
    }
}
