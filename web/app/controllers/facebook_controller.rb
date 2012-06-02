require 'fb_graph'

class FacebookController < ApplicationController
  def upload_file
    uploaded_file = params[:uploadedFile]
    name = uploaded_file.original_filename
    path = "/Users/nvenky/#{name}"
    File.open(path, "wb") { |f| f.write(uploaded_file.read) }
    p "Access token from server - #{params[:access_token]}"
    p (get_name params[:access_token])
    send_message_to_online_friends(get_name params[:access_token])
    render :text => "File has been uploaded successfully"
  end

  def find_online_friends
     access_token = params[:access_token]
     user = FbGraph::User.me(access_token).fetch
     min_online_friends_for_testing = 5
     friends = user.friends.select{|friend| is_online?(friend.raw_attributes[:id], min_online_friends_for_testing-=1)}
     .collect{|friend| {:id => friend.raw_attributes[:id], :name => friend.raw_attributes[:name]}}
     json = {:data => friends}
     render :json => json
  end

  def register_user_online
      user_id = get_id params[:access_token]
      registration_id = params[:registration_id]
       if registration_id.nil?
         online_users.delete(user_id)
       else
         online_users[user_id] = registration_id
       end
      render :json => {'status' => 'Success'}
   end

  #private
  def get_name(access_token)
    user = FbGraph::User.me(access_token).fetch
    user.name
  end

  def get_id(access_token)
    user = FbGraph::User.me(access_token).fetch
    user.identifier
  end

  def is_online?(id, min)
    min >=0 || online_users.has_key?(id)
  end

end
