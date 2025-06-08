"use client";

import Image from "next/image";
import Link from "next/link";
import { useState } from "react";

export default function Home() {
  const [showPopup, setShowPopup] = useState(false);

  const closePopup = () => {
    setShowPopup(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-amber-50 via-orange-50 to-rose-50">
      {/* Popup Modal */}
      {showPopup && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-3xl p-8 max-w-md w-full text-center space-y-6 shadow-2xl">
            <div className="w-20 h-20 mx-auto">
              <Image
                src="/images/capybara_cartoon_style_sitting_content_happy__eating_lettuce_with_heart_facing_left_solo.png"
                alt="Happy capybara"
                width={80}
                height={80}
                className="w-full h-full object-contain capybara-image"
              />
            </div>
            <h3 className="text-2xl font-serif text-amber-900">
              Almost ready! 🚧
            </h3>
            <p className="text-amber-700 leading-relaxed">
              Our capybara sanctuary is still being lovingly crafted. We&apos;re
              putting the finishing touches on this peaceful digital wellness
              experience.
            </p>
            <p className="text-amber-600 text-sm">
              Sign up for updates to be the first to know when it&apos;s ready!
            </p>
            <button
              onClick={closePopup}
              className="bg-amber-600 hover:bg-amber-700 text-white px-8 py-3 rounded-2xl font-medium transition-all duration-300 shadow-lg hover:shadow-xl"
            >
              Got it!
            </button>
          </div>
        </div>
      )}

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
        <nav className="relative z-10 flex items-center justify-between p-4 md:p-6">
          <div className="flex items-center space-x-3">
            <Image
              src="/images/capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo.png"
              alt="Happy capybara"
              width={40}
              height={40}
              className="rounded-full"
            />
            <span className="text-xl font-serif text-amber-900">
              ;;Sanctuary
            </span>
          </div>

          <div className="hidden md:flex items-center space-x-8">
            {/* <Link href="#features" className="text-amber-800 hover:text-amber-900 transition-colors">
              Features
            </Link>
            <Link href="#screenshots" className="text-amber-800 hover:text-amber-900 transition-colors">
              Screenshots
            </Link> */}
            <Link
              href="/about"
              className="text-amber-800 hover:text-amber-900 transition-colors"
            >
              About
            </Link>
            {/* <Link href="#download" className="text-amber-800 hover:text-amber-900 transition-colors">
              Download
            </Link> */}
          </div>
        </nav>

        {/* Hero Content */}
        <div className="relative z-10 max-w-6xl mx-auto px-6 md:px-8 py-4 md:py-8">
          <div className="grid md:grid-cols-2 gap-8 md:gap-12 items-center">
            <div className="space-y-8">
              <div className="space-y-4">
                <h1 className="text-4xl md:text-6xl font-serif text-amber-900 leading-tight">
                  Find peace in the
                  <span className="block text-orange-700">digital world</span>
                </h1>
                <p className="text-lg md:text-xl text-amber-800 leading-relaxed">
                  Guilt triiping you into using your phone less.{" "}
                </p>
              </div>

              <div className="flex flex-col sm:flex-row gap-4">
                <Link href="https://github.com/zodwick/Capibara_android">
                  <button
                    // onClick={handleDownloadClick}
                    className="bg-amber-600 hover:bg-amber-700 text-white px-8 py-4 rounded-2xl cursor-pointer font-medium transition-all duration-300 shadow-lg hover:shadow-xl"
                  >
                    Download for Android
                  </button>
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

      {/* Problems Section */}
      <section className="py-20 md:py-32 bg-gradient-to-r from-rose-50 to-orange-50">
        <div className="max-w-6xl mx-auto px-6 md:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-serif text-amber-900 mb-4">
              🧠 Reclaim Your Time. Regain Your Focus.
            </h2>
            <p className="text-lg text-amber-700 max-w-2xl mx-auto">
              A smarter digital wellbeing app that actually works—even when your
              self-control doesn&apos;t
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            <div className="bg-white/70 backdrop-blur-sm rounded-3xl p-8 text-center space-y-4 shadow-lg">
              <div className="w-16 h-16 mx-auto mb-4">
                <Image
                  src="/images/capybara_sitting_upright_alert_but_calm_looking_slightly_concerned_but_hopeful_white_bg.png"
                  alt="Concerned capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">
                You Check Your Phone 150+ Times Daily
              </h3>
              {/* <p className="text-amber-700">
                Every buzz, ping, and notification pulls you away from what matters. Your attention 
                is scattered across dozens of apps, leaving you feeling mentally exhausted.
              </p> */}
            </div>

            <div className="bg-white/70 backdrop-blur-sm rounded-3xl p-8 text-center space-y-4 shadow-lg">
              <div className="w-16 h-16 mx-auto mb-4">
                <Image
                  src="/images/capybara_cartoon_style_sitting_content_happy__eating_lettuce_with_heart_facing_left_solo.png"
                  alt="Stressed capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">
                You Lose 4 Hours Daily to Mindless Scrolling
              </h3>
              {/* <p className="text-amber-700">
                "Just a quick check" becomes an hour-long rabbit hole. You know you're wasting time, 
                but somehow you can't stop reaching for your phone.
              </p> */}
            </div>

            <div className="bg-white/70 backdrop-blur-sm rounded-3xl p-8 text-center space-y-4 shadow-lg">
              <div className="w-16 h-16 mx-auto mb-4">
                <Image
                  src="/images/capybara_cartoon_style_sleeping_curled_up_with_zzz_facing_right_solo.png"
                  alt="Tired capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">
                Your Sleep is Hijacked by Blue Light
              </h3>
              {/* <p className="text-amber-700">
                Late-night scrolling destroys your sleep quality. You're tired, irritable, and less 
                productive the next day—creating a vicious cycle of phone dependency.
              </p> */}
            </div>

            <div className="bg-white/70 backdrop-blur-sm rounded-3xl p-8 text-center space-y-4 shadow-lg">
              <div className="w-16 h-16 mx-auto mb-4">
                <Image
                  src="/images/capybara_cartoon_style_relaxed_in_hot_spring_duck_on_head_steam_bubbles_facing_left_solo.png"
                  alt="Anxious capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">
                You Feel Anxious Without Your Phone
              </h3>
              {/* <p className="text-amber-700">
                Phantom vibrations, constant FOMO, and the urge to check your phone every few minutes. 
                Your device has become a digital security blanket you can't put down.
              </p> */}
            </div>

            <div className="bg-white/70 backdrop-blur-sm rounded-3xl p-8 text-center space-y-4 shadow-lg">
              <div className="w-16 h-16 mx-auto mb-4">
                <Image
                  src="/images/capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo.png"
                  alt="Distracted capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">
                Your Relationships Are Suffering
              </h3>
              {/* <p className="text-amber-700">
                You're physically present but mentally absent. Family dinners, conversations with friends, 
                and quality time are interrupted by the constant pull of your screen.
              </p> */}
            </div>

            <div className="bg-white/70 backdrop-blur-sm rounded-3xl p-8 text-center space-y-4 shadow-lg">
              <div className="w-16 h-16 mx-auto mb-4">
                <Image
                  src="/images/capybara_sitting_peacefully_meditation_pose_eyes_closed_small_smile_floating_cherry_blossoms_around_it_white_bg.png"
                  alt="Overwhelmed capybara"
                  width={64}
                  height={64}
                  className="w-full h-full object-contain capybara-image"
                />
              </div>
              <h3 className="text-xl font-serif text-amber-900">
                You&apos;ve Tried Everything to Stop But Nothing Sticks
              </h3>
              {/* <p className="text-amber-700">
                App timers, digital detoxes, willpower—you&apos;ve tried it all. But within days (or hours), 
                you&apos;re back to your old habits, feeling defeated and out of control.
              </p> */}
            </div>
          </div>

          <div className="text-center mt-12">
            <p className="text-lg text-amber-800 font-medium">
              What if there was a gentler way to find balance?
            </p>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section
        id="features"
        className="py-20 md:py-32 bg-white/50 backdrop-blur-sm"
      >
        <div className="max-w-6xl mx-auto px-6 md:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-serif text-amber-900 mb-4">
              Gentle digital wellness
            </h2>
            <p className="text-lg text-amber-700 max-w-2xl mx-auto">
              No harsh restrictions or guilt. Just peaceful awareness and the
              company of capybaras.
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
              <h3 className="text-xl font-serif text-amber-900">
                Mindful Tracking
              </h3>
              <p className="text-amber-700">
                Gentle awareness of your screen time without judgment or
                pressure.
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
              <h3 className="text-xl font-serif text-amber-900">
                Peaceful Goals
              </h3>
              <p className="text-amber-700">
                Set intentions that feel right for you, not rigid rules that
                stress you out.
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
              <h3 className="text-xl font-serif text-amber-900">
                Emotional Connection
              </h3>
              <p className="text-amber-700">
                Your capybara friends reflect your digital wellness journey with
                empathy.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Widget Section */}
      <section className="py-20 md:py-32 bg-white/50 backdrop-blur-sm">
        <div className="max-w-6xl mx-auto px-6 md:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-serif text-amber-900 mb-4">
              Home Screen Widget
            </h2>
            <p className="text-lg text-amber-700">
              Your sanctuary, always visible
            </p>
          </div>

          <div className="max-w-md mx-auto">
            <Image
              src="/images/Screenshot_widget_cropped.png"
              alt="Capybara Sanctuary home screen widget"
              width={400}
              height={400}
              className="w-full h-auto rounded-xl shadow-lg"
            />
          </div>
        </div>
      </section>

      {/* Screenshots Section */}
      <section
        id="screenshots"
        className="py-20 md:py-32 bg-gradient-to-br from-amber-50 via-orange-50 to-rose-50"
      >
        <div className="max-w-6xl mx-auto px-6 md:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-serif text-amber-900 mb-4">
              A sanctuary in your pocket
            </h2>
            <p className="text-lg text-amber-700">
              Experience the gentle beauty of mindful technology
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="group">
              <div className="relative bg-gradient-to-br from-white to-gray-50 rounded-3xl p-4 shadow-2xl hover:shadow-3xl transition-all duration-300 hover:scale-105">
                <div className="absolute inset-0 bg-gradient-to-br from-amber-100/20 to-orange-100/20 rounded-3xl"></div>
                <div className="relative">
                  <Image
                    src="/images/screenshot_home_page_harmony.jpg"
                    alt="Peaceful sanctuary view"
                    width={300}
                    height={600}
                    className="w-full h-auto rounded-2xl shadow-lg"
                  />
                </div>
              </div>
              <p className="text-center text-amber-700 font-medium mt-6">
                When you&apos;re in harmony
              </p>
            </div>

            <div className="group">
              <div className="relative bg-gradient-to-br from-white to-gray-50 rounded-3xl p-4 shadow-2xl hover:shadow-3xl transition-all duration-300 hover:scale-105">
                <div className="absolute inset-0 bg-gradient-to-br from-rose-100/20 to-pink-100/20 rounded-3xl"></div>
                <div className="relative">
                  <Image
                    src="/images/screenshot_home_page_distress.jpg"
                    alt="Sanctuary needing care"
                    width={300}
                    height={600}
                    className="w-full h-auto rounded-2xl shadow-lg"
                  />
                </div>
              </div>
              <p className="text-center text-amber-700 font-medium mt-6">
                When balance is needed
              </p>
            </div>

            <div className="group">
              <div className="relative bg-gradient-to-br from-white to-gray-50 rounded-3xl p-4 shadow-2xl hover:shadow-3xl transition-all duration-300 hover:scale-105">
                <div className="absolute inset-0 bg-gradient-to-br from-green-100/20 to-emerald-100/20 rounded-3xl"></div>
                <div className="relative">
                  <Image
                    src="/images/screenshot_set_goal_page.jpg"
                    alt="Setting gentle goals"
                    width={300}
                    height={600}
                    className="w-full h-auto rounded-2xl shadow-lg"
                  />
                </div>
              </div>
              <p className="text-center text-amber-700 font-medium mt-6">
                Setting your intentions
              </p>
            </div>

            <div className="group">
              <div className="relative bg-gradient-to-br from-white to-gray-50 rounded-3xl p-4 shadow-2xl hover:shadow-3xl transition-all duration-300 hover:scale-105">
                <div className="absolute inset-0 bg-gradient-to-br from-blue-100/20 to-indigo-100/20 rounded-3xl"></div>
                <div className="relative">
                  <Image
                    src="/images/screenshot_usage_page.jpg"
                    alt="Mindful insights"
                    width={300}
                    height={600}
                    className="w-full h-auto rounded-2xl shadow-lg"
                  />
                </div>
              </div>
              <p className="text-center text-amber-700 font-medium mt-6">
                Gentle insights
              </p>
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
            We believe in gentle awareness over harsh restrictions. Your
            capybara sanctuary grows more beautiful as you find balance,
            creating a positive feedback loop that feels natural and
            sustainable.
          </p>

          <div className="grid md:grid-cols-2 gap-8 text-left">
            <div className="space-y-4">
              <h3 className="text-xl font-serif text-amber-900">
                No shame, only growth
              </h3>
              <p className="text-amber-700">
                We don&apos;t believe in digital detox extremes. Instead, we
                help you build a healthier relationship with technology through
                mindful awareness.
              </p>
            </div>

            <div className="space-y-4">
              <h3 className="text-xl font-serif text-amber-900">
                Emotional intelligence
              </h3>
              <p className="text-amber-700">
                Your capybaras respond to your digital habits with empathy, not
                judgment. They&apos;re here to support your journey, not make
                you feel guilty.
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

          {/* Values Section */}
          <section className="py-8 md:py-12 bg-white/30">
            <div className="max-w-3xl mx-auto px-6 md:px-8 text-center">
              <h2 className="text-2xl md:text-3xl font-serif text-amber-900 mb-4">
                Open source • Privacy-first • No ads
              </h2>

              <p className="text-amber-700 leading-relaxed">
                Built with 0 coding skills but lots of heart. All data stays on
                your device. Help and suggestions welcome.
              </p>
            </div>
          </section>
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
                A gentle approach to digital wellness, inspired by the peaceful
                nature of capybaras.
              </p>
            </div>

            <div className="space-y-4">
              <h3 className="font-serif text-lg">Connect</h3>
              <div className="space-y-2">
                <p className="text-amber-200">
                  Github:{" "}
                  <Link href="https://github.com/zodwick/Capibara_android">
                    zodwick/Capibara_android
                  </Link>
                </p>
                <p className="text-amber-200">
                  Made with 🤍 for mindful living
                </p>
              </div>
            </div>

            <div className="space-y-4">
              <h3 className="font-serif text-lg">Philosophy</h3>
              <p className="text-amber-200">
                &ldquo;In a world of endless notifications, sometimes the most
                radical act is simply being present.&rdquo;
              </p>
            </div>
          </div>

          <div className="border-t border-amber-800 mt-8 pt-8 text-center text-amber-300">
            <p>
              &copy; 2024 Capybara Sanctuary. Made for humans seeking digital
              peace.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}
