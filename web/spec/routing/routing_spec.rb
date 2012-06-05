require 'spec_helper'

describe "routes" do
  it "should route  /register to facebook#register_user_online" do
    {:post => '/register'}. should route_to(:controller => "facebook", :action => "register_user_online")
  end

  it "should route  /images to images#index" do
    {:post => '/images'}. should route_to(:controller => "images", :action => "index")
  end
end

