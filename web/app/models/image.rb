require 'simple_qs'

class Image < ActiveRecord::Base
  attr_accessible :file_name, :user
  belongs_to :user
  after_save :generate_thumbnails

  def generate_thumbnails
     message_producer.generate_thumbnail_message file_name
  end

  def message_producer
    @@message_producer ||= MessageProducer.new
  end
end
