package com.antigravity.antidistraction.data.local

import com.antigravity.antidistraction.di.MIGRATION_1_2
import com.antigravity.antidistraction.di.MIGRATION_2_3
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomMigrationTest {

    @Test
    fun migrationVersionsAreSequential() {
        assertEquals(1, MIGRATION_1_2.startVersion)
        assertEquals(2, MIGRATION_1_2.endVersion)
        assertEquals(2, MIGRATION_2_3.startVersion)
        assertEquals(3, MIGRATION_2_3.endVersion)
    }
}
