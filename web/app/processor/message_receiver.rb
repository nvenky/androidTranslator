class MessageReceiver < SQS
  def comsume_message
    queue.send_message({:file_name => file_name}.to_json)
    messages = queue.receive_messages
    messages.first.delete
  end
end
