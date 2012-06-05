class ImagesController < ApplicationController
  def index
    images = Image.all
    render json: {:data => images}
  end
end

