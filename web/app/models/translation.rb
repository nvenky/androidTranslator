class Translation < ActiveRecord::Base
  attr_accessible :data, :image_id
  belongs_to :image
  belongs_to :user

  def translation_data
    "#{user.name}: #{data}"
  end

  def as_json(options)
    super({:only => [], :methods => :translation_data})
  end
end
