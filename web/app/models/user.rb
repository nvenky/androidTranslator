class User < ActiveRecord::Base
  attr_accessible :facebook_id, :name, :device_registration_id
  has_many :images, :dependent => :destroy

  def create_user(fb_user, device_reg_id)
     facebook_id = fb_user.identifier
     name = fb_user.name
     device_registration_id = device_reg_id
     save!
  end
end
