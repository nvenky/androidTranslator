require 'spec_helper'

describe "images/edit" do
  before(:each) do
    @image = assign(:image, stub_model(Image,
      :user_id => "",
      :file_name => "MyString"
    ))
  end

  it "renders the edit image form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form", :action => images_path(@image), :method => "post" do
      assert_select "input#image_user_id", :name => "image[user_id]"
      assert_select "input#image_file_name", :name => "image[file_name]"
    end
  end
end
