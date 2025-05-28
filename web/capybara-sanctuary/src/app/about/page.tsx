import Image from "next/image";
import Link from "next/link";

export default function About() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-amber-50 via-orange-50 to-rose-50">
      {/* Navigation */}
      <nav className="flex items-center justify-between p-6 md:p-8">
        <Link href="/" className="flex items-center space-x-3">
          <Image
            src="/images/capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo.png"
            alt="Happy capybara"
            width={40}
            height={40}
            className="rounded-full"
          />
          <span className="text-xl font-serif text-amber-900">;;Sanctuary</span>
        </Link>
        
        <Link 
          href="/" 
          className="text-amber-800 hover:text-amber-900 transition-colors font-medium"
        >
          ← Back to Home
        </Link>
      </nav>

      {/* Hero Section */}
      <section className="max-w-4xl mx-auto px-6 md:px-8 py-12 md:py-20">
        <div className="text-center mb-16">
          <div className="mb-8">
            <Image
              src="/images/capybara_sitting_upright_alert_but_calm_looking_slightly_concerned_but_hopeful_white_bg.png"
              alt="Thoughtful capybara"
              width={120}
              height={120}
              className="mx-auto capybara-image"
            />
          </div>
          
          <h1 className="text-4xl md:text-5xl font-serif text-amber-900 mb-6">
            Why capybaras?
          </h1>
          
          <p className="text-lg md:text-xl text-amber-800 leading-relaxed max-w-3xl mx-auto">
            In a world of aggressive notifications and addictive design patterns, 
            we chose the most peaceful animal as our guide to digital wellness.
          </p>
        </div>

        {/* Philosophy Sections */}
        <div className="space-y-20">
          {/* Section 1: The Capybara Way */}
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="space-y-6">
              <h2 className="text-3xl font-serif text-amber-900">The Capybara Way</h2>
              <p className="text-amber-800 leading-relaxed">
                Capybaras are known for their calm, peaceful nature. They don&apos;t rush, 
                they don&apos;t stress, and they live in harmony with their environment. 
                They&apos;re social creatures who know when to engage and when to rest.
              </p>
              <p className="text-amber-800 leading-relaxed">
                This is exactly the relationship we want you to have with technology - 
                mindful, balanced, and peaceful.
              </p>
            </div>
            <div className="relative">
              <Image
                src="/images/capybara_cartoon_style_relaxed_in_hot_spring_duck_on_head_steam_bubbles_facing_left_solo.png"
                alt="Relaxed capybara in hot spring"
                width={300}
                height={300}
                className="w-full max-w-sm mx-auto capybara-image"
              />
            </div>
          </div>

          {/* Section 2: Gentle Awareness */}
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="order-2 md:order-1 relative">
              <Image
                src="/images/capybara_cartoon_style_sitting_content_happy__eating_lettuce_with_heart_facing_left_solo.png"
                alt="Happy capybara eating"
                width={300}
                height={300}
                className="w-full max-w-sm mx-auto capybara-image"
              />
            </div>
            <div className="order-1 md:order-2 space-y-6">
              <h2 className="text-3xl font-serif text-amber-900">Gentle Awareness</h2>
              <p className="text-amber-800 leading-relaxed">
                Traditional screen time apps use shame and restriction. We believe 
                in gentle awareness - helping you understand your patterns without 
                judgment.
              </p>
              <p className="text-amber-800 leading-relaxed">
                When your capybaras are happy, you know you&apos;re in balance. When they 
                need care, it&apos;s a gentle reminder to check in with yourself.
              </p>
            </div>
          </div>

          {/* Section 3: Emotional Intelligence */}
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="space-y-6">
              <h2 className="text-3xl font-serif text-amber-900">Emotional Intelligence</h2>
              <p className="text-amber-800 leading-relaxed">
                Your sanctuary reflects your digital wellness journey. Each capybara 
                has different moods and needs, just like the different aspects of 
                your relationship with technology.
              </p>
              <p className="text-amber-800 leading-relaxed">
                Some days you might need more screen time for work or connection. 
                Other days, you might crave digital silence. Your capybaras understand.
              </p>
            </div>
            <div className="relative">
              <Image
                src="/images/capybara_sitting_peacefully_meditation_pose_eyes_closed_small_smile_floating_cherry_blossoms_around_it_white_bg.png"
                alt="Meditating capybara"
                width={300}
                height={300}
                className="w-full max-w-sm mx-auto capybara-image-hero"
              />
            </div>
          </div>
        </div>

        {/* Features Deep Dive */}
        <div className="mt-32">
          <h2 className="text-3xl md:text-4xl font-serif text-amber-900 text-center mb-16">
            How it works
          </h2>
          
          <div className="grid md:grid-cols-2 gap-8">
            <div className="bg-white/60 backdrop-blur-sm rounded-3xl p-8 space-y-4">
              <div className="w-16 h-16 mb-4">
                <Image
                  src="/images/capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo.png"
                  alt="Happy capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">Daily Check-ins</h3>
              <p className="text-amber-700">
                Your sanctuary updates throughout the day based on your screen time. 
                No manual logging required - just gentle awareness.
              </p>
            </div>
            
            <div className="bg-white/60 backdrop-blur-sm rounded-3xl p-8 space-y-4">
              <div className="w-16 h-16 mb-4">
                <Image
                  src="/images/capybara_cartoon_style_sleeping_curled_up_with_zzz_facing_right_solo.png"
                  alt="Sleeping capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">Peaceful Goals</h3>
              <p className="text-amber-700">
                Set intentions that feel right for you. Your goals can change as 
                your life changes - flexibility is key to sustainability.
              </p>
            </div>
            
            <div className="bg-white/60 backdrop-blur-sm rounded-3xl p-8 space-y-4">
              <div className="w-16 h-16 mb-4">
                <Image
                  src="/images/capybara_sitting_upright_alert_but_calm_looking_slightly_concerned_but_hopeful_white_bg.png"
                  alt="Alert capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">Mindful Insights</h3>
              <p className="text-amber-700">
                Understand your patterns without judgment. See which apps bring 
                you joy and which ones drain your energy.
              </p>
            </div>
            
            <div className="bg-white/60 backdrop-blur-sm rounded-3xl p-8 space-y-4">
              <div className="w-16 h-16 mb-4">
                <Image
                  src="/images/capybara_cartoon_style_relaxed_in_hot_spring_duck_on_head_steam_bubbles_facing_left_solo.png"
                  alt="Relaxed capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">Gentle Reminders</h3>
              <p className="text-amber-700">
                When your capybaras need care, you&apos;ll get a gentle notification. 
                No harsh alarms or guilt trips - just a friendly check-in.
              </p>
            </div>
          </div>
        </div>

        {/* Privacy Section */}
        <div className="mt-32 bg-gradient-to-r from-orange-100 to-amber-100 rounded-3xl p-8 md:p-12">
          <div className="text-center mb-8">
            <h2 className="text-3xl font-serif text-amber-900 mb-4">
              Your data stays with you
            </h2>
            <p className="text-lg text-amber-800">
              Privacy isn&apos;t just a feature - it&apos;s fundamental to digital wellness.
            </p>
          </div>
          
          <div className="grid md:grid-cols-3 gap-6 text-center">
            <div className="space-y-3">
              <div className="text-2xl">🔒</div>
              <h3 className="font-serif text-amber-900">Local Storage</h3>
              <p className="text-sm text-amber-700">
                All your data stays on your device. We never see your usage patterns.
              </p>
            </div>
            
            <div className="space-y-3">
              <div className="text-2xl">🚫</div>
              <h3 className="font-serif text-amber-900">No Tracking</h3>
              <p className="text-sm text-amber-700">
                No analytics, no tracking, no data collection. Just you and your sanctuary.
              </p>
            </div>
            
            <div className="space-y-3">
              <div className="text-2xl">💝</div>
              <h3 className="font-serif text-amber-900">Free Forever</h3>
              <p className="text-sm text-amber-700">
                No subscriptions, no premium features. Digital wellness should be accessible to all.
              </p>
            </div>
          </div>
        </div>

        {/* Call to Action */}
        <div className="mt-32 text-center">
          <div className="mb-8">
            <Image
              src="/images/capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo.png"
              alt="Happy capybara with boba tea"
              width={100}
              height={100}
              className="mx-auto capybara-image"
            />
          </div>
          
          <h2 className="text-3xl font-serif text-amber-900 mb-6">
            Ready to find your digital peace?
          </h2>
          
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="bg-amber-600 hover:bg-amber-700 text-white px-8 py-4 rounded-2xl font-medium transition-all duration-300 shadow-lg hover:shadow-xl">
              Download for Android
            </button>
            <Link 
              href="/"
              className="border-2 border-amber-600 text-amber-700 hover:bg-amber-50 px-8 py-4 rounded-2xl font-medium transition-all duration-300 text-center"
            >
              Back to Home
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
} 