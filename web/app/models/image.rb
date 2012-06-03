class Image < ActiveRecord::Base
  attr_accessible :file_name, :user
  belongs_to :user
end
