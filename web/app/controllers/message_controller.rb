require 'net/https'

class MessageController < ApplicationController
   def send
     #C2DM.authenticate!("nvenky@gmail.com", "imin#007", "android-translator")
     c2dm = C2DM.new(auth_token)
     notification = {
      :registration_id => "APA91bFnXJZuboiLQQM1pqDigsgfvGpBC1L3RQfMUGy1JBXCtso3v6KBTBWzC-hZ6R50X_PGFbEMFB8m9eiVj7LFvh80rCWEfLUNJf5ybmB9mRlP_99ia02eAf8Qt_aMuRjuNd8PD1DxUqkGNZ5Zc_TNkxGB8bZyvw",
      :data => {
        :payload => "Arnold",
      },
      :collapse_key => "foobar" #optional
    }
    #C2DM.send_notifications(notifications)
    c2dm.send_notification(notification)
   end

   def auth_token
    # @auth_token ||= generate_auth_token
    @auth_token = 'DQAAAMMAAAArHJogMonIrMly8m1qXo0VGS1YUVlqbW6M_41ucl2fgoN4F_t7yAobtJR17LngcU897OEM233YfAezmaW7JC6VUYhwH3lgtVZB2wJlKhEuEvRptEoqqM7AP1zwQbo4uU13YBn8JyY4356HhiY-zV4oWLDZGvUeiMbhR8A0mGPLLH3ymO2IVMSVp_kLzJlNO5ERscO28ivL5plsgGgHbniacgMDUuvdbtVUnJ5K68vL2szj1I9Fey-K-3FFIxdZOLSqJCGsE0UPR332vn3Wzxnl'
   end

   def register_device
      p params.inspect
   end

   private
   def generate_auth_token
    uri =  'https://www.google.com/accounts/ClientLogin'
    url = URI.parse(uri)
    req = Net::HTTP::Get.new(url.path + '--data-urlencode Email=nvenky@gmail.com --data-urlencode Passwd=imin%007 -d accountType=GOOGLE -d source=android-translator -d service=ac2dm')
    res = Net::HTTP.start(url.host, url.port) {|http|
      http.request(req)
    }
    res
   end
end
