class AddUserIdToTranslation < ActiveRecord::Migration
  def change
    add_column :translations, :user_id, :integer
  end
end
