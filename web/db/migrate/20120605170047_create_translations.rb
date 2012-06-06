class CreateTranslations < ActiveRecord::Migration
  def change
    create_table :translations do |t|
      t.string :data
      t.integer :image_id

      t.timestamps
    end
  end
end
