require 'spec_helper'

describe "mailbox routes" do
  it "should route  /mailbox to mailbox#index" do
    {:post => '/register'}. should route_to(:controller => "message", :action => "register_device")
  end
end

