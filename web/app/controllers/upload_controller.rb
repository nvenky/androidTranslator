class UploadController < ApplicationController
  def upload_file
    uploaded_file = params[:uploadedFile]
    name = uploaded_file.original_filename
    path = "/Users/nvenky/#{name}"
    File.open(path, "wb") { |f| f.write(uploaded_file.read) }
    render :text => "File has been uploaded successfully"
  end
end
