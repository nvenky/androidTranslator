class AddDeviceRegistrationIdToUser < ActiveRecord::Migration
  def change
    add_column :users, :device_registration_id, :string
  end
end
