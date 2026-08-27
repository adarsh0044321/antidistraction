package com.antigravity.antidistraction.domain.engine

import com.antigravity.antidistraction.domain.model.IntentionType
import com.antigravity.antidistraction.domain.model.InterventionLevel
import com.antigravity.antidistraction.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeEscalationEngine @Inject constructor() {

    fun deEscalateLevel(
        currentLevel: InterventionLevel,
        recentIntentions: List<IntentionType>,
        userClassification: String
    ): InterventionLevel {
        // Signal 1: User classified app as Productive
        if (userClassification == "PRODUCTIVE") {
            Logger.i("DeEscalationEngine", "De-escalated level due to PRODUCTIVE user classification.")
            return InterventionLevel.LEVEL_0
        }

        // Signal 2: User consistently selects IMPORTANT or COMMUNICATION intentions
        val importantCount = recentIntentions.count { it == IntentionType.IMPORTANT || it == IntentionType.COMMUNICATION }
        if (importantCount >= 2 && currentLevel.severity > 1) {
            val deEscalated = InterventionLevel.values()[currentLevel.severity - 1]
            Logger.i("DeEscalationEngine", "De-escalated level from ${currentLevel.name} to ${deEscalated.name} (important intentions).")
            return deEscalated
        }

        return currentLevel
    }
}
