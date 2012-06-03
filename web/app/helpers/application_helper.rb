module ApplicationHelper
  def send_message_to_online_friends(name = 'Dummy')
    c2dm = C2DM.new(auth_token)
    User.all(:conditions => 'device_registration_id IS NOT NULL').each{ |user|
      p "Sending message to #{user.device_registration_id} for pic taken by #{name}"
      notification = {
        #:registration_id => "APA91bFnXJZuboiLQQM1pqDigsgfvGpBC1L3RQfMUGy1JBXCtso3v6KBTBWzC-hZ6R50X_PGFbEMFB8m9eiVj7LFvh80rCWEfLUNJf5ybmB9mRlP_99ia02eAf8Qt_aMuRjuNd8PD1DxUqkGNZ5Zc_TNk
        :registration_id => user.device_registration_id,
        :data => {
        :payload => name,
      },
      :collapse_key => "foobar" #optional
      }
      #C2DM.send_notifications(notifications)
      c2dm.send_notification(notification)
    }
  end

  private
  def auth_token
    # @auth_token ||= generate_auth_token
    @auth_token = 'DQAAAMMAAAArHJogMonIrMly8m1qXo0VGS1YUVlqbW6M_41ucl2fgoN4F_t7yAobtJR17LngcU897OEM233YfAezmaW7JC6VUYhwH3lgtVZB2wJlKhEuEvRptEoqqM7AP1zwQbo4uU13YBn8JyY4356HhiY-zV4oWLDZGvUeiMbhR8A0mGPLLH3ymO2IVMSVp_kLzJlNO5ERscO28ivL5plsgGgHbniacgMDUuvdbtVUnJ5K68vL2szj1I9Fey-K-3FFIxdZOLSqJCGsE0UPR332vn3Wzxnl'
  end

end
