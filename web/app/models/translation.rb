class Translation < ActiveRecord::Base
  attr_accessible :data, :image_id
  belongs_to :image
end
