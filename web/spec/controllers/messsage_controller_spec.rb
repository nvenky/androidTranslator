require 'spec_helper'
  describe MessageController do
  it 'should register device' do
    post 'register_device' , :registration_id => 'reg', :access_token => 'tok'
  end

  it 'should send message to C2DM' do
    get 'send_message'
  end
end
