# Keep Room database entities and DAOs
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.UnstableApi
