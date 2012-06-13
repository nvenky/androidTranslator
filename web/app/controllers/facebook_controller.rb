require 'fb_graph'

class FacebookController < ApplicationController
  def upload_file
    uploaded_file = params[:uploadedFile]
    name = uploaded_file.original_filename
    path = "/Users/nvenky/#{name}"
    File.open(path, "wb") { |f| f.write(uploaded_file.read) }
    send_message_to_online_friends get_user(params[:access_token]).name
    render :text => "File has been uploaded successfully"
  end

  def upload
    uploaded_file_name = params[:uploaded_file_name]
    fb_user = get_user(params[:access_token])
    user = User.find_by_facebook_id fb_user.identifier
    Image.new({:user => user, :file_name => uploaded_file_name}).save!
    p 'Uploaded Image, sending message to friends'
    send_message_to_online_friends user.name
    render :text => "File has been uploaded successfully"
  end


  def find_online_friends
     access_token = params[:access_token]
     user = get_user access_token
     min_online_friends_for_testing = 5
     friends = user.friends.select{|friend| is_online?(friend.raw_attributes[:id], min_online_friends_for_testing-=1)}
     .collect{|friend| {:id => friend.raw_attributes[:id], :name => friend.raw_attributes[:name]}}
     json = {:id => user.id, :data => friends}
     render :json => json
  end

  def register_user_online
    fb_user = get_user params[:access_token]
    registration_id = params[:registration_id]
    user = User.find_by_facebook_id(fb_user.identifier)
    if user.nil?
      User.new({:facebook_id => fb_user.identifier, :name => fb_user.name, :device_registration_id => registration_id}).save!
    else
      user.device_registration_id = registration_id
      user.save!
    end
    render :json => {'status' => 'Success'}
  end

  #private
  def get_user(access_token)
    FbGraph::User.me(access_token).fetch
  end

  def is_online?(id, min)
    min >=0 || online_users.has_key?(id)
  end

end
