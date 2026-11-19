package ch.rmy.android.http_shortcuts.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration13To14 : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `title_size` REAL")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `title_color` TEXT")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `value_size` REAL")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `value_color` TEXT")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `unit` TEXT")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `subtitle_text` TEXT")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `subtitle_position` TEXT")
        db.execSQL("ALTER TABLE `variable_widget` ADD COLUMN `padding` TEXT")

        db.execSQL("ALTER TABLE `widget` ADD COLUMN `background` TEXT")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `title_size` REAL")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `title_color` TEXT")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `title_source` TEXT")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `title_text` TEXT")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `subtitle_source` TEXT")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `subtitle_text` TEXT")
        db.execSQL("ALTER TABLE `widget` ADD COLUMN `padding` TEXT")
    }
}
