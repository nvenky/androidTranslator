require 'spec_helper'

describe MessageProducer do

  it 'should produce message to SQS' do
      message_producer = MessageProducer.new
      message_producer.generate_thumbnail_message 'file_name'
      message = SimpleQS::Message.receive(message_producer.queue)
      p message
  end
end
