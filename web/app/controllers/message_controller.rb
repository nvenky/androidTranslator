require 'net/https'

class MessageController < ApplicationController

      def send_message
     #C2DM.authenticate!("nvenky@gmail.com", "****", "android-translator")
     send_message_to_online_friends
     render :json => {'status' => 'Success'}
   end


   private
   def generate_auth_token
    uri =  'https://www.google.com/accounts/ClientLogin'
    url = URI.parse(uri)
    req = Net::HTTP::Get.new(url.path + '--data-urlencode Email=nvenky@gmail.com --data-urlencode Passwd=imin% -d accountType=GOOGLE -d source=android-translator -d service=ac2dm')
    res = Net::HTTP.start(url.host, url.port) {|http|
      http.request(req)
    }
    res
   end

end
