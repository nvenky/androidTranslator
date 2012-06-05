# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = "simple_qs"
  s.version = "0.2"

  s.required_rubygems_version = Gem::Requirement.new(">= 1.3.6") if s.respond_to? :required_rubygems_version=
  s.authors = ["Marjan Krekoten' (\xD0\x9C\xD0\xB0\xD1\x80'\xD1\x8F\xD0\xBD \xD0\x9A\xD1\x80\xD0\xB5\xD0\xBA\xD0\xBE\xD1\x82\xD0\xB5\xD0\xBD\xD1\x8C)"]
  s.date = "2010-08-17"
  s.description = "SimpleQS library fully wraps Amazon SQS REST API. It allows you perform all kind of calls on queues and messages."
  s.email = "krekoten@gmail.com"
  s.homepage = "http://github.com/krekoten/SimpleQS"
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.8.7")
  s.rubygems_version = "1.8.10"
  s.summary = "Amazon SQS service library"

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<xml-simple>, [">= 1.0.12"])
      s.add_runtime_dependency(%q<ruby-hmac>, [">= 0.3.2"])
      s.add_development_dependency(%q<rspec>, [">= 0"])
    else
      s.add_dependency(%q<xml-simple>, [">= 1.0.12"])
      s.add_dependency(%q<ruby-hmac>, [">= 0.3.2"])
      s.add_dependency(%q<rspec>, [">= 0"])
    end
  else
    s.add_dependency(%q<xml-simple>, [">= 1.0.12"])
    s.add_dependency(%q<ruby-hmac>, [">= 0.3.2"])
    s.add_dependency(%q<rspec>, [">= 0"])
  end
end
