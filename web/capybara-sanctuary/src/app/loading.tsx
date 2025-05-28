import Image from "next/image";

export default function Loading() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-amber-50 via-orange-50 to-rose-50 flex items-center justify-center">
      <div className="text-center">
        <div className="mb-8 animate-float">
          <Image
            src="/images/capybara_cartoon_style_sitting_happy_drinking_boba_tea_with_hearts_facing_forward_solo.png"
            alt="Happy capybara"
            width={120}
            height={120}
            className="mx-auto capybara-image"
          />
        </div>
        
        <h2 className="text-2xl font-serif text-amber-900 mb-4">
          Preparing your sanctuary...
        </h2>
        
        <div className="flex justify-center space-x-2">
          <div className="w-2 h-2 bg-amber-600 rounded-full animate-bounce"></div>
          <div className="w-2 h-2 bg-amber-600 rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
          <div className="w-2 h-2 bg-amber-600 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
        </div>
      </div>
    </div>
  );
} 