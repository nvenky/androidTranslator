class ImagesController < ApplicationController
  def index
    images = Image.limit(20).order('id desc')
    render json: {:data => images}
  end

  def show
    image = Image.find(params[:image_id])
    render json: {:image_id => image.id, :file_name => image.file_name, :translations => []}
  end

  def add_translation
    translation = Translation.new :data => params[:translation_data]
    translation.image = Image.find(params[:image_id])
    translation.save!
    render json: {:status => 'Succes'}
  end
end

