# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = "c2dm"
  s.version = "0.2.2"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Amro Mousa"]
  s.date = "2012-05-24"
  s.description = "c2dm sends push notifications to Android devices via google c2dm"
  s.email = ["amromousa@gmail.com"]
  s.homepage = ""
  s.licenses = ["MIT"]
  s.require_paths = ["lib"]
  s.rubyforge_project = "c2dm"
  s.rubygems_version = "1.8.10"
  s.summary = "sends push notifications to Android devices"

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<httparty>, [">= 0"])
      s.add_runtime_dependency(%q<json>, [">= 0"])
    else
      s.add_dependency(%q<httparty>, [">= 0"])
      s.add_dependency(%q<json>, [">= 0"])
    end
  else
    s.add_dependency(%q<httparty>, [">= 0"])
    s.add_dependency(%q<json>, [">= 0"])
  end
end
