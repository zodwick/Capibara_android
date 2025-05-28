import Image from "next/image";
import Link from "next/link";

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-amber-50 via-orange-50 to-rose-50">
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        {/* Background Image */}
        <div className="absolute inset-0 opacity-20">
          <Image
            src="/images/sunset_wide_1536_1024_ghibly.png"
            alt="Peaceful sunset background"
            fill
            className="object-cover"
            priority
          />
        </div>
        
        {/* Navigation */}
        <nav className="relative z-10 flex items-center justify-between p-6 md:p-8">
          <div className="flex items-center space-x-3">
            <Image
              src="/images/capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo.png"
              alt="Happy capybara"
              width={40}
              height={40}
              className="rounded-full"
            />
            <span className="text-xl font-serif text-amber-900">;;Sanctuary</span>
          </div>
          
          <div className="hidden md:flex items-center space-x-8">
            {/* <Link href="#features" className="text-amber-800 hover:text-amber-900 transition-colors">
              Features
            </Link>
            <Link href="#screenshots" className="text-amber-800 hover:text-amber-900 transition-colors">
              Screenshots
            </Link> */}
            <Link href="/about" className="text-amber-800 hover:text-amber-900 transition-colors">
              About
            </Link>
            {/* <Link href="#download" className="text-amber-800 hover:text-amber-900 transition-colors">
              Download
            </Link> */}
          </div>
        </nav>

        {/* Hero Content */}
        <div className="relative z-10 max-w-6xl mx-auto px-6 md:px-8 py-20 md:py-32">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="space-y-8">
              <div className="space-y-4">
                <h1 className="text-4xl md:text-6xl font-serif text-amber-900 leading-tight">
                  Find peace in the
                  <span className="block text-orange-700">digital world</span>
                </h1>
                <p className="text-lg md:text-xl text-amber-800 leading-relaxed">
                  A gentle companion for mindful screen time. Watch over your capybara sanctuary 
                  and discover the joy of digital balance.
                </p>
              </div>
              
              <div className="flex flex-col sm:flex-row gap-4">
                <button className="bg-amber-600 hover:bg-amber-700 text-white px-8 py-4 rounded-2xl font-medium transition-all duration-300 shadow-lg hover:shadow-xl">
                  Download for Android
                </button>
                <Link 
                  href="/about"
                  className="border-2 border-amber-600 text-amber-700 hover:bg-amber-50 px-8 py-4 rounded-2xl font-medium transition-all duration-300 text-center"
                >
                  Learn More
                </Link>
              </div>
            </div>
            
            <div className="relative">
              <div className="relative z-10">
                <Image
                  src="/images/capybara_sitting_peacefully_meditation_pose_eyes_closed_small_smile_floating_cherry_blossoms_around_it_white_bg.png"
                  alt="Peaceful meditating capybara"
                  width={400}
                  height={400}
                  className="w-full max-w-md mx-auto capybara-image-hero mix-blend-screen rounded-full"
                />
              </div>
              {/* Floating elements */}
              <div className="absolute top-10 right-10 animate-bounce">
                <div className="w-3 h-3 bg-pink-300 rounded-full opacity-60"></div>
              </div>
              <div className="absolute bottom-20 left-10 animate-pulse">
                <div className="w-2 h-2 bg-orange-300 rounded-full opacity-40"></div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-20 md:py-32 bg-white/50 backdrop-blur-sm">
        <div className="max-w-6xl mx-auto px-6 md:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-serif text-amber-900 mb-4">
              Gentle digital wellness
            </h2>
            <p className="text-lg text-amber-700 max-w-2xl mx-auto">
              No harsh restrictions or guilt. Just peaceful awareness and the company of capybaras.
            </p>
          </div>
          
          <div className="grid md:grid-cols-3 gap-8">
            <div className="text-center space-y-4 p-6">
              <div className="w-20 h-20 mx-auto mb-4">
                <Image
                  src="/images/capybara_cartoon_style_sitting_content_happy__eating_lettuce_with_heart_facing_left_solo.png"
                  alt="Happy capybara"
                  width={80}
                  height={80}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">Mindful Tracking</h3>
              <p className="text-amber-700">
                Gentle awareness of your screen time without judgment or pressure.
              </p>
            </div>
            
            <div className="text-center space-y-4 p-6">
              <div className="w-20 h-20 mx-auto mb-4">
                <Image
                  src="/images/capybara_cartoon_style_relaxed_in_hot_spring_duck_on_head_steam_bubbles_facing_left_solo.png"
                  alt="Relaxed capybara"
                  width={80}
                  height={80}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">Peaceful Goals</h3>
              <p className="text-amber-700">
                Set intentions that feel right for you, not rigid rules that stress you out.
              </p>
            </div>
            
            <div className="text-center space-y-4 p-6">
              <div className="w-20 h-20 mx-auto mb-4">
                <Image
                  src="/images/capybara_cartoon_style_sleeping_curled_up_with_zzz_facing_right_solo.png"
                  alt="Sleeping capybara"
                  width={80}
                  height={80}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">Emotional Connection</h3>
              <p className="text-amber-700">
                Your capybara friends reflect your digital wellness journey with empathy.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Screenshots Section */}
      <section id="screenshots" className="py-20 md:py-32">
        <div className="max-w-6xl mx-auto px-6 md:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-serif text-amber-900 mb-4">
              A sanctuary in your pocket
            </h2>
            <p className="text-lg text-amber-700">
              Experience the gentle beauty of mindful technology
            </p>
          </div>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            <div className="space-y-4">
              <div className="screenshot-container">
                <Image
                  src="/images/screenshot_home_page_harmony.jpg"
                  alt="Peaceful sanctuary view"
                  width={300}
                  height={600}
                  className="w-full h-auto screenshot-image"
                />
              </div>
              <p className="text-center text-sm text-amber-700">When you&apos;re in harmony</p>
            </div>
            
            <div className="space-y-4">
              <div className="screenshot-container">
                <Image
                  src="/images/screenshot_home_page_distress.jpg"
                  alt="Sanctuary needing care"
                  width={300}
                  height={600}
                  className="w-full h-auto screenshot-image"
                />
              </div>
              <p className="text-center text-sm text-amber-700">When balance is needed</p>
            </div>
            
            <div className="space-y-4">
              <div className="screenshot-container">
                <Image
                  src="/images/screenshot_set_goal_page.jpg"
                  alt="Setting gentle goals"
                  width={300}
                  height={600}
                  className="w-full h-auto screenshot-image"
                />
              </div>
              <p className="text-center text-sm text-amber-700">Setting your intentions</p>
            </div>
            
            <div className="space-y-4">
              <div className="screenshot-container">
                <Image
                  src="/images/screenshot_usage_page.jpg"
                  alt="Mindful insights"
                  width={300}
                  height={600}
                  className="w-full h-auto screenshot-image"
                />
              </div>
              <p className="text-center text-sm text-amber-700">Gentle insights</p>
            </div>
          </div>
        </div>
      </section>

      {/* Philosophy Section */}
      <section className="py-20 md:py-32 bg-gradient-to-r from-orange-100 to-amber-100">
        <div className="max-w-4xl mx-auto px-6 md:px-8 text-center">
          <div className="mb-8">
            <Image
              src="/images/capybara_sitting_upright_alert_but_calm_looking_slightly_concerned_but_hopeful_white_bg.png"
              alt="Thoughtful capybara"
              width={120}
              height={120}
              className="mx-auto capybara-image"
            />
          </div>
          
          <h2 className="text-3xl md:text-4xl font-serif text-amber-900 mb-6">
            Technology should serve peace, not steal it
          </h2>
          
          <p className="text-lg md:text-xl text-amber-800 leading-relaxed mb-8">
            We believe in gentle awareness over harsh restrictions. Your capybara sanctuary 
            grows more beautiful as you find balance, creating a positive feedback loop 
            that feels natural and sustainable.
          </p>
          
          <div className="grid md:grid-cols-2 gap-8 text-left">
            <div className="space-y-4">
              <h3 className="text-xl font-serif text-amber-900">No shame, only growth</h3>
              <p className="text-amber-700">
                We don&apos;t believe in digital detox extremes. Instead, we help you build 
                a healthier relationship with technology through mindful awareness.
              </p>
            </div>
            
            <div className="space-y-4">
              <h3 className="text-xl font-serif text-amber-900">Emotional intelligence</h3>
              <p className="text-amber-700">
                Your capybaras respond to your digital habits with empathy, not judgment. 
                They&apos;re here to support your journey, not make you feel guilty.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Download Section */}
      <section id="download" className="py-20 md:py-32">
        <div className="max-w-4xl mx-auto px-6 md:px-8 text-center">
          <div className="mb-8">
            <Image
              src="/images/capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo.png"
              alt="Happy capybara with boba tea"
              width={100}
              height={100}
              className="mx-auto capybara-image"
            />
          </div>
          
          <h2 className="text-3xl md:text-4xl font-serif text-amber-900 mb-6">
            Start your sanctuary today
          </h2>
          
          <p className="text-lg text-amber-700 mb-8 max-w-2xl mx-auto">
            Join thousands who have found peace in their digital lives. 
            Your capybara friends are waiting for you.
          </p>
          
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="bg-amber-600 hover:bg-amber-700 text-white px-8 py-4 rounded-2xl font-medium transition-all duration-300 shadow-lg hover:shadow-xl">
              Download for Android
            </button>
            <button className="border-2 border-amber-600 text-amber-700 hover:bg-amber-50 px-8 py-4 rounded-2xl font-medium transition-all duration-300">
              Coming Soon: iOS
            </button>
          </div>
          
          <p className="text-sm text-amber-600 mt-6">
            Free to download • No ads • Privacy-focused
          </p>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-amber-900 text-amber-100 py-12">
        <div className="max-w-6xl mx-auto px-6 md:px-8">
          <div className="grid md:grid-cols-3 gap-8">
            <div className="space-y-4">
              <div className="flex items-center space-x-3">
                <Image
                  src="/images/capybara_cartoon_style_sitting_calm_facing_left_solo_square_1024_icon.png"
                  alt="Capybara sanctuary icon"
                  width={32}
                  height={32}
                  className="rounded-lg"
                />
                <span className="text-lg font-serif">;;Sanctuary</span>
              </div>
              <p className="text-amber-200">
                A gentle approach to digital wellness, inspired by the peaceful nature of capybaras.
              </p>
            </div>
            
            <div className="space-y-4">
              <h3 className="font-serif text-lg">Connect</h3>
              <div className="space-y-2">
                <p className="text-amber-200">Email: hello@capybarasanctuary.app</p>
                <p className="text-amber-200">Made with 🤍 for mindful living</p>
              </div>
            </div>
            
            <div className="space-y-4">
              <h3 className="font-serif text-lg">Philosophy</h3>
              <p className="text-amber-200">
                &ldquo;In a world of endless notifications, sometimes the most radical act is simply being present.&rdquo;
              </p>
            </div>
          </div>
          
          <div className="border-t border-amber-800 mt-8 pt-8 text-center text-amber-300">
            <p>&copy; 2024 Capybara Sanctuary. Made for humans seeking digital peace.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
