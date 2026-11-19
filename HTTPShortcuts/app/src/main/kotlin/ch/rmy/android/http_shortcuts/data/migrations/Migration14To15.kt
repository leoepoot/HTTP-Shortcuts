package ch.rmy.android.http_shortcuts.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration14To15 : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `title_font_weight` INTEGER")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `value_font_weight` INTEGER")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `unit_size` REAL")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `unit_color` TEXT")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `unit_font_weight` INTEGER")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `subtitle_size` REAL")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `subtitle_color` TEXT")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `subtitle_font_weight` INTEGER")

        db.execSQL("ALTER TABLE `widget` ADD COLUMN `title_font_weight` INTEGER")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `subtitle_size` REAL")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `subtitle_color` TEXT")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `subtitle_font_weight` INTEGER")
    }
}
