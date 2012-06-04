require 'simple_qs'

class MessageProducer

  def generate_thumbnail_message(file_name)
    queue.send_message({:file_name => file_name}.to_json)
  end

  def queue
    @@queue ||= initialize_sqs_and_get_queue
  end

  private
  def initialize_sqs_and_get_queue
    SimpleQS.account_id = 'nvenky@gmail.com'
    SimpleQS.access_key_id = 'AKIAJXINCOD3QJRYK6EA'
    SimpleQS.secret_access_key = '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'
    SimpleQS.host = :us_east_1
    SimpleQS::Queue.new('androidTranslatorMessaging')
  end
end
